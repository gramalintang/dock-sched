package com.docksched.view;

/** Halaman yang ditampilkan di area isi MainFrame. */
public interface Halaman {

    /** Dipanggil setiap kali halaman dibuka dari menu (untuk memuat ulang data). */
    default void saatDitampilkan() {
    }
}
