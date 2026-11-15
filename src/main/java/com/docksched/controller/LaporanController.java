package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.Dock;
import com.docksched.model.StatusProses;
import com.docksched.model.Truck;
import com.docksched.util.AplikasiException;
import com.docksched.util.Konstanta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Dashboard status truck & dock serta rekap performa (UC-12, UC-13; DS-F-05.1 - 05.4). */
public class LaporanController {

    /** Dock beserta truck yang sedang menempatinya. */
    public record StatusDockTampil(Dock dock, String nomorPolisi, StatusProses statusTruck, long durasiDetik,
            boolean melebihi) {
    }

    public record DataDashboard(LocalDateTime waktu, List<Truck> trucks, List<StatusDockTampil> docks) {

        public long hitung(StatusProses s) {
            return trucks.stream().filter(t -> t.getStatusProses() == s).count();
        }

        public long jumlahMelebihi() {
            return trucks.stream().filter(t -> t.getStatusProses() != StatusProses.SELESAI_BERANGKAT
                    && t.melebihiTarget(waktu)).count();
        }
    }

    public record BarisHarian(LocalDate tanggal, int terdaftar, int selesai, double rataDurasiMenit, int terlambat) {
    }

    public record BarisTahap(String tahap, double rataMenit, int terlambatTerpanjang) {
    }

    public record BarisUtilisasi(String kodeDock, String jenis, double terpakaiMenit, double persen) {
    }

    public record Rekap(LocalDate dari, LocalDate sampai, LocalDateTime dibuat, int totalTerdaftar, int totalSelesai,
            double rataDurasiProsesMenit, double rataMenungguMenit, int terlambat, double persenTerlambat,
            List<BarisHarian> harian, List<BarisTahap> tahap, List<BarisUtilisasi> utilisasi) {
    }

    private record Interval(int idDock, LocalDateTime mulai, LocalDateTime selesai) {
    }

    private static final String SQL_SELESAI = "SELECT t.id_truck, t.waktu_pendaftaran, "
            + "MIN(CASE WHEN l.status_proses = 'PARKIR_DOCK' THEN l.waktu END) AS waktu_parkir, "
            + "MAX(CASE WHEN l.status_proses = 'SELESAI_BERANGKAT' THEN l.waktu END) AS waktu_selesai, "
            + "MAX(CASE WHEN l.status_proses = 'MENUNGGU' THEN l.durasi_tahap_detik END) AS d_tunggu, "
            + "MAX(CASE WHEN l.status_proses = 'PARKIR_DOCK' THEN l.durasi_tahap_detik END) AS d_parkir, "
            + "MAX(CASE WHEN l.status_proses = 'LOADING_UNLOADING' THEN l.durasi_tahap_detik END) AS d_load, "
            + "MAX(CASE WHEN l.status_proses = 'ADMINISTRASI' THEN l.durasi_tahap_detik END) AS d_admin "
            + "FROM truck t JOIN log_status l ON l.id_truck = t.id_truck "
            + "GROUP BY t.id_truck, t.waktu_pendaftaran "
            + "HAVING MAX(CASE WHEN l.status_proses = 'SELESAI_BERANGKAT' THEN l.waktu END) >= ? "
            + "AND MAX(CASE WHEN l.status_proses = 'SELESAI_BERANGKAT' THEN l.waktu END) < ?";

    private static final String SQL_OKUPANSI = "SELECT j.id_dock, "
            + "(SELECT MIN(l.waktu) FROM log_status l WHERE l.id_truck = j.id_truck AND l.status_proses = 'PARKIR_DOCK') AS parkir, "
            + "(SELECT MAX(l.waktu) FROM log_status l WHERE l.id_truck = j.id_truck AND l.status_proses = 'SELESAI_BERANGKAT') AS selesai "
            + "FROM jadwal j WHERE j.status IN ('AKTIF', 'SELESAI')";

    private final TruckController truckController = new TruckController();
    private final DockController dockController = new DockController();

    /** Status terkini semua truck dan dock; durasi dibandingkan dengan target 45 menit (DS-F-05.1, 05.2). */
    public DataDashboard ambilDashboard() throws SQLException {
        LocalDateTime sekarang = LocalDateTime.now();
        List<Truck> trucks = truckController.daftarDashboard(sekarang);
        List<Dock> docks = dockController.daftar();
        List<StatusDockTampil> tampil = new ArrayList<>();
        for (Dock d : docks) {
            Truck penghuni = null;
            for (Truck t : trucks) {
                boolean diDock = t.getStatusProses() == StatusProses.PARKIR_DOCK
                        || t.getStatusProses() == StatusProses.LOADING_UNLOADING
                        || t.getStatusProses() == StatusProses.ADMINISTRASI;
                if (diDock && d.getKodeDock().equals(t.getKodeDock())) {
                    penghuni = t;
                    break;
                }
            }
            if (penghuni == null) {
                tampil.add(new StatusDockTampil(d, null, null, -1, false));
            } else {
                tampil.add(new StatusDockTampil(d, penghuni.getNomorPolisi(), penghuni.getStatusProses(),
                        penghuni.durasiProsesDetik(sekarang), penghuni.melebihiTarget(sekarang)));
            }
        }
        return new DataDashboard(sekarang, trucks, tampil);
    }

