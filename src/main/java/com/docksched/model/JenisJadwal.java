package com.docksched.model;

/** Asal keputusan jadwal: dihitung otomatis (SAW) atau diatur manual oleh Admin Gudang. */
public enum JenisJadwal {
    OTOMATIS("Otomatis (SAW)"),
    MANUAL("Manual (Admin)");

    private final String label;

    JenisJadwal(String label) {
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
