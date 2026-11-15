package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.Dock;
import com.docksched.model.Jadwal;
import com.docksched.model.JenisJadwal;
import com.docksched.model.JenisMuatan;
import com.docksched.model.PengaturanSpk;
import com.docksched.model.Pengguna;
import com.docksched.model.StatusJadwal;
import com.docksched.model.StatusProses;
import com.docksched.model.Truck;
import com.docksched.util.AplikasiException;
import com.docksched.util.Konstanta;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Penjadwalan slot dock dengan SAW dan penjadwalan ulang manual (UC-05 - UC-08;
 * DS-F-03.1 - 03.6).
 */
public class PenjadwalanController {

    /** Hasil satu kali proses penjadwalan. */
    public record HasilPenjadwalan(LocalDateTime waktu, List<SawCalculator.HasilSaw> saw,
            List<SlotAllocator.Alokasi> alokasi, int jumlahDijadwalkan, int jumlahTanpaDock,
            int jumlahManualDipertahankan) {
    }

    private static final String SELECT_JADWAL = "SELECT j.id_jadwal, j.id_truck, j.id_dock, j.skor_prioritas, j.urutan, "
            + "j.estimasi_waktu_masuk, j.estimasi_waktu_selesai, j.waktu_keputusan, j.jenis, j.status, j.id_pengguna, "
            + "j.keterangan, t.nomor_polisi, t.nama_sopir, t.jenis_muatan, t.status_proses, d.kode_dock "
            + "FROM jadwal j JOIN truck t ON t.id_truck = j.id_truck JOIN dock d ON d.id_dock = j.id_dock ";

    private final DockController dockController = new DockController();
    private final PengaturanController pengaturanController = new PengaturanController();

    /**
     * Menghitung skor SAW untuk semua truck "Menunggu", mengurutkan, mengalokasikan
     * dock, dan mencatat hasilnya
     * (truck, dock, waktu keputusan). Jadwal manual Admin dipertahankan. Dijalankan
     * dalam satu transaksi.
     */
    public HasilPenjadwalan jalankan(Pengguna user) throws SQLException {
        LocalDateTime sekarang = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                kunciPenjadwalan(c);
                List<Dock> docks = dockController.daftar(c);
                PengaturanSpk pengaturan = pengaturanController.ambil(c);
                List<Truck> trucks = trukMenunggu(c);
                int manual = hitungManualAktif(c);
                Map<Integer, List<SlotAllocator.Interval>> sibuk = rentangSibuk(c, sekarang);

                List<SawCalculator.HasilSaw> saw = SawCalculator.hitung(trucks, docks, pengaturan);
                List<SlotAllocator.Alokasi> alokasi = SlotAllocator.alokasikan(saw, docks, sibuk, sekarang);

                int dijadwalkan = 0;
                int tanpaDock = 0;
                for (SlotAllocator.Alokasi a : alokasi) {
                    int idTruck = a.saw().truck().getIdTruck();
                    try (PreparedStatement ps = c.prepareStatement("UPDATE jadwal SET status = 'DIGANTI', "
                            + "keterangan = 'Diganti oleh penjadwalan baru' WHERE id_truck = ? AND status = 'AKTIF'")) {
                        ps.setInt(1, idTruck);
                        ps.executeUpdate();
                    }
                    if (a.dock() == null) {
                        tanpaDock++;
                        continue;
                    }
                    String sql = "INSERT INTO jadwal (id_truck, id_dock, skor_prioritas, urutan, estimasi_waktu_masuk, "
                            + "estimasi_waktu_selesai, waktu_keputusan, jenis, status, id_pengguna, keterangan) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, 'OTOMATIS', 'AKTIF', ?, NULL)";
                    try (PreparedStatement ps = c.prepareStatement(sql)) {
                        ps.setInt(1, idTruck);
                        ps.setInt(2, a.dock().getIdDock());
                        ps.setBigDecimal(3, BigDecimal.valueOf(a.saw().skor()).setScale(4, RoundingMode.HALF_UP));
                        ps.setInt(4, a.saw().urutan());
                        ps.setTimestamp(5, Timestamp.valueOf(a.masuk()));
                        ps.setTimestamp(6, Timestamp.valueOf(a.selesai()));
                        ps.setTimestamp(7, Timestamp.valueOf(sekarang));
                        ps.setInt(8, user.getIdPengguna());
                        ps.executeUpdate();
                    }
                    dijadwalkan++;
                }
                Audit.catat(c, user.getIdPengguna(), "PENJADWALAN", "SAW: " + trucks.size() + " truck diproses, "
                        + dijadwalkan + " dijadwalkan, " + tanpaDock + " tanpa dock, " + manual
                        + " jadwal manual dipertahankan");
                c.commit();
                return new HasilPenjadwalan(sekarang, saw, alokasi, dijadwalkan, tanpaDock, manual);
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Mutex antar pengguna: proses penjadwalan tidak boleh berjalan bersamaan. */
    static void kunciPenjadwalan(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT nilai FROM pengaturan WHERE kunci = 'bobot_muatan' FOR UPDATE")) {
            ps.executeQuery().close();
        }
    }

