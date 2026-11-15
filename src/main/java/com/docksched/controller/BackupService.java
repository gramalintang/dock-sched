package com.docksched.controller;

import com.docksched.config.Koneksi;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * ~/.docksched/backup dan dapat dipulihkan dengan menjalankannya di MySQL.
 */
public class BackupService {

    /** Urutan tabel mengikuti relasi foreign key. */
    private static final String[] TABEL = {"pengguna", "dock", "pengaturan", "truck", "jadwal", "log_status",
        "log_aktivitas"};

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
        Files.createDirectories(folder());
        Path berkas = folder().resolve("dock_sched_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql");
        try (Connection c = Koneksi.buka(); BufferedWriter w = Files.newBufferedWriter(berkas, StandardCharsets.UTF_8)) {
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
        return berkas;
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
