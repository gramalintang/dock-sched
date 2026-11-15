package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.Pengguna;
import com.docksched.model.Peran;
import com.docksched.util.AplikasiException;
import com.docksched.util.PasswordUtil;
import com.docksched.util.Validator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

/** Autentikasi pengguna (UC-01; DS-F-01.1 - 01.5). */
public class AuthController {

    static final int MAKS_GAGAL = 5;
    static final int KUNCI_MENIT = 15;

    /**
     * Memvalidasi username dan password terhadap tabel pengguna (DS-F-01.2).
     * Mengembalikan null bila salah; pemanggil menampilkan pesan umum tanpa menyebut field mana yang keliru (DS-F-01.4).
     */
    public Pengguna login(String username, char[] password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.length == 0) {
            return null;
        }
        String sql = "SELECT id_pengguna, username, password_hash, nama_lengkap, peran, aktif "
                + "FROM pengguna WHERE username = ? AND aktif = 1";
        try (Connection c = Koneksi.buka(); PreparedStatement ps = c.prepareStatement(sql)) {
            periksaKunciLogin(c, username.trim());
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash"))) {
                    Pengguna p = petakan(rs);
                    p.setProgrammer(adalahProgrammer(c, p.getIdPengguna()));
                    Audit.catat(c, p.getIdPengguna(), "LOGIN", detailLoginBerhasil(username.trim()));
                    return p;
                }
            }
            Audit.catat(c, null, "LOGIN_GAGAL", detailLoginGagal(username.trim()));
            return null;
        }
    }

    private void periksaKunciLogin(Connection c, String username) throws SQLException {
        String sql = "SELECT COUNT(*), MIN(waktu) FROM log_aktivitas WHERE aktivitas = 'LOGIN_GAGAL' AND detail = ? "
                + "AND waktu > ? AND waktu > COALESCE((SELECT MAX(waktu) FROM log_aktivitas "
                + "WHERE aktivitas = 'LOGIN' AND detail = ?), '1970-01-01')";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, potong(detailLoginGagal(username)));
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusMinutes(KUNCI_MENIT)));
            ps.setString(3, potong(detailLoginBerhasil(username)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) >= MAKS_GAGAL) {
                    LocalDateTime terbuka = rs.getTimestamp(2).toLocalDateTime().plusMinutes(KUNCI_MENIT);
                    long sisa = Math.max(1, Duration.between(LocalDateTime.now(), terbuka).toMinutes() + 1);
                    throw new AplikasiException("Terlalu banyak percobaan login gagal. Coba lagi dalam " + sisa
                            + " menit.");
                }
            }
        }
    }

    private static String detailLoginGagal(String username) {
        return "Percobaan login gagal untuk username: " + username;
    }

    private static String detailLoginBerhasil(String username) {
        return "Login berhasil: " + username;
    }

    private static String potong(String detail) {
        return detail.length() > 250 ? detail.substring(0, 250) : detail;
    }

    static boolean adalahProgrammer(Connection c, int idPengguna) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT nilai FROM pengaturan WHERE kunci = 'programmer_id'");
                ResultSet rs = ps.executeQuery()) {
            return rs.next() && Integer.toString(idPengguna).equals(rs.getString(1));
        }
    }

    public void catatLogout(Pengguna p, boolean otomatis) {
        if (p == null) {
            return;
        }
        try (Connection c = Koneksi.buka()) {
            Audit.catat(c, p.getIdPengguna(), otomatis ? "LOGOUT_OTOMATIS" : "LOGOUT",
                    (otomatis ? "Logout otomatis (tidak aktif): " : "Logout: ") + p.getUsername());
        } catch (SQLException e) {
            // Gagal mencatat log tidak boleh menghalangi logout.
        }
    }

    /** Pengguna mengganti passwordnya sendiri. */
    public void gantiPassword(Pengguna p, char[] passwordLama, String passwordBaru) throws SQLException {
        String pesan = Validator.validasiPasswordBaru(passwordBaru);
        if (pesan != null) {
            throw new AplikasiException(pesan);
        }
        try (Connection c = Koneksi.buka()) {
            String hashLama;
            try (PreparedStatement ps = c.prepareStatement("SELECT password_hash FROM pengguna WHERE id_pengguna = ?")) {
                ps.setInt(1, p.getIdPengguna());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new AplikasiException("Akun tidak ditemukan.");
                    }
                    hashLama = rs.getString(1);
                }
            }
            if (!PasswordUtil.verify(passwordLama, hashLama)) {
                throw new AplikasiException("Password lama tidak sesuai.");
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE pengguna SET password_hash = ? WHERE id_pengguna = ?")) {
                ps.setString(1, PasswordUtil.hash(passwordBaru.toCharArray()));
                ps.setInt(2, p.getIdPengguna());
                ps.executeUpdate();
            }
            Audit.catat(c, p.getIdPengguna(), "GANTI_PASSWORD", "Pengguna mengganti password: " + p.getUsername());
        }
    }

    static Pengguna petakan(ResultSet rs) throws SQLException {
        Pengguna p = new Pengguna();
        p.setIdPengguna(rs.getInt("id_pengguna"));
        p.setUsername(rs.getString("username"));
        p.setPasswordHash(rs.getString("password_hash"));
        p.setNamaLengkap(rs.getString("nama_lengkap"));
        p.setPeran(Peran.valueOf(rs.getString("peran")));
        p.setAktif(rs.getBoolean("aktif"));
        return p;
    }
}
