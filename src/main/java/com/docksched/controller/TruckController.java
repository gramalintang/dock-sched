package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.JenisMuatan;
import com.docksched.model.Pengguna;
import com.docksched.model.StatusProses;
import com.docksched.model.Truck;
import com.docksched.util.AplikasiException;
import com.docksched.util.Validator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Input, pencarian, dan penyuntingan data truck (UC-02, UC-03, UC-04; DS-F-02.1 - 02.5). */
public class TruckController {

    /** Kolom dasar truck beserta data turunan (waktu parkir/selesai, dock aktif). Kompatibel MySQL 5.7/8.0. */
    static final String SELECT_TRUCK = "SELECT t.id_truck, t.nomor_polisi, t.nama_sopir, t.jenis_muatan, "
            + "t.pemasok_pelanggan, t.perkiraan_waktu_kedatangan, t.estimasi_durasi_menit, t.status_proses, "
            + "t.waktu_pendaftaran, t.diarsipkan, "
            + "(SELECT MIN(l.waktu) FROM log_status l WHERE l.id_truck = t.id_truck AND l.status_proses = 'PARKIR_DOCK') AS waktu_parkir, "
            + "(SELECT MAX(l.waktu) FROM log_status l WHERE l.id_truck = t.id_truck AND l.status_proses = 'SELESAI_BERANGKAT') AS waktu_selesai, "
            + "(SELECT MAX(l.waktu) FROM log_status l WHERE l.id_truck = t.id_truck) AS waktu_tahap, "
            + "(SELECT d.kode_dock FROM jadwal j JOIN dock d ON d.id_dock = j.id_dock "
            + "  WHERE j.id_truck = t.id_truck AND j.status IN ('AKTIF','SELESAI') ORDER BY j.id_jadwal DESC LIMIT 1) AS kode_dock "
            + "FROM truck t ";

    static Truck petakan(ResultSet rs) throws SQLException {
        Truck t = new Truck();
        t.setIdTruck(rs.getInt("id_truck"));
        t.setNomorPolisi(rs.getString("nomor_polisi"));
        t.setNamaSopir(rs.getString("nama_sopir"));
        t.setJenisMuatan(JenisMuatan.valueOf(rs.getString("jenis_muatan")));
        t.setPemasokPelanggan(rs.getString("pemasok_pelanggan"));
        t.setPerkiraanKedatangan(waktu(rs.getTimestamp("perkiraan_waktu_kedatangan")));
        t.setEstimasiDurasiMenit(rs.getInt("estimasi_durasi_menit"));
        t.setStatusProses(StatusProses.valueOf(rs.getString("status_proses")));
        t.setWaktuPendaftaran(waktu(rs.getTimestamp("waktu_pendaftaran")));
        t.setDiarsipkan(rs.getBoolean("diarsipkan"));
        t.setWaktuParkir(waktu(rs.getTimestamp("waktu_parkir")));
        t.setWaktuSelesai(waktu(rs.getTimestamp("waktu_selesai")));
        t.setWaktuTahapSekarang(waktu(rs.getTimestamp("waktu_tahap")));
        t.setKodeDock(rs.getString("kode_dock"));
        return t;
    }

