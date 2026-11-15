package com.docksched.model;

/** Siklus hidup satu catatan jadwal (setiap proses penjadwalan dicatat, DS-F-03.6). */
public enum StatusJadwal {
    AKTIF("Aktif"),
    DIGANTI("Diganti"),
    BATAL("Batal"),
    SELESAI("Selesai");

    private final String label;

    StatusJadwal(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
