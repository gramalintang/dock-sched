package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.Pengguna;
import com.docksched.util.AplikasiException;
import com.docksched.util.PasswordUtil;
import com.docksched.util.Validator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Pengelolaan akun pengguna oleh Admin Gudang (SRS 2.2). Akun tidak dihapus, hanya dinonaktifkan. */
public class PenggunaController {

    public List<Pengguna> daftar() throws SQLException {
        List<Pengguna> hasil = new ArrayList<>();
        String sql = "SELECT id_pengguna, username, password_hash, nama_lengkap, peran, aktif, dibuat_pada "
                + "FROM pengguna ORDER BY username";
        try (Connection c = Koneksi.buka(); PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pengguna p = AuthController.petakan(rs);
                Timestamp ts = rs.getTimestamp("dibuat_pada");
                p.setDibuatPada(ts == null ? null : ts.toLocalDateTime());
                hasil.add(p);
            }
        }
        return hasil;
    }

    private void validasi(Pengguna p) {
        String pesan = Validator.validasiUsername(p.getUsername());
        if (pesan != null) {
            throw new AplikasiException(pesan);
        }
        if (p.getNamaLengkap() == null || p.getNamaLengkap().trim().isEmpty()) {
            throw new AplikasiException("Nama lengkap wajib diisi.");
        }
        if (p.getNamaLengkap().trim().length() > 100) {
            throw new AplikasiException("Nama lengkap maksimal 100 karakter.");
        }
        if (p.getPeran() == null) {
            throw new AplikasiException("Peran wajib dipilih.");
        }
        p.setUsername(p.getUsername().trim());
        p.setNamaLengkap(p.getNamaLengkap().trim());
    }

    public void tambah(Pengguna p, String passwordAwal, Pengguna admin) throws SQLException {
        validasi(p);
        String pesan = Validator.validasiPasswordBaru(passwordAwal);
        if (pesan != null) {
            throw new AplikasiException(pesan);
        }
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                if (usernameAda(c, p.getUsername(), 0)) {
                    throw new AplikasiException("Username " + p.getUsername() + " sudah dipakai.");
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO pengguna (username, password_hash, "
                        + "nama_lengkap, peran, aktif, dibuat_pada) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, p.getUsername());
                    ps.setString(2, PasswordUtil.hash(passwordAwal.toCharArray()));
                    ps.setString(3, p.getNamaLengkap());
                    ps.setString(4, p.getPeran().name());
                    ps.setBoolean(5, p.isAktif());
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    ps.executeUpdate();
                }
                Audit.catat(c, admin.getIdPengguna(), "TAMBAH_PENGGUNA",
                        "Akun baru: " + p.getUsername() + " (" + p.getPeran().getLabel() + ")");
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Memperbarui akun; {@code passwordBaru} boleh kosong (password tidak diubah). */
    public void perbarui(Pengguna p, String passwordBaru, Pengguna admin) throws SQLException {
        validasi(p);
        boolean ubahPassword = passwordBaru != null && !passwordBaru.isEmpty();
        if (ubahPassword) {
            String pesan = Validator.validasiPasswordBaru(passwordBaru);
            if (pesan != null) {
                throw new AplikasiException(pesan);
            }
        }
        if (p.getIdPengguna() == admin.getIdPengguna()) {
            if (!p.isAktif()) {
                throw new AplikasiException("Anda tidak dapat menonaktifkan akun Anda sendiri.");
            }
            if (p.getPeran() != admin.getPeran()) {
                throw new AplikasiException("Anda tidak dapat mengubah peran akun Anda sendiri.");
            }
        }
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                if (!admin.isProgrammer() && targetAdalahProgrammer(c, p.getIdPengguna())) {
                    throw new AplikasiException("Akun Programmer hanya dapat dikelola oleh Programmer.");
                }
                if (usernameAda(c, p.getUsername(), p.getIdPengguna())) {
                    throw new AplikasiException("Username " + p.getUsername() + " sudah dipakai.");
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE pengguna SET username = ?, nama_lengkap = ?, "
                        + "peran = ?, aktif = ? WHERE id_pengguna = ?")) {
                    ps.setString(1, p.getUsername());
                    ps.setString(2, p.getNamaLengkap());
                    ps.setString(3, p.getPeran().name());
                    ps.setBoolean(4, p.isAktif());
                    ps.setInt(5, p.getIdPengguna());
                    ps.executeUpdate();
                }
                if (ubahPassword) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE pengguna SET password_hash = ? WHERE id_pengguna = ?")) {
                        ps.setString(1, PasswordUtil.hash(passwordBaru.toCharArray()));
                        ps.setInt(2, p.getIdPengguna());
                        ps.executeUpdate();
                    }
                }
                Audit.catat(c, admin.getIdPengguna(), "UBAH_PENGGUNA", "Akun " + p.getUsername()
                        + " (" + p.getPeran().getLabel() + (p.isAktif() ? ", aktif" : ", nonaktif") + ")"
                        + (ubahPassword ? ", password direset" : ""));
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private boolean targetAdalahProgrammer(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT nilai FROM pengaturan WHERE kunci='programmer_id'"); ResultSet rs = ps.executeQuery()) {
            return rs.next() && Integer.toString(id).equals(rs.getString(1));
        }
    }

    private boolean usernameAda(Connection c, String username, int kecualiId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM pengguna WHERE username = ? AND id_pengguna <> ?")) {
            ps.setString(1, username);
            ps.setInt(2, kecualiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
