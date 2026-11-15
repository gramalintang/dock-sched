package com.docksched.util;

/** Pelanggaran aturan bisnis; pesannya berbahasa Indonesia dan boleh langsung ditampilkan ke pengguna. */
public class AplikasiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AplikasiException(String pesan) {
        super(pesan);
    }
}
