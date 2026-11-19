package com.docksched.controller;

import com.docksched.config.Koneksi;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cadangan basis data ke berkas .sql berisi perintah INSERT (DS-NF-05: minimal sekali sehari). Berkas disimpan di
 * ~/.docksched/backup dan dapat dipulihkan dengan menjalankannya di H2 (RUNSCRIPT).
 */
public class BackupService {

    /** Urutan tabel mengikuti relasi foreign key. */
    private static final String[] TABEL = {"pengguna", "dock", "pengaturan", "truck", "jadwal", "log_status",
        "log_aktivitas"};

    /** Lama cadangan disimpan sebelum dihapus otomatis. */
    static final int SIMPAN_HARI = 30;

    public Path folder() {
        return Koneksi.folderData().resolve("backup");
    }

    /** Membuat cadangan hari ini bila belum ada. Mengembalikan berkas baru, atau null bila sudah ada. */
    public Path cadangkanBilaPerlu() throws SQLException, IOException {
        String awalan = "dock_sched_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "_";
        Path dir = folder();
        if (Files.isDirectory(dir)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, awalan + "*.sql")) {
                if (ds.iterator().hasNext()) {
                    return null;
                }
            }
        }
        return cadangkan();
    }

    public Path cadangkan() throws SQLException, IOException {
        Path dir = folder();
        Files.createDirectories(dir);
        aturIzinPemilik(dir, "rwx------");
        Path berkas = dir.resolve("dock_sched_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql");
        Path sementara = dir.resolve(berkas.getFileName() + ".part");
        try {
            try (Connection c = Koneksi.buka();
                    BufferedWriter w = Files.newBufferedWriter(sementara, StandardCharsets.UTF_8)) {
                aturIzinPemilik(sementara, "rw-------");
                w.write("-- Cadangan DOCK-SCHED " + LocalDateTime.now() + "\n");
                w.write("-- Pulihkan: buat database lewat aplikasi (atau database/schema.sql), lalu jalankan berkas ini.\n");
                w.write("SET NAMES utf8mb4;\nSET FOREIGN_KEY_CHECKS=0;\n");
                for (int i = TABEL.length - 1; i >= 0; i--) {
                    w.write("DELETE FROM " + TABEL[i] + ";\n");
                }
                for (String tabel : TABEL) {
                    tulisTabel(c, w, tabel);
                }
                w.write("SET FOREIGN_KEY_CHECKS=1;\n");
            }
            Files.move(sementara, berkas, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(sementara);
        }
        hapusCadanganLama(dir);
        return berkas;
    }

    private void hapusCadanganLama(Path dir) throws IOException {
        String batas = "dock_sched_" + LocalDate.now().minusDays(SIMPAN_HARI).format(DateTimeFormatter.BASIC_ISO_DATE);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "dock_sched_*.sql")) {
            for (Path p : ds) {
                // Nama berkas diawali tanggal yyyyMMdd sehingga urutan teks = urutan tanggal.
                if (p.getFileName().toString().compareTo(batas) < 0) {
                    Files.deleteIfExists(p);
                }
            }
        }
    }

    private static void aturIzinPemilik(Path p, String izin) throws IOException {
        try {
            Files.setPosixFilePermissions(p, PosixFilePermissions.fromString(izin));
        } catch (UnsupportedOperationException e) {
            // shortcut: Windows tidak mendukung izin POSIX; bila perlu, atur ACL lewat AclFileAttributeView.
        }
    }

    private void tulisTabel(Connection c, BufferedWriter w, String tabel) throws SQLException, IOException {
        // Nama tabel berasal dari konstanta di atas, bukan dari input pengguna.
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM " + tabel); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            StringBuilder kolom = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                kolom.append(i > 1 ? ", " : "").append('`').append(md.getColumnName(i)).append('`');
            }
            while (rs.next()) {
                StringBuilder nilai = new StringBuilder();
                for (int i = 1; i <= n; i++) {
                    nilai.append(i > 1 ? ", " : "").append(literal(rs, md, i));
                }
                w.write("INSERT INTO `" + tabel + "` (" + kolom + ") VALUES (" + nilai + ");\n");
            }
        }
    }

    private String literal(ResultSet rs, ResultSetMetaData md, int i) throws SQLException {
        Object o = rs.getObject(i);
        if (o == null) {
            return "NULL";
        }
        switch (md.getColumnType(i)) {
            case Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.DECIMAL, Types.NUMERIC,
                    Types.FLOAT, Types.DOUBLE, Types.REAL, Types.BIT, Types.BOOLEAN -> {
                if (o instanceof Boolean b) {
                    return b ? "1" : "0";
                }
                return o.toString();
            }
            default -> {
                String s = rs.getString(i);
                StringBuilder sb = new StringBuilder("'");
                for (int k = 0; k < s.length(); k++) {
                    char ch = s.charAt(k);
                    switch (ch) {
                        case '\\' -> sb.append("\\\\");
                        case '\'' -> sb.append("\\'");
                        case '\n' -> sb.append("\\n");
                        case '\r' -> sb.append("\\r");
                        case '\0' -> sb.append("\\0");
                        case '\u001a' -> sb.append("\\Z");
                        default -> sb.append(ch);
                    }
                }
                return sb.append('\'').toString();
            }
        }
    }
}
