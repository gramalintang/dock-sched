package com.docksched.model;

import java.time.LocalDateTime;

/** Satu catatan keputusan penjadwalan truck ke dock (class Jadwal pada class diagram). */
public class Jadwal {

    private int idJadwal;
    private int idTruck;
    private int idDock;
    private double skorPrioritas;
    private int urutan;
    private LocalDateTime estimasiWaktuMasuk;
    private LocalDateTime estimasiWaktuSelesai;
    private LocalDateTime waktuKeputusan;
    private JenisJadwal jenis = JenisJadwal.OTOMATIS;
    private StatusJadwal status = StatusJadwal.AKTIF;
    private Integer idPengguna;
    private String keterangan;
    private String nomorPolisi;
    private String namaSopir;
    private JenisMuatan jenisMuatan;
    private String kodeDock;
    private StatusProses statusTruck;

    public int getIdJadwal() {
        return idJadwal;
    }

    public void setIdJadwal(int idJadwal) {
        this.idJadwal = idJadwal;
    }

    public int getIdTruck() {
        return idTruck;
    }

    public void setIdTruck(int idTruck) {
        this.idTruck = idTruck;
    }

    public int getIdDock() {
        return idDock;
    }

    public void setIdDock(int idDock) {
        this.idDock = idDock;
    }

    public double getSkorPrioritas() {
        return skorPrioritas;
    }

    public void setSkorPrioritas(double skorPrioritas) {
        this.skorPrioritas = skorPrioritas;
    }

    public int getUrutan() {
        return urutan;
    }

    public void setUrutan(int urutan) {
        this.urutan = urutan;
    }

    public LocalDateTime getEstimasiWaktuMasuk() {
        return estimasiWaktuMasuk;
    }

    public void setEstimasiWaktuMasuk(LocalDateTime estimasiWaktuMasuk) {
        this.estimasiWaktuMasuk = estimasiWaktuMasuk;
    }

    public LocalDateTime getEstimasiWaktuSelesai() {
        return estimasiWaktuSelesai;
    }

    public void setEstimasiWaktuSelesai(LocalDateTime estimasiWaktuSelesai) {
        this.estimasiWaktuSelesai = estimasiWaktuSelesai;
    }

    public LocalDateTime getWaktuKeputusan() {
        return waktuKeputusan;
    }

    public void setWaktuKeputusan(LocalDateTime waktuKeputusan) {
        this.waktuKeputusan = waktuKeputusan;
    }

    public JenisJadwal getJenis() {
        return jenis;
    }

    public void setJenis(JenisJadwal jenis) {
        this.jenis = jenis;
    }

    public StatusJadwal getStatus() {
        return status;
    }

    public void setStatus(StatusJadwal status) {
        this.status = status;
    }

    public Integer getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(Integer idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
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

    public String getKodeDock() {
        return kodeDock;
    }

    public void setKodeDock(String kodeDock) {
        this.kodeDock = kodeDock;
    }

    public StatusProses getStatusTruck() {
        return statusTruck;
    }

    public void setStatusTruck(StatusProses statusTruck) {
        this.statusTruck = statusTruck;
    }
}
