package com.docksched.model;

import com.docksched.util.Konstanta;
import java.time.Duration;
import java.time.LocalDateTime;

/** Truck delivery yang akan loading/unloading (class Truck pada class diagram). */
public class Truck {

    private int idTruck;
    private String nomorPolisi;
    private String namaSopir;
    private JenisMuatan jenisMuatan;
    private String pemasokPelanggan;
    private LocalDateTime perkiraanKedatangan;
    private int estimasiDurasiMenit;
    private StatusProses statusProses = StatusProses.MENUNGGU;
    private LocalDateTime waktuPendaftaran;
    private boolean diarsipkan;
    private String kodeDock;
    private LocalDateTime waktuParkir;
    private LocalDateTime waktuSelesai;
    private LocalDateTime waktuTahapSekarang;

    public int getIdTruck() {
        return idTruck;
    }

    public void setIdTruck(int idTruck) {
        this.idTruck = idTruck;
    }

    public String getNomorPolisi() {
        return nomorPolisi;
    }

    public void setNomorPolisi(String nomorPolisi) {
        this.nomorPolisi = nomorPolisi;
    }

    public String getNamaSopir() {
        return namaSopir;
    }

    public void setNamaSopir(String namaSopir) {
        this.namaSopir = namaSopir;
    }

    public JenisMuatan getJenisMuatan() {
        return jenisMuatan;
    }

    public void setJenisMuatan(JenisMuatan jenisMuatan) {
        this.jenisMuatan = jenisMuatan;
    }

    public String getPemasokPelanggan() {
        return pemasokPelanggan;
    }

    public void setPemasokPelanggan(String pemasokPelanggan) {
        this.pemasokPelanggan = pemasokPelanggan;
    }

    public LocalDateTime getPerkiraanKedatangan() {
        return perkiraanKedatangan;
    }

    public void setPerkiraanKedatangan(LocalDateTime perkiraanKedatangan) {
        this.perkiraanKedatangan = perkiraanKedatangan;
    }

    public int getEstimasiDurasiMenit() {
        return estimasiDurasiMenit;
    }

    public void setEstimasiDurasiMenit(int estimasiDurasiMenit) {
        this.estimasiDurasiMenit = estimasiDurasiMenit;
    }

    public StatusProses getStatusProses() {
        return statusProses;
    }

    public void setStatusProses(StatusProses statusProses) {
        this.statusProses = statusProses;
    }

    public LocalDateTime getWaktuPendaftaran() {
        return waktuPendaftaran;
    }

    public void setWaktuPendaftaran(LocalDateTime waktuPendaftaran) {
        this.waktuPendaftaran = waktuPendaftaran;
    }

    public boolean isDiarsipkan() {
        return diarsipkan;
    }

    public void setDiarsipkan(boolean diarsipkan) {
        this.diarsipkan = diarsipkan;
    }

    public String getKodeDock() {
        return kodeDock;
    }

    public void setKodeDock(String kodeDock) {
        this.kodeDock = kodeDock;
    }

    public LocalDateTime getWaktuParkir() {
        return waktuParkir;
    }

    public void setWaktuParkir(LocalDateTime waktuParkir) {
        this.waktuParkir = waktuParkir;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(LocalDateTime waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public LocalDateTime getWaktuTahapSekarang() {
        return waktuTahapSekarang;
    }

    public void setWaktuTahapSekarang(LocalDateTime waktuTahapSekarang) {
        this.waktuTahapSekarang = waktuTahapSekarang;
    }
    /**
     * Total durasi proses (detik) dari truck parkir di dock (DS-NF-14) sampai berangkat, atau sampai
     * {@code sekarang} bila belum selesai. -1 bila truck belum parkir.
     */
    public long durasiProsesDetik(LocalDateTime sekarang) {
        if (waktuParkir == null) {
            return -1;
        }
        LocalDateTime akhir = waktuSelesai != null ? waktuSelesai : sekarang;
        return Math.max(0, Duration.between(waktuParkir, akhir).getSeconds());
    }

    /** True bila total durasi proses melebihi target 45 menit (DS-F-04.4, DS-F-05.2). */
    public boolean melebihiTarget(LocalDateTime sekarang) {
        return durasiProsesDetik(sekarang) > Konstanta.TARGET_MENIT * 60L;
    }

    @Override
    public String toString() {
        return nomorPolisi;
    }
}
