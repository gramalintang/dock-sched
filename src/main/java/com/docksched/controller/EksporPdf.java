package com.docksched.controller;

import com.docksched.util.Format;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Menulis laporan ke berkas PDF tanpa pustaka tambahan. PDF dirakit langsung (teks Helvetica + garis tabel),
 * cukup untuk laporan tabel bagi manajemen gudang.
 */
public final class EksporPdf {

    private static final double LEBAR = 595;
    private static final double TINGGI = 842;
    private static final double MARGIN = 40;
    private static final double LEBAR_ISI = LEBAR - 2 * MARGIN;
    private static final double UKURAN = 9;
    private static final double TINGGI_BARIS = 12;

    private final List<StringBuilder> halaman = new ArrayList<>();
    private StringBuilder aktif;
    private double y;

    private EksporPdf() {
    }

    public static void tulis(Path tujuan, LaporanDokumen dok) throws IOException {
        EksporPdf pdf = new EksporPdf();
        pdf.susun(dok);
        Files.write(tujuan, pdf.rakit(dok.getJudul()));
    }

    private void halamanBaru() {
        aktif = new StringBuilder();
        halaman.add(aktif);
        y = TINGGI - MARGIN;
    }

    private void susun(LaporanDokumen dok) {
        halamanBaru();
        teks(MARGIN, y - 14, 16, true, dok.getJudul());
        y -= 22;
        teks(MARGIN, y - 10, 10, false, dok.getSubjudul());
        y -= 14;
        teks(MARGIN, y - 10, 9, false, dok.getCatatan());
        y -= 22;
        for (LaporanDokumen.Bagian b : dok.getBagian()) {
            tabel(b);
        }
    }

    private void tabel(LaporanDokumen.Bagian b) {
        int n = b.getKolom().size();
        double[] bobot = new double[n];
        for (int i = 0; i < n; i++) {
            bobot[i] = Math.min(40, Math.max(8, b.getKolom().get(i).length() * 0.6));
            for (List<Object> baris : b.getBaris()) {
                if (i < baris.size()) {
                    bobot[i] = Math.max(bobot[i], Math.min(40, teksSel(baris.get(i)).length()));
                }
            }
        }
        double jumlah = 0;
        for (double w : bobot) {
            jumlah += w;
        }
        double[] lebar = new double[n];
        double[] awal = new double[n];
        double x = MARGIN;
        for (int i = 0; i < n; i++) {
            lebar[i] = LEBAR_ISI * bobot[i] / jumlah;
            awal[i] = x;
            x += lebar[i];
        }

        if (y < 110) {
            halamanBaru();
        }
        teks(MARGIN, y - 12, 12, true, b.getJudul());
        y -= 20;
        kepalaTabel(b, awal, lebar);
        for (List<Object> baris : b.getBaris()) {
            List<List<String>> sel = new ArrayList<>();
            int maksBaris = 1;
            for (int i = 0; i < n; i++) {
                String nilai = i < baris.size() ? teksSel(baris.get(i)) : "";
                List<String> pecah = bungkus(nilai, jumlahKarakter(lebar[i]));
                sel.add(pecah);
                maksBaris = Math.max(maksBaris, pecah.size());
            }
            double tinggi = maksBaris * TINGGI_BARIS + 4;
            if (y - tinggi < MARGIN + 25) {
                halamanBaru();
                teks(MARGIN, y - 12, 10, true, b.getJudul() + " (lanjutan)");
                y -= 20;
                kepalaTabel(b, awal, lebar);
            }
            for (int i = 0; i < n; i++) {
                List<String> pecah = sel.get(i);
                for (int k = 0; k < pecah.size(); k++) {
                    teks(awal[i] + 3, y - 10 - k * TINGGI_BARIS, UKURAN, false, pecah.get(k));
                }
            }
            y -= tinggi;
            garis(MARGIN, y, MARGIN + LEBAR_ISI, y, 0.4);
        }
        y -= 18;
    }

    private void kepalaTabel(LaporanDokumen.Bagian b, double[] awal, double[] lebar) {
        int n = b.getKolom().size();
        List<List<String>> sel = new ArrayList<>();
        int maksBaris = 1;
        for (int i = 0; i < n; i++) {
            List<String> pecah = bungkus(b.getKolom().get(i), jumlahKarakter(lebar[i]));
            sel.add(pecah);
            maksBaris = Math.max(maksBaris, pecah.size());
        }
        double tinggi = maksBaris * TINGGI_BARIS + 4;
        aktif.append("0.85 0.88 0.95 rg ").append(f(MARGIN)).append(' ').append(f(y - tinggi)).append(' ')
                .append(f(LEBAR_ISI)).append(' ').append(f(tinggi)).append(" re f 0 g\n");
        for (int i = 0; i < n; i++) {
            List<String> pecah = sel.get(i);
            for (int k = 0; k < pecah.size(); k++) {
                teks(awal[i] + 3, y - 10 - k * TINGGI_BARIS, UKURAN, true, pecah.get(k));
            }
        }
        y -= tinggi;
        garis(MARGIN, y, MARGIN + LEBAR_ISI, y, 0.8);
    }

