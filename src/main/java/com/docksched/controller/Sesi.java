package com.docksched.controller;

import com.docksched.model.Pengguna;

/** Pengguna yang sedang login pada aplikasi desktop ini. */
public final class Sesi {

    private static Pengguna pengguna;

    private Sesi() {
    }

    public static synchronized Pengguna getPengguna() {
        return pengguna;
    }

    public static synchronized void mulai(Pengguna p) {
        pengguna = p;
    }

    public static synchronized void akhiri() {
        pengguna = null;
    }
}
