package com.docksched.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Pembuka koneksi JDBC ke database H2 tertanam (mode kompatibel MySQL). Berkas database disimpan di folder pengguna
 * (~/.docksched/data), dibuat otomatis saat aplikasi pertama kali dibuka; tidak perlu memasang server database.
 */
public final class Koneksi {

    public static final String NAMA_DATABASE = "dock_sched";

    private static final Path FOLDER = Paths.get(System.getProperty("user.home"), ".docksched");
    // Lokasi dapat ditimpa lewat -Ddocksched.db.path (berguna untuk pengujian).
    private static final String URL = "jdbc:h2:file:"
            + System.getProperty("docksched.db.path", FOLDER.resolve("data").resolve(NAMA_DATABASE).toString())
            + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";

    private Koneksi() {
    }

    public static Path folderData() {
        return FOLDER;
    }

    /** Koneksi ke database dock_sched. */
    public static Connection buka() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }
}