    private static int jumlahKarakter(double lebarKolom) {
        return Math.max(4, (int) ((lebarKolom - 6) / (UKURAN * 0.55)));
    }

    private static String teksSel(Object nilai) {
        if (nilai == null) {
            return "";
        }
        if (nilai instanceof Double || nilai instanceof Float) {
            return Format.desimal(((Number) nilai).doubleValue(), 1);
        }
        return nilai.toString();
    }

    /** Membungkus teks per kata; kata yang lebih panjang dari lebar kolom dipotong. */
    private static List<String> bungkus(String teks, int maks) {
        List<String> baris = new ArrayList<>();
        StringBuilder sekarang = new StringBuilder();
        for (String kata : teks.split(" ")) {
            while (kata.length() > maks) {
                if (sekarang.length() > 0) {
                    baris.add(sekarang.toString());
                    sekarang.setLength(0);
                }
                baris.add(kata.substring(0, maks));
                kata = kata.substring(maks);
            }
            if (sekarang.length() == 0) {
                sekarang.append(kata);
            } else if (sekarang.length() + 1 + kata.length() <= maks) {
                sekarang.append(' ').append(kata);
            } else {
                baris.add(sekarang.toString());
                sekarang.setLength(0);
                sekarang.append(kata);
            }
        }
        baris.add(sekarang.toString());
        return baris;
    }

    private void teks(double x, double yy, double ukuran, boolean tebal, String isi) {
        aktif.append("BT /").append(tebal ? "F2 " : "F1 ").append(f(ukuran)).append(" Tf ")
                .append(f(x)).append(' ').append(f(yy)).append(" Td (").append(escape(isi)).append(") Tj ET\n");
    }

    private void garis(double x1, double y1, double x2, double y2, double tebal) {
        aktif.append(f(tebal)).append(" w ").append(f(x1)).append(' ').append(f(y1)).append(" m ")
                .append(f(x2)).append(' ').append(f(y2)).append(" l S\n");
    }

    private static String f(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    /** Escape string PDF; karakter di luar Latin-1 diganti "?" karena font standar memakai WinAnsi. */
    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\\' || ch == '(' || ch == ')') {
                sb.append('\\').append(ch);
            } else if (ch < 32) {
                sb.append(' ');
            } else if (ch > 255) {
                sb.append('?');
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private byte[] rakit(String judul) throws IOException {
        int total = halaman.size();
        for (int i = 0; i < total; i++) {
            StringBuilder sb = halaman.get(i);
            sb.append("BT /F1 8 Tf ").append(f(MARGIN)).append(" 24 Td (").append(escape(judul)).append(") Tj ET\n");
            sb.append("BT /F1 8 Tf ").append(f(LEBAR - MARGIN - 70)).append(" 24 Td (Halaman ").append(i + 1)
                    .append(" dari ").append(total).append(") Tj ET\n");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offset = new ArrayList<>();
        tulisAscii(out, "%PDF-1.4\n");
        // Objek: 1 Catalog, 2 Pages, 3 Helvetica, 4 Helvetica-Bold, lalu pasangan (Page, Content) per halaman.
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < total; i++) {
            kids.append(5 + i * 2).append(" 0 R ");
        }
        objek(out, offset, "<< /Type /Catalog /Pages 2 0 R >>");
        objek(out, offset, "<< /Type /Pages /Kids [ " + kids + "] /Count " + total + " >>");
        objek(out, offset, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
        objek(out, offset, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");
        for (int i = 0; i < total; i++) {
            int konten = 6 + i * 2;
            objek(out, offset, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) LEBAR + " " + (int) TINGGI
                    + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + konten + " 0 R >>");
            byte[] isi = halaman.get(i).toString().getBytes(StandardCharsets.ISO_8859_1);
            offset.add(out.size());
            tulisAscii(out, offset.size() + " 0 obj\n<< /Length " + isi.length + " >>\nstream\n");
            out.write(isi);
            tulisAscii(out, "\nendstream\nendobj\n");
        }
        int xref = out.size();
        tulisAscii(out, "xref\n0 " + (offset.size() + 1) + "\n0000000000 65535 f \n");
        for (int o : offset) {
            tulisAscii(out, String.format(Locale.US, "%010d 00000 n \n", o));
        }
        tulisAscii(out, "trailer\n<< /Size " + (offset.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        return out.toByteArray();
    }

    private static void objek(ByteArrayOutputStream out, List<Integer> offset, String isi) throws IOException {
        offset.add(out.size());
        tulisAscii(out, offset.size() + " 0 obj\n" + isi + "\nendobj\n");
    }

    private static void tulisAscii(ByteArrayOutputStream out, String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }
}