    /** Rekap performa untuk rentang tanggal [dari, sampai] (DS-F-05.3, 05.4). */
    public Rekap rekap(LocalDate dari, LocalDate sampai) throws SQLException {
        if (dari == null || sampai == null) {
            throw new AplikasiException("Rentang tanggal wajib diisi.");
        }
        if (sampai.isBefore(dari)) {
            throw new AplikasiException("Tanggal akhir tidak boleh lebih awal dari tanggal awal.");
        }
        if (ChronoUnit.DAYS.between(dari, sampai) > 366) {
            throw new AplikasiException("Rentang tanggal maksimal 1 tahun.");
        }
        LocalDateTime mulai = dari.atStartOfDay();
        LocalDateTime akhir = sampai.plusDays(1).atStartOfDay();
        LocalDateTime sekarang = LocalDateTime.now();
        final long batas = Konstanta.TARGET_MENIT * 60L;

        Map<LocalDate, int[]> hari = new LinkedHashMap<>(); // [terdaftar, selesai, terlambat]
        Map<LocalDate, Double> jumlahDurasiHari = new LinkedHashMap<>();
        for (LocalDate d = dari; !d.isAfter(sampai); d = d.plusDays(1)) {
            hari.put(d, new int[3]);
            jumlahDurasiHari.put(d, 0.0);
        }

        int totalTerdaftar = 0;
        int selesai = 0;
        int terlambat = 0;
        double jumlahProses = 0;
        int nProses = 0;
        double jumlahTunggu = 0;
        int nTunggu = 0;
        double[] jumlahTahap = new double[3];
        int[] nTahap = new int[3];
        int[] terpanjang = new int[3];

        List<Interval> okupansi = new ArrayList<>();
        List<Dock> docks;
        try (Connection c = Koneksi.buka()) {
            docks = dockController.daftar(c);
            try (PreparedStatement ps = c.prepareStatement("SELECT waktu_pendaftaran FROM truck "
                    + "WHERE waktu_pendaftaran >= ? AND waktu_pendaftaran < ?")) {
                ps.setTimestamp(1, Timestamp.valueOf(mulai));
                ps.setTimestamp(2, Timestamp.valueOf(akhir));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LocalDate tgl = rs.getTimestamp(1).toLocalDateTime().toLocalDate();
                        int[] h = hari.get(tgl);
                        if (h != null) {
                            h[0]++;
                            totalTerdaftar++;
                        }
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(SQL_SELESAI)) {
                ps.setTimestamp(1, Timestamp.valueOf(mulai));
                ps.setTimestamp(2, Timestamp.valueOf(akhir));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Timestamp tsSelesai = rs.getTimestamp("waktu_selesai");
                        Timestamp tsParkir = rs.getTimestamp("waktu_parkir");
                        if (tsSelesai == null) {
                            continue;
                        }
                        LocalDate tgl = tsSelesai.toLocalDateTime().toLocalDate();
                        int[] h = hari.get(tgl);
                        if (h == null) {
                            continue;
                        }
                        selesai++;
                        h[1]++;
                        double tunggu = detik(rs, "d_tunggu");
                        if (tunggu >= 0) {
                            jumlahTunggu += tunggu;
                            nTunggu++;
                        }
                        double[] tahap = {detik(rs, "d_parkir"), detik(rs, "d_load"), detik(rs, "d_admin")};
                        for (int i = 0; i < 3; i++) {
                            if (tahap[i] >= 0) {
                                jumlahTahap[i] += tahap[i];
                                nTahap[i]++;
                            }
                        }
                        if (tsParkir != null) {
                            long total = Math.max(0, Duration.between(tsParkir.toLocalDateTime(),
                                    tsSelesai.toLocalDateTime()).getSeconds());
                            jumlahProses += total;
                            nProses++;
                            jumlahDurasiHari.merge(tgl, (double) total, Double::sum);
                            if (total > batas) {
                                terlambat++;
                                h[2]++;
                                int idx = 0;
                                for (int i = 1; i < 3; i++) {
                                    if (tahap[i] > tahap[idx]) {
                                        idx = i;
                                    }
                                }
                                terpanjang[idx]++;
                            }
                        }
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(SQL_OKUPANSI); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp tp = rs.getTimestamp("parkir");
                    if (tp == null) {
                        continue;
                    }
                    Timestamp ts = rs.getTimestamp("selesai");
                    okupansi.add(new Interval(rs.getInt("id_dock"), tp.toLocalDateTime(),
                            ts == null ? sekarang : ts.toLocalDateTime()));
                }
            }
        }

