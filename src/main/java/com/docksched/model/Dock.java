package com.docksched.model;

/** Dock loading-unloading di gudang (class Dock pada class diagram). */
public class Dock {

    private int idDock;
    private String kodeDock;
    private int kapasitas;
    private JenisMuatan jenisMuatan;
    private boolean serbaguna;
    private StatusDock status = StatusDock.TERSEDIA;

    public int getIdDock() {
        return idDock;
    }

    public void setIdDock(int idDock) {
        this.idDock = idDock;
    }

    public String getKodeDock() {
        return kodeDock;
    }

    public void setKodeDock(String kodeDock) {
        this.kodeDock = kodeDock;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public JenisMuatan getJenisMuatan() {
        return jenisMuatan;
    }

    public void setJenisMuatan(JenisMuatan jenisMuatan) {
        this.jenisMuatan = jenisMuatan;
    }

    public boolean isSerbaguna() {
        return serbaguna;
    }

    public void setSerbaguna(boolean serbaguna) {
        this.serbaguna = serbaguna;
    }

    public StatusDock getStatus() {
        return status;
    }

    public void setStatus(StatusDock status) {
        this.status = status;
    }
    /** Kesesuaian jenis muatan (DS-NF-13): dock khusus hanya untuk jenisnya, dock serbaguna untuk keduanya. */
    public boolean cocokUntuk(JenisMuatan jenis) {
        return serbaguna || jenisMuatan == jenis;
    }

    /** Dock yang tidak rusak boleh dijadwalkan. */
    public boolean berfungsi() {
        return status != StatusDock.RUSAK;
    }

    public String getKeteranganJenis() {
        return serbaguna ? "Serbaguna" : jenisMuatan.getLabel();
    }

    @Override
    public String toString() {
        return kodeDock + " (" + getKeteranganJenis() + ")";
    }
}
