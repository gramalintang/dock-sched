package com.docksched.model;

/** Ketersediaan dock. TERISI diatur otomatis oleh sistem; RUSAK diatur Admin Gudang. */
public enum StatusDock {
    TERSEDIA("Tersedia"),
    TERISI("Terisi"),
    RUSAK("Rusak");

    private final String label;

    StatusDock(String label) {
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
