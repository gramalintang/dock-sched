package com.docksched.model;

/** Bobot kriteria SAW dan nilai prioritas muatan yang diatur Admin Gudang (DS-F-03.2, TBD-1). */
public class PengaturanSpk {

    private int bobotMuatan = 50;
    private int bobotDock = 30;
    private int bobotDurasi = 20;
    private int nilaiBahanBaku = 5;
    private int nilaiBarangJadi = 3;

    public int getBobotMuatan() {
        return bobotMuatan;
    }

    public void setBobotMuatan(int bobotMuatan) {
        this.bobotMuatan = bobotMuatan;
    }

    public int getBobotDock() {
        return bobotDock;
    }

    public void setBobotDock(int bobotDock) {
        this.bobotDock = bobotDock;
    }

    public int getBobotDurasi() {
        return bobotDurasi;
    }

    public void setBobotDurasi(int bobotDurasi) {
        this.bobotDurasi = bobotDurasi;
    }

    public int getNilaiBahanBaku() {
        return nilaiBahanBaku;
    }

    public void setNilaiBahanBaku(int nilaiBahanBaku) {
        this.nilaiBahanBaku = nilaiBahanBaku;
    }

    public int getNilaiBarangJadi() {
        return nilaiBarangJadi;
    }

    public void setNilaiBarangJadi(int nilaiBarangJadi) {
        this.nilaiBarangJadi = nilaiBarangJadi;
    }
    public int totalBobot() {
        return bobotMuatan + bobotDock + bobotDurasi;
    }

    public int nilaiMuatan(JenisMuatan jenis) {
        return jenis == JenisMuatan.BAHAN_BAKU_MASUK ? nilaiBahanBaku : nilaiBarangJadi;
    }
}
