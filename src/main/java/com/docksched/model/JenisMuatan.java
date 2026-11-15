package com.docksched.model;

/** Klasifikasi muatan truck (DS-F-02.3). */
public enum JenisMuatan {
    BAHAN_BAKU_MASUK("Bahan Baku Masuk", "Unloading"),
    BARANG_JADI_KELUAR("Barang Jadi Keluar", "Loading");

    private final String label;
    private final String aktivitas;

    JenisMuatan(String label, String aktivitas) {
        this.label = label;
        this.aktivitas = aktivitas;
    }

    public String getLabel() {
        return label;
    }

    /** Unloading (bongkar bahan baku) atau Loading (muat barang jadi). */
    public String getAktivitas() {
        return aktivitas;
    }

    @Override
    public String toString() {
        return label;
    }
}
