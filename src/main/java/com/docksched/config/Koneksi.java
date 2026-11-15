package com.docksched.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Pengaturan dan pembuka koneksi JDBC ke MySQL. Pengaturan disimpan di folder pengguna
 * (~/.docksched/koneksi.properties), bukan di dalam proyek, sehingga password database tidak ikut ter-upload ke GitHub.
 */
public final class Koneksi {

    public static final String NAMA_DATABASE = "dock_sched";

    private static final Path FOLDER = Paths.get(System.getProperty("user.home"), ".docksched");
    private static final Path FILE = FOLDER.resolve("koneksi.properties");
    private static final String PARAMETER = "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&useUnicode=true&characterEncoding=UTF-8&connectTimeout=5000";

    private static String host = "localhost";
    private static int port = 3306;
    private static String user = "root";
    private static String password = "";

    static {
        muat();
    }

    private Koneksi() {
    }

    public static Path folderData() {
        return FOLDER;
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static synchronized void atur(String host, int port, String user, String password) {
        Koneksi.host = host;
        Koneksi.port = port;
        Koneksi.user = user;
        Koneksi.password = password;
    }

    private static synchronized void muat() {
        // Properti sistem boleh menimpa (berguna untuk pengujian / skrip).
        host = System.getProperty("docksched.db.host", host);
        port = Integer.getInteger("docksched.db.port", port);
        user = System.getProperty("docksched.db.user", user);
        password = System.getProperty("docksched.db.password", password);
        if (System.getProperty("docksched.db.host") != null || !Files.isRegularFile(FILE)) {
            return;
        }
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            p.load(in);
            host = p.getProperty("host", host);
            port = Integer.parseInt(p.getProperty("port", String.valueOf(port)));
            user = p.getProperty("user", user);
            password = p.getProperty("password", password);
        } catch (IOException | NumberFormatException e) {
            // File rusak: pakai nilai bawaan; pengguna bisa mengaturnya lagi lewat aplikasi.
        }
    }

    public static synchronized void simpan() throws IOException {
        Files.createDirectories(FOLDER);
        Properties p = new Properties();
        p.setProperty("host", host);
        p.setProperty("port", String.valueOf(port));
        p.setProperty("user", user);
        p.setProperty("password", password);
        try (OutputStream out = Files.newOutputStream(FILE)) {
            p.store(out, "Pengaturan koneksi DOCK-SCHED");
        }
    }

    private static String urlDasar() {
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    /** Koneksi ke server tanpa memilih database (untuk membuat database pertama kali). */
    public static synchronized Connection bukaServer() throws SQLException {
        return DriverManager.getConnection(urlDasar() + PARAMETER, user, password);
    }

    /** Koneksi ke database dock_sched. */
    public static synchronized Connection buka() throws SQLException {
        return DriverManager.getConnection(urlDasar() + NAMA_DATABASE + PARAMETER, user, password);
    }
}
