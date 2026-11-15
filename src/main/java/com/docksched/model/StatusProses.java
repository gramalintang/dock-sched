package com.docksched.model;

/** Lima status proses truck sesuai state machine SRS 5 (DS-F-04.1). */
public enum StatusProses {
    MENUNGGU("Menunggu"),
    PARKIR_DOCK("Parkir Dock"),
    LOADING_UNLOADING("Loading/Unloading"),
    ADMINISTRASI("Administrasi"),
    SELESAI_BERANGKAT("Selesai/Berangkat");

    private final String label;

    StatusProses(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Status berikutnya; null bila sudah di status akhir. */
    public StatusProses berikutnya() {
        StatusProses[] semua = values();
        int i = ordinal() + 1;
        return i < semua.length ? semua[i] : null;
    }

    @Override
    public String toString() {
        return label;
    }
}
