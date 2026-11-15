package com.docksched.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Validasi format input (DS-F-02.2 dan aturan akun). */
public final class Validator {

    /** Format plat Indonesia: 1-2 huruf, 1-4 angka, 0-3 huruf. Contoh: B 1234 XYZ, AB 123 CD. */
    private static final Pattern PLAT = Pattern.compile("^([A-Z]{1,2})\\s*(\\d{1,4})\\s*([A-Z]{0,3})$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");

    private Validator() {
    }

    /** "b1234xyz" -> "B 1234 XYZ"; null bila format tidak valid. */
    public static String normalisasiPlat(String mentah) {
        if (mentah == null) {
            return null;
        }
        Matcher m = PLAT.matcher(mentah.trim().toUpperCase(Locale.ROOT));
        if (!m.matches()) {
            return null;
        }
        String belakang = m.group(3).isEmpty() ? "" : " " + m.group(3);
        return m.group(1) + " " + m.group(2) + belakang;
    }

    /** Pesan kesalahan, atau null bila username valid. */
    public static String validasiUsername(String username) {
        if (username == null || !USERNAME.matcher(username.trim()).matches()) {
            return "Username harus 3-30 karakter dan hanya berisi huruf, angka, titik, atau garis bawah.";
        }
        return null;
    }

    /** Pesan kesalahan, atau null bila password baru memenuhi syarat. */
    public static String validasiPasswordBaru(String password) {
        if (password == null || password.length() < 8) {
            return "Password minimal 8 karakter.";
        }
        if (password.length() > 100) {
            return "Password maksimal 100 karakter.";
        }
        return null;
    }
}
