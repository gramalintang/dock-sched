package com.docksched.config;

import com.docksched.model.JenisMuatan;
import com.docksched.model.Peran;
import com.docksched.util.PasswordUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Menyiapkan database saat aplikasi pertama kali dijalankan: membuat database dan tabel (database/schema.sql),
 * lalu mengisi akun awal dan dock awal bila masih kosong.
 */
public final class DatabaseInit {

    private static boolean sudahSiap;

    private DatabaseInit() {
    }

    /** Dipanggil saat pengaturan koneksi berubah agar database disiapkan ulang pada server yang baru. */
    public static synchronized void reset() {
        sudahSiap = false;
    }

    public static synchronized void siapkan() throws SQLException {
        if (sudahSiap) {
            return;
        }
        try (Connection c = Koneksi.bukaServer(); Statement st = c.createStatement()) {
            for (String sql : bacaSkrip()) {
                st.execute(sql);
            }
        }
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                isiDataAwal(c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
        sudahSiap = true;
    }

    private static List<String> bacaSkrip() throws SQLException {
        List<String> hasil = new ArrayList<>();
        try (InputStream in = DatabaseInit.class.getResourceAsStream("/database/schema.sql")) {
            if (in == null) {
                throw new SQLException("File database/schema.sql tidak ditemukan di dalam aplikasi.");
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String baris;
                while ((baris = r.readLine()) != null) {
                    String t = baris.trim();
                    if (t.isEmpty() || t.startsWith("--")) {
                        continue;
                    }
                    sb.append(baris).append('\n');
                }
            }
            for (String bagian : sb.toString().split(";")) {
                if (!bagian.trim().isEmpty()) {
                    hasil.add(bagian.trim());
                }
            }
        } catch (IOException e) {
            throw new SQLException("Gagal membaca database/schema.sql: " + e.getMessage(), e);
        }
        return hasil;
    }

    private static void isiDataAwal(Connection c) throws SQLException {
        if (hitung(c, "dock") == 0) {
            tambahDock(c, "DOCK-A1", 20, JenisMuatan.BAHAN_BAKU_MASUK, false);
            tambahDock(c, "DOCK-A2", 15, JenisMuatan.BAHAN_BAKU_MASUK, false);
            tambahDock(c, "DOCK-B1", 20, JenisMuatan.BARANG_JADI_KELUAR, false);
            tambahDock(c, "DOCK-B2", 15, JenisMuatan.BARANG_JADI_KELUAR, false);
            tambahDock(c, "DOCK-S1", 10, JenisMuatan.BAHAN_BAKU_MASUK, true);
        }
    }

    /** True when no user has yet been provisioned. No default username/password is created. */
    public static boolean requiereProgrammerInicial() throws SQLException {
        try (Connection c = Koneksi.buka(); PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM pengguna"); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    /** Creates the first full-access account and records its identity in the existing settings table. */
    public static void buatProgrammerPertama(String username, String nama, char[] password) throws SQLException {
        String u = username == null ? "" : username.trim();
        String n = nama == null ? "" : nama.trim();
        String pesan = com.docksched.util.Validator.validasiUsername(u);
        if (pesan != null) throw new IllegalArgumentException(pesan);
        if (n.isEmpty() || n.length() > 100) throw new IllegalArgumentException("Nama wajib diisi (maksimal 100 karakter).");
        if (password == null || password.length < 8 || password.length > 100)
            throw new IllegalArgumentException("Password harus 8-100 karakter.");
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement cek = c.prepareStatement("SELECT COUNT(*) FROM pengguna"); ResultSet rs = cek.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) != 0) throw new SQLException("Akun sudah tersedia. Setup Programmer tidak dapat diulang.");
                }
                int id;
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO pengguna (username,password_hash,nama_lengkap,peran,aktif,dibuat_pada) VALUES (?,?,?,?,1,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, u);
                    ps.setString(2, PasswordUtil.hash(password));
                    ps.setString(3, n);
                    ps.setString(4, Peran.ADMIN_GUDANG.name());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { if (!keys.next()) throw new SQLException("Gagal membuat akun Programmer."); id = keys.getInt(1); }
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO pengaturan (kunci,nilai) VALUES ('programmer_id',?) ON DUPLICATE KEY UPDATE nilai=VALUES(nilai)")) {
                    ps.setString(1, Integer.toString(id)); ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }

    private static int hitung(Connection c, String tabel) throws SQLException {
        // Nama tabel berasal dari kode program (bukan input pengguna), jadi aman digabung.
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM " + tabel);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static void tambahDock(Connection c, String kode, int kapasitas, JenisMuatan jenis, boolean serbaguna)
            throws SQLException {
        String sql = "INSERT INTO dock (kode_dock, kapasitas, jenis_muatan, dock_serbaguna, status_ketersediaan) "
                + "VALUES (?, ?, ?, ?, 'TERSEDIA')";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, kapasitas);
            ps.setString(3, jenis.name());
            ps.setBoolean(4, serbaguna);
            ps.executeUpdate();
        }
    }
}
