package com.docksched.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

/** Pencatatan log aktivitas penting: login, perubahan status, perubahan jadwal (DS-NF-08). */
public final class Audit {

    private Audit() {
    }

    /** Mencatat memakai koneksi/transaksi yang sedang berjalan. */
    public static void catat(Connection c, Integer idPengguna, String aktivitas, String detail) throws SQLException {
        String sql = "INSERT INTO log_aktivitas (id_pengguna, aktivitas, detail, waktu) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (idPengguna == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idPengguna);
            }
            ps.setString(2, aktivitas);
            ps.setString(3, detail == null ? null : (detail.length() > 250 ? detail.substring(0, 250) : detail));
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }
}
