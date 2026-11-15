package com.docksched.util;

import java.time.LocalDateTime;
import java.util.Locale;

/** Fungsi bantu pemformatan teks untuk tampilan. */
public final class Format {

    private static final Locale ID = Locale.forLanguageTag("id-ID");

    private Format() {
    }

    public static String waktu(LocalDateTime t) {
        return t == null ? "-" : t.format(Konstanta.FMT_WAKTU);
    }

    public static String jam(LocalDateTime t) {
        return t == null ? "-" : t.format(Konstanta.FMT_JAM);
    }

    /** Contoh: 3725 detik -> "62 mnt 05 dtk". Nilai negatif berarti belum ada data. */
    public static String durasi(long detik) {
        if (detik < 0) {
            return "-";
        }
        return (detik / 60) + " mnt " + String.format("%02d", detik % 60) + " dtk";
    }

    public static String desimal(double nilai, int digit) {
        return String.format(ID, "%." + digit + "f", nilai);
    }
}
