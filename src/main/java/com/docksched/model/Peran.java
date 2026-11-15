package com.docksched.model;

/** Peran pengguna sesuai SRS 2.2 beserta hak aksesnya (DS-F-01.3, DS-NF-07). */
public enum Peran {
    ADMIN_GUDANG("Admin Gudang"),
    PETUGAS_DOCK("Petugas Dock"),
    KEPALA_GUDANG("Kepala Gudang");

    private final String label;

    Peran(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Input data truck (UC-02) serta cari/sunting (UC-03, UC-04). */
    public boolean bisaKelolaTruck() {
        return this == ADMIN_GUDANG || this == PETUGAS_DOCK;
    }

    /** Menjalankan penjadwalan dan melihat hasilnya (UC-05, UC-06, UC-07). */
    public boolean bisaPenjadwalan() {
        return this == ADMIN_GUDANG || this == PETUGAS_DOCK;
    }

    /** Menjadwalkan ulang manual (UC-08): hanya Admin Gudang. */
    public boolean bisaJadwalUlang() {
        return this == ADMIN_GUDANG;
    }

    /** Mengubah status proses: hanya Petugas Dock (DS-NF-15). */
    public boolean bisaUbahStatus() {
        return this == PETUGAS_DOCK;
    }

    /** Membuka menu Update Status (Admin hanya melihat). */
    public boolean bisaLihatStatus() {
        return this == ADMIN_GUDANG || this == PETUGAS_DOCK;
    }

    /** Dashboard dan laporan (UC-12, UC-13, UC-14). */
    public boolean bisaDashboardLaporan() {
        return this == ADMIN_GUDANG || this == KEPALA_GUDANG;
    }

    /** Kelola akun, master data, dan bobot kriteria: Admin Gudang. */
    public boolean bisaMasterData() {
        return this == ADMIN_GUDANG;
    }

    @Override
    public String toString() {
        return label;
    }
}