    /** Truck "Menunggu" yang dihitung ulang (kecuali yang jadwalnya diatur manual oleh Admin). */
    private List<Truck> trukMenunggu(Connection c) throws SQLException {
        String sql = TruckController.SELECT_TRUCK + "WHERE t.status_proses = 'MENUNGGU' AND t.diarsipkan = 0 "
                + "AND NOT EXISTS (SELECT 1 FROM jadwal j WHERE j.id_truck = t.id_truck AND j.status = 'AKTIF' "
                + "AND j.jenis = 'MANUAL') ORDER BY t.perkiraan_waktu_kedatangan, t.id_truck";
        List<Truck> hasil = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(TruckController.petakan(rs));
            }
        }
        return hasil;
    }

    private int hitungManualAktif(Connection c) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jadwal j JOIN truck t ON t.id_truck = j.id_truck "
                + "WHERE j.status = 'AKTIF' AND j.jenis = 'MANUAL' AND t.status_proses = 'MENUNGGU' AND t.diarsipkan = 0";
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Rentang sibuk dock: truck yang sedang diproses (sampai estimasi selesai) dan jadwal manual yang dipertahankan. */
    private Map<Integer, List<SlotAllocator.Interval>> rentangSibuk(Connection c, LocalDateTime sekarang)
            throws SQLException {
        Map<Integer, List<SlotAllocator.Interval>> peta = new HashMap<>();
        String sql = "SELECT j.id_dock, j.estimasi_waktu_masuk, j.estimasi_waktu_selesai, t.status_proses, j.jenis "
                + "FROM jadwal j JOIN truck t ON t.id_truck = j.id_truck WHERE j.status = 'AKTIF' "
                + "AND (t.status_proses IN ('PARKIR_DOCK','LOADING_UNLOADING','ADMINISTRASI') "
                + "OR (t.status_proses = 'MENUNGGU' AND j.jenis = 'MANUAL' AND t.diarsipkan = 0))";
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDateTime masuk = rs.getTimestamp("estimasi_waktu_masuk").toLocalDateTime();
                LocalDateTime selesai = rs.getTimestamp("estimasi_waktu_selesai").toLocalDateTime();
                StatusProses status = StatusProses.valueOf(rs.getString("status_proses"));
                SlotAllocator.Interval iv;
                if (status == StatusProses.MENUNGGU) {
                    iv = new SlotAllocator.Interval(masuk, selesai);
                } else {
                    LocalDateTime minimal = sekarang.plusMinutes(Konstanta.SISA_MELEWATI_ESTIMASI_MENIT);
                    iv = new SlotAllocator.Interval(sekarang, selesai.isAfter(minimal) ? selesai : minimal);
                }
                peta.computeIfAbsent(rs.getInt("id_dock"), k -> new ArrayList<>()).add(iv);
            }
        }
        return peta;
    }

    private static Jadwal petakan(ResultSet rs) throws SQLException {
        Jadwal j = new Jadwal();
        j.setIdJadwal(rs.getInt("id_jadwal"));
        j.setIdTruck(rs.getInt("id_truck"));
        j.setIdDock(rs.getInt("id_dock"));
        j.setSkorPrioritas(rs.getDouble("skor_prioritas"));
        j.setUrutan(rs.getInt("urutan"));
        j.setEstimasiWaktuMasuk(rs.getTimestamp("estimasi_waktu_masuk").toLocalDateTime());
        j.setEstimasiWaktuSelesai(rs.getTimestamp("estimasi_waktu_selesai").toLocalDateTime());
        j.setWaktuKeputusan(rs.getTimestamp("waktu_keputusan").toLocalDateTime());
        j.setJenis(JenisJadwal.valueOf(rs.getString("jenis")));
        j.setStatus(StatusJadwal.valueOf(rs.getString("status")));
        int idPengguna = rs.getInt("id_pengguna");
        j.setIdPengguna(rs.wasNull() ? null : idPengguna);
        j.setKeterangan(rs.getString("keterangan"));
        j.setNomorPolisi(rs.getString("nomor_polisi"));
        j.setNamaSopir(rs.getString("nama_sopir"));
        j.setJenisMuatan(JenisMuatan.valueOf(rs.getString("jenis_muatan")));
        j.setStatusTruck(StatusProses.valueOf(rs.getString("status_proses")));
        j.setKodeDock(rs.getString("kode_dock"));
        return j;
    }

    /**
     * Jadwal aktif untuk truck yang belum selesai, urut berdasarkan estimasi masuk.
     */
    public List<Jadwal> daftarAktif() throws SQLException {
        String sql = SELECT_JADWAL + "WHERE j.status = 'AKTIF' AND t.status_proses <> 'SELESAI_BERANGKAT' "
                + "AND t.diarsipkan = 0 ORDER BY j.estimasi_waktu_masuk, j.urutan, j.id_jadwal";
        List<Jadwal> hasil = new ArrayList<>();
        try (Connection c = Koneksi.buka();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        }
        return hasil;
    }

    /** Dock yang boleh dipilih untuk menjadwalkan ulang sebuah truck (sesuai jenis dan tidak rusak). */
    public List<Dock> dockUntukJadwalUlang(JenisMuatan jenis) throws SQLException {
        List<Dock> hasil = new ArrayList<>();
        for (Dock d : dockController.daftar()) {
            if (d.berfungsi() && d.cocokUntuk(jenis)) {
                hasil.add(d);
            }
        }
        return hasil;
    }

    /**
     * Menjadwalkan ulang manual (UC-08, DS-F-03.5): pindah dock dan/atau waktu
     * masuk. Hanya Admin Gudang,
     * hanya untuk truck "Menunggu", tanpa bentrok dengan jadwal lain.
     */
    public void jadwalUlang(int idJadwal, int idDockBaru, LocalDateTime masukBaru, String keterangan, Pengguna admin)
            throws SQLException {
        if (!admin.isProgrammer() && !admin.getPeran().bisaJadwalUlang()) {
            throw new AplikasiException("Hanya Admin Gudang yang dapat menjadwalkan ulang.");
        }
        if (masukBaru == null) {
            throw new AplikasiException("Waktu masuk baru wajib diisi.");
        }
        if (masukBaru.isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
            throw new AplikasiException("Waktu masuk baru tidak boleh di masa lalu.");
        }
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                kunciPenjadwalan(c);
                Jadwal lama = null;
                try (PreparedStatement ps = c.prepareStatement(SELECT_JADWAL + "WHERE j.id_jadwal = ?")) {
                    ps.setInt(1, idJadwal);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            lama = petakan(rs);
                        }
                    }
                }
                if (lama == null || lama.getStatus() != StatusJadwal.AKTIF) {
                    throw new AplikasiException("Jadwal tidak ditemukan atau sudah tidak aktif. Segarkan tampilan.");
                }
                if (lama.getStatusTruck() != StatusProses.MENUNGGU) {
                    throw new AplikasiException("Truck " + lama.getNomorPolisi() + " sudah berstatus "
                            + lama.getStatusTruck().getLabel() + " dan tidak dapat dijadwalkan ulang.");
                }
                Dock dock = null;
                for (Dock d : dockController.daftar(c)) {
                    if (d.getIdDock() == idDockBaru) {
                        dock = d;
                    }
                }
                if (dock == null) {
                    throw new AplikasiException("Dock tidak ditemukan.");
                }
                if (!dock.berfungsi()) {
                    throw new AplikasiException("Dock " + dock.getKodeDock() + " sedang rusak.");
                }
                if (!dock.cocokUntuk(lama.getJenisMuatan())) {
                    throw new AplikasiException("Dock " + dock.getKodeDock() + " tidak sesuai untuk muatan "
                            + lama.getJenisMuatan().getLabel() + ".");
                }
                int durasiMenit;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT estimasi_durasi_menit FROM truck WHERE id_truck = ?")) {
                    ps.setInt(1, lama.getIdTruck());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        durasiMenit = rs.getInt(1);
                    }
                }
                LocalDateTime selesaiBaru = masukBaru.plus(Duration.ofMinutes(durasiMenit));
                String bentrok = cariBentrok(c, idDockBaru, idJadwal, masukBaru, selesaiBaru);
                if (bentrok != null) {
                    throw new AplikasiException("Slot bentrok dengan jadwal truck " + bentrok + " pada dock "
                            + dock.getKodeDock() + ". Pilih waktu atau dock lain.");
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE jadwal SET status = 'DIGANTI', "
                        + "keterangan = 'Dijadwalkan ulang manual' WHERE id_jadwal = ?")) {
                    ps.setInt(1, idJadwal);
                    ps.executeUpdate();
                }
                String sql = "INSERT INTO jadwal (id_truck, id_dock, skor_prioritas, urutan, estimasi_waktu_masuk, "
                        + "estimasi_waktu_selesai, waktu_keputusan, jenis, status, id_pengguna, keterangan) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, 'MANUAL', 'AKTIF', ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, lama.getIdTruck());
                    ps.setInt(2, idDockBaru);
                    ps.setBigDecimal(3, BigDecimal.valueOf(lama.getSkorPrioritas()).setScale(4, RoundingMode.HALF_UP));
                    ps.setInt(4, lama.getUrutan());
                    ps.setTimestamp(5, Timestamp.valueOf(masukBaru));
                    ps.setTimestamp(6, Timestamp.valueOf(selesaiBaru));
                    ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setInt(8, admin.getIdPengguna());
                    if (keterangan == null || keterangan.trim().isEmpty()) {
                        ps.setNull(9, Types.VARCHAR);
                    } else {
                        String k = keterangan.trim();
                        ps.setString(9, k.length() > 250 ? k.substring(0, 250) : k);
                    }
                    ps.executeUpdate();
                }
                Audit.catat(c, admin.getIdPengguna(), "JADWAL_ULANG", "Truck " + lama.getNomorPolisi() + ": "
                        + lama.getKodeDock() + " -> " + dock.getKodeDock() + ", masuk "
                        + masukBaru.format(Konstanta.FMT_WAKTU)
                        + (keterangan == null || keterangan.trim().isEmpty() ? "" : ". Alasan: " + keterangan.trim()));
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /**
     * Truck batal (UC-08): jadwal dibatalkan dan truck diarsipkan sehingga keluar
     * dari antrean.
     * Datanya tetap tersimpan (tidak dihapus permanen, DS-NF-04).
     */
    public void batalkanTruck(int idJadwal, String keterangan, Pengguna admin) throws SQLException {
        if (!admin.isProgrammer() && !admin.getPeran().bisaJadwalUlang()) {
            throw new AplikasiException("Hanya Admin Gudang yang dapat membatalkan jadwal.");
        }
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                kunciPenjadwalan(c);
                Jadwal j = null;
                try (PreparedStatement ps = c.prepareStatement(SELECT_JADWAL + "WHERE j.id_jadwal = ? FOR UPDATE")) {
                    ps.setInt(1, idJadwal);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            j = petakan(rs);
                        }
                    }
                }
                if (j == null || j.getStatus() != StatusJadwal.AKTIF) {
                    throw new AplikasiException("Jadwal tidak ditemukan atau sudah tidak aktif. Segarkan tampilan.");
                }
                if (j.getStatusTruck() != StatusProses.MENUNGGU) {
                    throw new AplikasiException("Truck " + j.getNomorPolisi() + " sudah berstatus "
                            + j.getStatusTruck().getLabel() + " dan tidak dapat dibatalkan.");
                }
                String alasan = keterangan == null || keterangan.trim().isEmpty() ? "Truck batal" : keterangan.trim();
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE jadwal SET status = 'BATAL', keterangan = ? WHERE id_jadwal = ?")) {
                    ps.setString(1, alasan.length() > 250 ? alasan.substring(0, 250) : alasan);
                    ps.setInt(2, idJadwal);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE truck SET diarsipkan = 1 WHERE id_truck = ?")) {
                    ps.setInt(1, j.getIdTruck());
                    ps.executeUpdate();
                }
                Audit.catat(c, admin.getIdPengguna(), "BATAL_TRUCK", "Truck " + j.getNomorPolisi() + " dibatalkan. "
                        + alasan);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private String cariBentrok(Connection c, int idDock, int kecualiJadwal, LocalDateTime masuk, LocalDateTime selesai)
            throws SQLException {
        String sql = "SELECT t.nomor_polisi FROM jadwal j JOIN truck t ON t.id_truck = j.id_truck "
                + "WHERE j.id_dock = ? AND j.status = 'AKTIF' AND j.id_jadwal <> ? "
                + "AND j.estimasi_waktu_masuk < ? AND j.estimasi_waktu_selesai > ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDock);
            ps.setInt(2, kecualiJadwal);
            ps.setTimestamp(3, Timestamp.valueOf(selesai));
            ps.setTimestamp(4, Timestamp.valueOf(masuk));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /**
     * Teks estimasi jadwal untuk diteruskan ke sopir (UC-07). Cara penyampaian ke
     * sopir masih TBD-2 di SRS,
     * sehingga aplikasi hanya menyediakan teks yang dapat disalin.
     */
    public static String teksInformasiSopir(List<Jadwal> daftar) {
        StringBuilder sb = new StringBuilder();
        for (Jadwal j : daftar) {
            sb.append(j.getNomorPolisi()).append(" (").append(j.getNamaSopir()).append("): masuk ")
                    .append(j.getKodeDock()).append(" pukul ")
                    .append(j.getEstimasiWaktuMasuk().format(Konstanta.FMT_WAKTU))
                    .append(", estimasi selesai ").append(j.getEstimasiWaktuSelesai().format(Konstanta.FMT_JAM))
                    .append('\n');
        }
        return sb.toString();
    }
}