    static LocalDateTime waktu(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    /**
     * Memvalidasi seluruh field wajib dan format nomor polisi (DS-F-02.2). Nomor polisi pada objek diubah ke
     * bentuk baku. Melempar AplikasiException berisi semua kesalahan.
     */
    public void validasi(Truck t) {
        List<String> salah = new ArrayList<>();
        String plat = Validator.normalisasiPlat(t.getNomorPolisi());
        if (t.getNomorPolisi() == null || t.getNomorPolisi().trim().isEmpty()) {
            salah.add("Nomor polisi wajib diisi.");
        } else if (plat == null) {
            salah.add("Format nomor polisi tidak valid (contoh: B 1234 XYZ).");
        } else {
            t.setNomorPolisi(plat);
        }
        if (t.getNamaSopir() == null || t.getNamaSopir().trim().isEmpty()) {
            salah.add("Nama sopir wajib diisi.");
        } else if (t.getNamaSopir().trim().length() > 100) {
            salah.add("Nama sopir maksimal 100 karakter.");
        }
        if (t.getJenisMuatan() == null) {
            salah.add("Jenis muatan wajib dipilih.");
        }
        if (t.getPemasokPelanggan() == null || t.getPemasokPelanggan().trim().isEmpty()) {
            salah.add("Pemasok/pelanggan wajib diisi.");
        } else if (t.getPemasokPelanggan().trim().length() > 100) {
            salah.add("Pemasok/pelanggan maksimal 100 karakter.");
        }
        if (t.getPerkiraanKedatangan() == null) {
            salah.add("Perkiraan waktu kedatangan wajib diisi.");
        }
        if (t.getEstimasiDurasiMenit() < 1 || t.getEstimasiDurasiMenit() > 600) {
            salah.add("Estimasi durasi bongkar-muat harus 1-600 menit.");
        }
        if (!salah.isEmpty()) {
            throw new AplikasiException(String.join("\n", salah));
        }
        t.setNamaSopir(t.getNamaSopir().trim());
        t.setPemasokPelanggan(t.getPemasokPelanggan().trim());
    }

    /** Menyimpan truck baru dengan status awal "Menunggu" dan waktu pendaftaran otomatis (DS-F-02.4). */
    public int simpan(Truck t, Pengguna user) throws SQLException {
        validasi(t);
        LocalDateTime sekarang = LocalDateTime.now();
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                // Serialisasi cek plat + insert: tabel tidak punya UNIQUE untuk plat aktif.
                PenjadwalanController.kunciPenjadwalan(c);
                if (platMasihAktif(c, t.getNomorPolisi(), 0)) {
                    throw new AplikasiException("Truck dengan nomor polisi " + t.getNomorPolisi()
                            + " masih terdaftar dan belum selesai diproses.");
                }
                int id;
                String sql = "INSERT INTO truck (nomor_polisi, nama_sopir, jenis_muatan, pemasok_pelanggan, "
                        + "perkiraan_waktu_kedatangan, estimasi_durasi_menit, status_proses, waktu_pendaftaran, "
                        + "diarsipkan, id_pengguna_input) VALUES (?, ?, ?, ?, ?, ?, 'MENUNGGU', ?, 0, ?)";
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, t.getNomorPolisi());
                    ps.setString(2, t.getNamaSopir());
                    ps.setString(3, t.getJenisMuatan().name());
                    ps.setString(4, t.getPemasokPelanggan());
                    ps.setTimestamp(5, Timestamp.valueOf(t.getPerkiraanKedatangan()));
                    ps.setInt(6, t.getEstimasiDurasiMenit());
                    ps.setTimestamp(7, Timestamp.valueOf(sekarang));
                    ps.setInt(8, user.getIdPengguna());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        id = keys.getInt(1);
                    }
                }
                // State awal: log tahap "Menunggu" dimulai saat pendaftaran.
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO log_status "
                        + "(id_truck, id_pengguna, status_proses, waktu, durasi_tahap_detik) VALUES (?, ?, 'MENUNGGU', ?, NULL)")) {
                    ps.setInt(1, id);
                    ps.setInt(2, user.getIdPengguna());
                    ps.setTimestamp(3, Timestamp.valueOf(sekarang));
                    ps.executeUpdate();
                }
                Audit.catat(c, user.getIdPengguna(), "INPUT_TRUCK", "Truck baru: " + t.getNomorPolisi()
                        + " (" + t.getJenisMuatan().getLabel() + ")");
                c.commit();
                t.setIdTruck(id);
                t.setStatusProses(StatusProses.MENUNGGU);
                t.setWaktuPendaftaran(sekarang);
                return id;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Apakah ada truck lain dengan plat sama yang belum selesai dan belum diarsipkan. */
    private boolean platMasihAktif(Connection c, String plat, int kecualiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM truck WHERE nomor_polisi = ? AND status_proses <> 'SELESAI_BERANGKAT' "
                + "AND diarsipkan = 0 AND id_truck <> ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plat);
            ps.setInt(2, kecualiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Mencari truck berstatus "Menunggu" (UC-03). {@code kata} dicocokkan ke nomor polisi, nama sopir, dan
     * pemasok/pelanggan; {@code jenis} boleh null (semua jenis).
     */
    public List<Truck> cari(String kata, JenisMuatan jenis) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_TRUCK)
                .append("WHERE t.status_proses = 'MENUNGGU' AND t.diarsipkan = 0 ");
        String k = kata == null ? "" : kata.trim();
        if (!k.isEmpty()) {
            sql.append("AND (t.nomor_polisi LIKE ? OR t.nama_sopir LIKE ? OR t.pemasok_pelanggan LIKE ?) ");
        }
        if (jenis != null) {
            sql.append("AND t.jenis_muatan = ? ");
        }
        sql.append("ORDER BY t.perkiraan_waktu_kedatangan, t.id_truck");
        List<Truck> hasil = new ArrayList<>();
        try (Connection c = Koneksi.buka(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            if (!k.isEmpty()) {
                String pola = "%" + k.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
                ps.setString(i++, pola);
                ps.setString(i++, pola);
                ps.setString(i++, pola);
            }
            if (jenis != null) {
                ps.setString(i, jenis.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(petakan(rs));
                }
            }
        }
        return hasil;
    }

    /**
     * Menyunting truck berstatus "Menunggu" (UC-04). Bila jenis muatan, durasi, atau waktu kedatangan berubah,
     * jadwal aktif truck dibatalkan agar penjadwalan dihitung ulang.
     */
    public void perbarui(Truck t, Pengguna user) throws SQLException {
        validasi(t);
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                PenjadwalanController.kunciPenjadwalan(c);
                Truck lama = null;
                try (PreparedStatement ps = c.prepareStatement(SELECT_TRUCK + "WHERE t.id_truck = ? FOR UPDATE")) {
                    ps.setInt(1, t.getIdTruck());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            lama = petakan(rs);
                        }
                    }
                }
                if (lama == null) {
                    throw new AplikasiException("Data truck tidak ditemukan.");
                }
                if (lama.getStatusProses() != StatusProses.MENUNGGU) {
                    throw new AplikasiException("Hanya truck berstatus \"Menunggu\" yang dapat disunting. "
                            + "Status saat ini: " + lama.getStatusProses().getLabel() + ".");
                }
                if (platMasihAktif(c, t.getNomorPolisi(), t.getIdTruck())) {
                    throw new AplikasiException("Nomor polisi " + t.getNomorPolisi()
                            + " sudah dipakai truck lain yang belum selesai diproses.");
                }
                String sql = "UPDATE truck SET nomor_polisi = ?, nama_sopir = ?, jenis_muatan = ?, "
                        + "pemasok_pelanggan = ?, perkiraan_waktu_kedatangan = ?, estimasi_durasi_menit = ? "
                        + "WHERE id_truck = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, t.getNomorPolisi());
                    ps.setString(2, t.getNamaSopir());
                    ps.setString(3, t.getJenisMuatan().name());
                    ps.setString(4, t.getPemasokPelanggan());
                    ps.setTimestamp(5, Timestamp.valueOf(t.getPerkiraanKedatangan()));
                    ps.setInt(6, t.getEstimasiDurasiMenit());
                    ps.setInt(7, t.getIdTruck());
                    ps.executeUpdate();
                }
                boolean pengaruhJadwal = lama.getJenisMuatan() != t.getJenisMuatan()
                        || lama.getEstimasiDurasiMenit() != t.getEstimasiDurasiMenit()
                        || !lama.getPerkiraanKedatangan().equals(t.getPerkiraanKedatangan());
                if (pengaruhJadwal) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE jadwal SET status = 'DIGANTI', "
                            + "keterangan = 'Data truck diubah; perlu dijadwalkan ulang' "
                            + "WHERE id_truck = ? AND status = 'AKTIF'")) {
                        ps.setInt(1, t.getIdTruck());
                        ps.executeUpdate();
                    }
                }
                Audit.catat(c, user.getIdPengguna(), "SUNTING_TRUCK", "Truck diubah: " + t.getNomorPolisi()
                        + (pengaruhJadwal ? " (jadwal dibatalkan, perlu penjadwalan ulang)" : ""));
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Truck yang belum selesai (untuk menu Update Status). */
    public List<Truck> daftarBerjalan() throws SQLException {
        String sql = SELECT_TRUCK + "WHERE t.diarsipkan = 0 AND t.status_proses <> 'SELESAI_BERANGKAT' "
                + "ORDER BY FIELD(t.status_proses, 'LOADING_UNLOADING', 'ADMINISTRASI', 'PARKIR_DOCK', 'MENUNGGU'), "
                + "t.perkiraan_waktu_kedatangan, t.id_truck";
        List<Truck> hasil = new ArrayList<>();
        try (Connection c = Koneksi.buka(); PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        }
        return hasil;
    }

    /** Truck untuk dashboard: yang belum selesai, ditambah yang selesai sejak awal hari ini. */
    public List<Truck> daftarDashboard(LocalDateTime sekarang) throws SQLException {
        String sql = SELECT_TRUCK + "WHERE t.diarsipkan = 0 AND (t.status_proses <> 'SELESAI_BERANGKAT' OR "
                + "(SELECT MAX(l.waktu) FROM log_status l WHERE l.id_truck = t.id_truck) >= ?) "
                + "ORDER BY FIELD(t.status_proses, 'LOADING_UNLOADING', 'ADMINISTRASI', 'PARKIR_DOCK', 'MENUNGGU', 'SELESAI_BERANGKAT'), "
                + "t.perkiraan_waktu_kedatangan, t.id_truck";
        List<Truck> hasil = new ArrayList<>();
        try (Connection c = Koneksi.buka(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(sekarang.toLocalDate().atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(petakan(rs));
                }
            }
        }
        return hasil;
    }
}
