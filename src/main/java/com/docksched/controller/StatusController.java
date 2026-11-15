package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.JenisMuatan;
import com.docksched.model.Pengguna;
import com.docksched.model.StatusDock;
import com.docksched.model.StatusProses;
import com.docksched.util.AplikasiException;
import com.docksched.util.Konstanta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Pembaruan status proses truck (UC-09, UC-10, UC-11; DS-F-04.1 - 04.5).
 * Urutan: Menunggu -> Parkir Dock -> Loading/Unloading -> Administrasi -> Selesai/Berangkat.
 */
public class StatusController {

    /** Hasil pembaruan status untuk ditampilkan ke pengguna. */
    public record HasilUbahStatus(String nomorPolisi, StatusProses statusLama, StatusProses statusBaru,
            long durasiTahapDetik, long totalDetik, boolean melebihiTarget, String kodeDock) {
    }

    /**
     * Mengubah status truck ke {@code tujuan}. Aturan: hanya Petugas Dock (DS-NF-15); tidak boleh melompati tahap
     * (DS-F-04.5); untuk masuk "Parkir Dock" truck harus punya jadwal aktif pada dock yang sesuai jenis muatan dan
     * tersedia (guard T3, DS-F-03.3). Timestamp dicatat otomatis (DS-F-04.2) dan durasi tahap sebelumnya dihitung
     * dari selisih timestamp (DS-F-04.3).
     */
    public HasilUbahStatus ubahStatus(int idTruck, StatusProses tujuan, Pengguna user) throws SQLException {
        if (user == null || (!user.isProgrammer() && !user.getPeran().bisaUbahStatus())) {
            throw new AplikasiException("Perubahan status proses hanya dapat dilakukan oleh Petugas Dock.");
        }
        if (tujuan == null) {
            throw new AplikasiException("Pilih status baru terlebih dahulu.");
        }
        LocalDateTime sekarang = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                String plat;
                StatusProses saatIni;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT nomor_polisi, status_proses, diarsipkan FROM truck WHERE id_truck = ? FOR UPDATE")) {
                    ps.setInt(1, idTruck);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new AplikasiException("Data truck tidak ditemukan. Segarkan tampilan.");
                        }
                        plat = rs.getString("nomor_polisi");
                        saatIni = StatusProses.valueOf(rs.getString("status_proses"));
                        if (rs.getBoolean("diarsipkan")) {
                            throw new AplikasiException("Truck " + plat + " sudah diarsipkan.");
                        }
                    }
                }
                StatusProses seharusnya = saatIni.berikutnya();
                if (seharusnya == null) {
                    throw new AplikasiException("Truck " + plat + " sudah berstatus Selesai/Berangkat.");
                }
                if (tujuan != seharusnya) {
                    throw new AplikasiException("Perubahan status ditolak: tahap tidak boleh dilompati.\n"
                            + "Status saat ini \"" + saatIni.getLabel() + "\", status berikutnya yang diizinkan adalah \""
                            + seharusnya.getLabel() + "\".");
                }

                int idDock = 0;
                if (tujuan == StatusProses.PARKIR_DOCK) {
                    idDock = periksaDockUntukParkir(c, idTruck, plat);
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE dock SET status_ketersediaan = 'TERISI' WHERE id_dock = ?")) {
                        ps.setInt(1, idDock);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = c.prepareStatement("UPDATE truck SET status_proses = ? WHERE id_truck = ?")) {
                    ps.setString(1, tujuan.name());
                    ps.setInt(2, idTruck);
                    ps.executeUpdate();
                }

                // Tutup tahap sebelumnya: durasi = selisih timestamp berurutan.
                long durasiTahap = 0;
                int idLogTerakhir = 0;
                LocalDateTime mulaiTahap = null;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT id_log, waktu FROM log_status WHERE id_truck = ? ORDER BY id_log DESC LIMIT 1")) {
                    ps.setInt(1, idTruck);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idLogTerakhir = rs.getInt("id_log");
                            mulaiTahap = rs.getTimestamp("waktu").toLocalDateTime();
                        }
                    }
                }
                if (mulaiTahap != null) {
                    durasiTahap = Math.max(0, Duration.between(mulaiTahap, sekarang).getSeconds());
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE log_status SET durasi_tahap_detik = ? WHERE id_log = ?")) {
                        ps.setLong(1, durasiTahap);
                        ps.setInt(2, idLogTerakhir);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO log_status "
                        + "(id_truck, id_pengguna, status_proses, waktu, durasi_tahap_detik) VALUES (?, ?, ?, ?, NULL)")) {
                    ps.setInt(1, idTruck);
                    ps.setInt(2, user.getIdPengguna());
                    ps.setString(3, tujuan.name());
                    ps.setTimestamp(4, Timestamp.valueOf(sekarang));
                    ps.executeUpdate();
                }

                if (tujuan == StatusProses.SELESAI_BERANGKAT) {
                    // Jadwal selesai dan dock dibebaskan (kecuali Admin menandainya rusak).
                    try (PreparedStatement ps = c.prepareStatement("SELECT id_jadwal, id_dock FROM jadwal "
                            + "WHERE id_truck = ? AND status = 'AKTIF' ORDER BY id_jadwal DESC LIMIT 1")) {
                        ps.setInt(1, idTruck);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                idDock = rs.getInt("id_dock");
                                try (PreparedStatement up = c.prepareStatement(
                                        "UPDATE jadwal SET status = 'SELESAI' WHERE id_jadwal = ?")) {
                                    up.setInt(1, rs.getInt("id_jadwal"));
                                    up.executeUpdate();
                                }
                            }
                        }
                    }
                    if (idDock > 0) {
                        try (PreparedStatement ps = c.prepareStatement("UPDATE dock SET status_ketersediaan = 'TERSEDIA' "
                                + "WHERE id_dock = ? AND status_ketersediaan = 'TERISI'")) {
                            ps.setInt(1, idDock);
                            ps.executeUpdate();
                        }
                    }
                }

                // Total durasi proses sejak truck parkir di dock (DS-NF-14).
                long total = -1;
                try (PreparedStatement ps = c.prepareStatement("SELECT MIN(waktu) FROM log_status "
                        + "WHERE id_truck = ? AND status_proses = 'PARKIR_DOCK'")) {
                    ps.setInt(1, idTruck);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getTimestamp(1) != null) {
                            total = Math.max(0, Duration.between(rs.getTimestamp(1).toLocalDateTime(), sekarang).getSeconds());
                        }
                    }
                }
                String kodeDock = null;
                try (PreparedStatement ps = c.prepareStatement("SELECT d.kode_dock FROM jadwal j JOIN dock d "
                        + "ON d.id_dock = j.id_dock WHERE j.id_truck = ? AND j.status IN ('AKTIF','SELESAI') "
                        + "ORDER BY j.id_jadwal DESC LIMIT 1")) {
                    ps.setInt(1, idTruck);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            kodeDock = rs.getString(1);
                        }
                    }
                }
                boolean lewat = total > Konstanta.TARGET_MENIT * 60L;
                Audit.catat(c, user.getIdPengguna(), "UBAH_STATUS", "Truck " + plat + ": " + saatIni.getLabel() + " -> "
                        + tujuan.getLabel() + (lewat ? " (melebihi target " + Konstanta.TARGET_MENIT + " menit)" : ""));
                c.commit();
                return new HasilUbahStatus(plat, saatIni, tujuan, durasiTahap, total, lewat, kodeDock);
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Guard T3: dock jadwal aktif harus sesuai jenis muatan dan berstatus tersedia. Mengembalikan id dock. */
    private int periksaDockUntukParkir(Connection c, int idTruck, String plat) throws SQLException {
        String sql = "SELECT d.id_dock, d.kode_dock, d.status_ketersediaan, d.jenis_muatan, d.dock_serbaguna, "
                + "t.jenis_muatan AS jenis_truck FROM jadwal j JOIN dock d ON d.id_dock = j.id_dock "
                + "JOIN truck t ON t.id_truck = j.id_truck WHERE j.id_truck = ? AND j.status = 'AKTIF' "
                + "ORDER BY j.id_jadwal DESC LIMIT 1 FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTruck);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new AplikasiException("Truck " + plat + " belum dijadwalkan ke dock. "
                            + "Jalankan menu Penjadwalan Slot Dock terlebih dahulu.");
                }
                String kode = rs.getString("kode_dock");
                StatusDock status = StatusDock.valueOf(rs.getString("status_ketersediaan"));
                JenisMuatan jenisDock = JenisMuatan.valueOf(rs.getString("jenis_muatan"));
                JenisMuatan jenisTruck = JenisMuatan.valueOf(rs.getString("jenis_truck"));
                boolean cocok = rs.getBoolean("dock_serbaguna") || jenisDock == jenisTruck;
                if (!cocok) {
                    throw new AplikasiException("Dock " + kode + " tidak sesuai dengan jenis muatan truck "
                            + plat + ". Jadwalkan ulang truck ini.");
                }
                if (status == StatusDock.RUSAK) {
                    throw new AplikasiException("Dock " + kode + " sedang rusak. Jadwalkan ulang truck " + plat + ".");
                }
                if (status == StatusDock.TERISI) {
                    throw new AplikasiException("Dock " + kode + " masih terisi truck lain. "
                            + "Tunggu sampai dock tersedia sebelum truck " + plat + " parkir.");
                }
                return rs.getInt("id_dock");
            }
        }
    }
}
