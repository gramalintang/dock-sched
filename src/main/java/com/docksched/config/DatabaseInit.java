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

    private static final String SKEMA = "/database/schema.sql";
    private static final String MIGRASI = "/database/migrasi/";

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
            for (String sql : bacaSkrip(SKEMA)) {
                st.execute(sql);
            }
        }
        try (Connection c = Koneksi.buka()) {
            jalankanMigrasi(c);
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

    private static void jalankanMigrasi(Connection c) throws SQLException {
        int versi = versiSkema(c);
        while (DatabaseInit.class.getResource(MIGRASI + (versi + 1) + ".sql") != null) {
            versi++;
            try (Statement st = c.createStatement()) {
                for (String sql : bacaSkrip(MIGRASI + versi + ".sql")) {
                    st.execute(sql);
                }
            }
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO pengaturan (kunci, nilai) VALUES "
                    + "('versi_skema', ?) ON DUPLICATE KEY UPDATE nilai = VALUES(nilai)")) {
                ps.setString(1, Integer.toString(versi));
                ps.executeUpdate();
            }
        }
    }

    private static int versiSkema(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT nilai FROM pengaturan WHERE kunci = 'versi_skema'");
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Integer.parseInt(rs.getString(1).trim()) : 1;
        } catch (NumberFormatException e) {
            throw new SQLException("Nilai pengaturan versi_skema rusak.", e);
        }
    }

    private static List<String> bacaSkrip(String resource) throws SQLException {
        try (InputStream in = DatabaseInit.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new SQLException("File " + resource + " tidak ditemukan di dalam aplikasi.");
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String baris;
                while ((baris = r.readLine()) != null) {
                    if (!baris.trim().startsWith("--")) {
                        sb.append(baris).append('\n');
                    }
                }
            }
            return pisahPerintah(sb.toString());
        } catch (IOException e) {
            throw new SQLException("Gagal membaca " + resource + ": " + e.getMessage(), e);
        }
    }

    static List<String> pisahPerintah(String skrip) {
        List<String> hasil = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char kutip = 0;
        for (int i = 0; i < skrip.length(); i++) {
            char ch = skrip.charAt(i);
            if (kutip != 0) {
                if (ch == '\\' && i + 1 < skrip.length()) {
                    sb.append(ch).append(skrip.charAt(++i));
                    continue;
                }
                if (ch == kutip) {
                    kutip = 0;
                }
            } else if (ch == '\'' || ch == '"' || ch == '`') {
                kutip = ch;
            } else if (ch == ';') {
                tambahBilaIsi(hasil, sb);
                continue;
            }
            sb.append(ch);
        }
        tambahBilaIsi(hasil, sb);
        return hasil;
    }

    private static void tambahBilaIsi(List<String> hasil, StringBuilder sb) {
        String t = sb.toString().trim();
        if (!t.isEmpty()) {
            hasil.add(t);
        }
        sb.setLength(0);
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
    public static boolean perluProgrammerAwal() throws SQLException {
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
