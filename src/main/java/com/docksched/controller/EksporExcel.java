package com.docksched.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Menulis laporan ke berkas Excel (.xlsx) tanpa pustaka tambahan: .xlsx adalah arsip ZIP berisi XML
 * (Office Open XML), sehingga cukup memakai java.util.zip bawaan JDK.
 */
public final class EksporExcel {

    private static final int GAYA_BIASA = 0;
    private static final int GAYA_TEBAL = 1;
    private static final int GAYA_JUDUL = 2;
    private static final int GAYA_HEADER = 3;
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    private EksporExcel() {
    }

    public static void tulis(Path tujuan, LaporanDokumen dok) throws IOException {
        StringBuilder data = new StringBuilder();
        int[] lebar = new int[8];
        int baris = 1;
        baris = tambahBaris(data, baris, GAYA_JUDUL, lebar, dok.getJudul());
        baris = tambahBaris(data, baris, GAYA_BIASA, lebar, dok.getSubjudul());
        baris = tambahBaris(data, baris, GAYA_BIASA, lebar, dok.getCatatan());
        for (LaporanDokumen.Bagian b : dok.getBagian()) {
            baris++; // baris kosong pemisah
            baris = tambahBaris(data, baris, GAYA_TEBAL, lebar, b.getJudul());
            baris = tambahBaris(data, baris, GAYA_HEADER, lebar, b.getKolom().toArray());
            for (List<Object> sel : b.getBaris()) {
                baris = tambahBaris(data, baris, GAYA_BIASA, lebar, sel.toArray());
            }
        }

        StringBuilder sheet = new StringBuilder();
        sheet.append(XML).append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><cols>");
        for (int i = 0; i < lebar.length; i++) {
            if (lebar[i] > 0) {
                sheet.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1)
                        .append("\" width=\"").append(Math.min(45, Math.max(10, lebar[i] + 2))).append("\" customWidth=\"1\"/>");
            }
        }
        sheet.append("</cols><sheetData>").append(data).append("</sheetData></worksheet>");

        try (OutputStream out = Files.newOutputStream(tujuan); ZipOutputStream zip = new ZipOutputStream(out)) {
            masukkan(zip, "[Content_Types].xml", XML
                    + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                    + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                    + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                    + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                    + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                    + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                    + "</Types>");
            masukkan(zip, "_rels/.rels", XML
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                    + "</Relationships>");
            masukkan(zip, "xl/workbook.xml", XML
                    + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                    + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                    + "<sheets><sheet name=\"Laporan\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            masukkan(zip, "xl/_rels/workbook.xml.rels", XML
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                    + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
                    + "</Relationships>");
            masukkan(zip, "xl/styles.xml", XML
                    + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                    + "<fonts count=\"3\">"
                    + "<font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                    + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                    + "<font><b/><sz val=\"14\"/><name val=\"Calibri\"/></font>"
                    + "</fonts>"
                    + "<fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill>"
                    + "<fill><patternFill patternType=\"gray125\"/></fill>"
                    + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFD9E1F2\"/><bgColor indexed=\"64\"/></patternFill></fill></fills>"
                    + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                    + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                    + "<cellXfs count=\"4\">"
                    + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
                    + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"
                    + "<xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"
                    + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\"/>"
                    + "</cellXfs>"
                    + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                    + "</styleSheet>");
            masukkan(zip, "xl/worksheets/sheet1.xml", sheet.toString());
        }
    }

    private static int tambahBaris(StringBuilder sb, int nomor, int gaya, int[] lebar, Object... sel) {
        sb.append("<row r=\"").append(nomor).append("\">");
        for (int i = 0; i < sel.length && i < lebar.length; i++) {
            Object nilai = sel[i];
            if (nilai == null) {
                continue;
            }
            String ref = kolom(i) + nomor;
            if (nilai instanceof Number) {
                sb.append("<c r=\"").append(ref).append("\" s=\"").append(gaya).append("\"><v>")
                        .append(nilai.toString()).append("</v></c>");
            } else {
                String teks = nilai.toString();
                // Hanya sel tabel (baris dengan banyak kolom) yang memengaruhi lebar kolom.
                if (sel.length > 1) {
                    lebar[i] = Math.max(lebar[i], teks.length());
                } else if (lebar[i] == 0) {
                    lebar[i] = 10;
                }
                sb.append("<c r=\"").append(ref).append("\" s=\"").append(gaya)
                        .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                        .append(escape(teks)).append("</t></is></c>");
            }
        }
        sb.append("</row>");
        return nomor + 1;
    }

    private static String kolom(int i) {
        return String.valueOf((char) ('A' + i));
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                default -> {
                    // Karakter kontrol tidak valid pada XML 1.0 dibuang.
                    if (ch >= 0x20 || ch == '\t' || ch == '\n' || ch == '\r') {
                        sb.append(ch);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static void masukkan(ZipOutputStream zip, String nama, String isi) throws IOException {
        zip.putNextEntry(new ZipEntry(nama));
        zip.write(isi.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}