        List<BarisHarian> harian = new ArrayList<>();
        for (Map.Entry<LocalDate, int[]> e : hari.entrySet()) {
            int[] h = e.getValue();
            double rata = h[1] == 0 ? 0 : jumlahDurasiHari.get(e.getKey()) / h[1] / 60.0;
            harian.add(new BarisHarian(e.getKey(), h[0], h[1], rata, h[2]));
        }
        String[] namaTahap = {StatusProses.PARKIR_DOCK.getLabel(), StatusProses.LOADING_UNLOADING.getLabel(),
            StatusProses.ADMINISTRASI.getLabel()};
        List<BarisTahap> tahapList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tahapList.add(new BarisTahap(namaTahap[i], nTahap[i] == 0 ? 0 : jumlahTahap[i] / nTahap[i] / 60.0,
                    terpanjang[i]));
        }

        LocalDateTime akhirJendela = akhir.isAfter(sekarang) ? sekarang : akhir;
        double menitJendela = akhirJendela.isAfter(mulai) ? Duration.between(mulai, akhirJendela).toSeconds() / 60.0 : 0;
        List<BarisUtilisasi> utilisasi = new ArrayList<>();
        for (Dock d : docks) {
            double terpakai = 0;
            for (Interval iv : okupansi) {
                if (iv.idDock() != d.getIdDock()) {
                    continue;
                }
                LocalDateTime a = iv.mulai().isAfter(mulai) ? iv.mulai() : mulai;
                LocalDateTime b = iv.selesai().isBefore(akhirJendela) ? iv.selesai() : akhirJendela;
                if (b.isAfter(a)) {
                    terpakai += Duration.between(a, b).toSeconds() / 60.0;
                }
            }
            double persen = menitJendela <= 0 ? 0 : Math.min(100.0, terpakai / menitJendela * 100.0);
            utilisasi.add(new BarisUtilisasi(d.getKodeDock(), d.getKeteranganJenis(), terpakai, persen));
        }

        double rataProses = nProses == 0 ? 0 : jumlahProses / nProses / 60.0;
        double rataTunggu = nTunggu == 0 ? 0 : jumlahTunggu / nTunggu / 60.0;
        double persenTerlambat = nProses == 0 ? 0 : terlambat * 100.0 / nProses;
        return new Rekap(dari, sampai, sekarang, totalTerdaftar, selesai, rataProses, rataTunggu, terlambat,
                persenTerlambat, harian, tahapList, utilisasi);
    }

    private static double detik(ResultSet rs, String kolom) throws SQLException {
        long nilai = rs.getLong(kolom);
        return rs.wasNull() ? -1 : nilai;
    }

    /** Menyusun dokumen laporan (untuk ekspor PDF/Excel) dari hasil rekap. */
    public static LaporanDokumen buatDokumen(Rekap r, String dibuatOleh) {
        LaporanDokumen d = new LaporanDokumen("Laporan Performa Penjadwalan DOCK-SCHED",
                "Periode " + r.dari().format(Konstanta.FMT_TANGGAL) + " s.d. " + r.sampai().format(Konstanta.FMT_TANGGAL),
                "Dibuat " + r.dibuat().format(Konstanta.FMT_WAKTU) + " oleh " + dibuatOleh);

        LaporanDokumen.Bagian ringkas = d.tambahBagian("Ringkasan", "Indikator", "Nilai");
        ringkas.baris("Truck terdaftar pada periode", r.totalTerdaftar());
        ringkas.baris("Truck selesai diproses", r.totalSelesai());
        ringkas.baris("Rata-rata durasi proses (menit, sejak parkir di dock)", bulat(r.rataDurasiProsesMenit()));
        ringkas.baris("Rata-rata waktu menunggu sebelum parkir (menit)", bulat(r.rataMenungguMenit()));
        ringkas.baris("Truck melebihi target " + Konstanta.TARGET_MENIT + " menit", r.terlambat());
        ringkas.baris("Tingkat keterlambatan (%)", bulat(r.persenTerlambat()));

        LaporanDokumen.Bagian harian = d.tambahBagian("Rekap per hari", "Tanggal", "Terdaftar", "Selesai",
                "Rata-rata durasi (menit)", "Melebihi 45 menit");
        for (BarisHarian h : r.harian()) {
            harian.baris(h.tanggal().format(Konstanta.FMT_TANGGAL), h.terdaftar(), h.selesai(),
                    bulat(h.rataDurasiMenit()), h.terlambat());
        }

        LaporanDokumen.Bagian tahap = d.tambahBagian("Rata-rata durasi per tahap", "Tahap", "Rata-rata (menit)",
                "Tahap terpanjang pada truck terlambat");
        for (BarisTahap t : r.tahap()) {
            tahap.baris(t.tahap(), bulat(t.rataMenit()), t.terlambatTerpanjang());
        }

        LaporanDokumen.Bagian util = d.tambahBagian("Utilisasi dock", "Dock", "Jenis", "Terpakai (menit)",
                "Utilisasi (%)");
        for (BarisUtilisasi u : r.utilisasi()) {
            util.baris(u.kodeDock(), u.jenis(), bulat(u.terpakaiMenit()), bulat(u.persen()));
        }
        return d;
    }

    private static double bulat(double nilai) {
        return Math.round(nilai * 10.0) / 10.0;
    }
}
