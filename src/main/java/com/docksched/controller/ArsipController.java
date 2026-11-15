package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.Pengguna;
import com.docksched.util.AplikasiException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Pengarsipan data lama oleh Admin Gudang. Data historis tidak pernah dihapus permanen (DS-NF-04): truck yang
 * selesai sebelum tanggal tertentu hanya ditandai diarsipkan, sehingga tidak tampil di daftar harian.
 */
public class ArsipController {

    public int arsipkanSelesaiSebelum(LocalDate tanggal, Pengguna admin) throws SQLException {
        if (!admin.isProgrammer() && !admin.getPeran().bisaMasterData()) {
            throw new AplikasiException("Hanya Admin Gudang yang dapat mengarsipkan data.");
        }
        if (tanggal == null || tanggal.isAfter(LocalDate.now())) {
            throw new AplikasiException("Tanggal arsip tidak boleh di masa depan.");
        }
        String sql = "UPDATE truck SET diarsipkan = 1 WHERE status_proses = 'SELESAI_BERANGKAT' AND diarsipkan = 0 "
                + "AND (SELECT MAX(l.waktu) FROM log_status l WHERE l.id_truck = truck.id_truck) < ?";
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(tanggal.atStartOfDay()));
                int jumlah = ps.executeUpdate();
                Audit.catat(c, admin.getIdPengguna(), "ARSIP", jumlah + " truck selesai sebelum " + tanggal + " diarsipkan");
                c.commit();
                return jumlah;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
