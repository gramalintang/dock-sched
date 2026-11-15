package com.docksched.util;

import java.time.format.DateTimeFormatter;

/** Konstanta aplikasi DOCK-SCHED. */
public final class Konstanta {

    private Konstanta() {
    }

    public static final String NAMA_APLIKASI = "DOCK-SCHED";
    public static final String SUB_JUDUL = "Sistem Penjadwalan Slot Loading-Unloading Truck Delivery di Gudang";

    /** Target maksimal proses dari truck parkir di dock sampai berangkat (DS-NF-14). */
    public static final int TARGET_MENIT = 45;

    public static final int SISA_MELEWATI_ESTIMASI_MENIT = 15;

    /** Batas tidak aktif sebelum logout otomatis (DS-F-01.5) = 30 menit. Properti sistem hanya untuk pengujian. */
    public static final int IDLE_MENIT = Integer.getInteger("docksched.idleMinutes", 30);

    /** Interval pembaruan dashboard (DS-NF-02: maksimal 10 detik). */
    public static final int REFRESH_DASHBOARD_DETIK = 5;

    public static final DateTimeFormatter FMT_WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter FMT_JAM = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter FMT_JAM_DETIK = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FMT_TANGGAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
