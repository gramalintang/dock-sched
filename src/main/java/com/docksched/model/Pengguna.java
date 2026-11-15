package com.docksched.model;

import java.time.LocalDateTime;

/** Akun pengguna aplikasi (class Pengguna pada class diagram). */
public class Pengguna {

    private int idPengguna;
    private String username;
    private String passwordHash;
    private String namaLengkap;
    private Peran peran;
    private boolean aktif = true;
    private boolean programmer;
    private LocalDateTime dibuatPada;

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public Peran getPeran() {
        return peran;
    }

    public void setPeran(Peran peran) {
        this.peran = peran;
    }

    public boolean isProgrammer() { return programmer; }

    public void setProgrammer(boolean programmer) { this.programmer = programmer; }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public LocalDateTime getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(LocalDateTime dibuatPada) {
        this.dibuatPada = dibuatPada;
    }
    @Override
    public String toString() {
        return namaLengkap + " (" + peran.getLabel() + ")";
    }
}
