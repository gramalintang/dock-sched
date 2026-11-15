package com.docksched.model;

import java.time.LocalDateTime;

/** Catatan waktu setiap perubahan status truck (class LogStatus pada class diagram). */
public class LogStatus {

    private int idLog;
    private int idTruck;
    private Integer idPengguna;
    private StatusProses statusProses;
    private LocalDateTime waktu;
    private Integer durasiTahapDetik;

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdTruck() {
        return idTruck;
    }

    public void setIdTruck(int idTruck) {
        this.idTruck = idTruck;
    }

    public Integer getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(Integer idPengguna) {
        this.idPengguna = idPengguna;
    }

    public StatusProses getStatusProses() {
        return statusProses;
    }

    public void setStatusProses(StatusProses statusProses) {
        this.statusProses = statusProses;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public void setWaktu(LocalDateTime waktu) {
        this.waktu = waktu;
    }

    public Integer getDurasiTahapDetik() {
        return durasiTahapDetik;
    }

    public void setDurasiTahapDetik(Integer durasiTahapDetik) {
        this.durasiTahapDetik = durasiTahapDetik;
    }
}
