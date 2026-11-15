package com.docksched.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Isi laporan yang netral terhadap format, dipakai oleh ekspor PDF dan Excel (DS-F-05.5). */
public class LaporanDokumen {

    /** Satu tabel dalam laporan. Sel boleh berupa String atau Number. */
    public static class Bagian {

        private final String judul;
        private final List<String> kolom;
        private final List<List<Object>> baris = new ArrayList<>();

        Bagian(String judul, String... kolom) {
            this.judul = judul;
            this.kolom = Arrays.asList(kolom);
        }

        public Bagian baris(Object... sel) {
            baris.add(new ArrayList<>(Arrays.asList(sel)));
            return this;
        }

        public String getJudul() {
            return judul;
        }

        public List<String> getKolom() {
            return kolom;
        }

        public List<List<Object>> getBaris() {
            return baris;
        }
    }

    private final String judul;
    private final String subjudul;
    private final String catatan;
    private final List<Bagian> bagian = new ArrayList<>();

    public LaporanDokumen(String judul, String subjudul, String catatan) {
        this.judul = judul;
        this.subjudul = subjudul;
        this.catatan = catatan;
    }

    public Bagian tambahBagian(String judulBagian, String... kolom) {
        Bagian b = new Bagian(judulBagian, kolom);
        bagian.add(b);
        return b;
    }

    public String getJudul() {
        return judul;
    }

    public String getSubjudul() {
        return subjudul;
    }

    public String getCatatan() {
        return catatan;
    }

    public List<Bagian> getBagian() {
        return bagian;
    }
}
