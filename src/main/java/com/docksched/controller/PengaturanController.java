package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.PengaturanSpk;
import com.docksched.model.Pengguna;
import com.docksched.util.AplikasiException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/** Bobot kriteria SAW yang dapat diatur Admin Gudang (DS-F-03.2). */
public class PengaturanController {

    public PengaturanSpk ambil() throws SQLException {
        try (Connection c = Koneksi.buka()) {
            return ambil(c);
        }
    }

    public PengaturanSpk ambil(Connection c) throws SQLException {
        Map<String, Integer> nilai = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT kunci, nilai FROM pengaturan");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    nilai.put(rs.getString("kunci"), Integer.parseInt(rs.getString("nilai").trim()));
                } catch (NumberFormatException e) {
                    // Nilai rusak diabaikan; nilai bawaan dipakai.
                }
            }
        }
        PengaturanSpk p = new PengaturanSpk();
        p.setBobotMuatan(nilai.getOrDefault("bobot_muatan", p.getBobotMuatan()));
        p.setBobotDock(nilai.getOrDefault("bobot_dock", p.getBobotDock()));
        p.setBobotDurasi(nilai.getOrDefault("bobot_durasi", p.getBobotDurasi()));
        p.setNilaiBahanBaku(nilai.getOrDefault("nilai_bahan_baku", p.getNilaiBahanBaku()));
        p.setNilaiBarangJadi(nilai.getOrDefault("nilai_barang_jadi", p.getNilaiBarangJadi()));
        return p;
    }

    public void simpan(PengaturanSpk p, Pengguna user) throws SQLException {
        if (p.getBobotMuatan() < 0 || p.getBobotDock() < 0 || p.getBobotDurasi() < 0) {
            throw new AplikasiException("Bobot tidak boleh negatif.");
        }
        if (p.totalBobot() <= 0) {
            throw new AplikasiException("Total bobot harus lebih dari 0.");
        }
        if (p.getNilaiBahanBaku() < 1 || p.getNilaiBarangJadi() < 1) {
            throw new AplikasiException("Nilai prioritas muatan minimal 1.");
        }
        String sql = "INSERT INTO pengaturan (kunci, nilai) VALUES (?, ?) ON DUPLICATE KEY UPDATE nilai = VALUES(nilai)";
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                simpanBaris(ps, "bobot_muatan", p.getBobotMuatan());
                simpanBaris(ps, "bobot_dock", p.getBobotDock());
                simpanBaris(ps, "bobot_durasi", p.getBobotDurasi());
                simpanBaris(ps, "nilai_bahan_baku", p.getNilaiBahanBaku());
                simpanBaris(ps, "nilai_barang_jadi", p.getNilaiBarangJadi());
                Audit.catat(c, user.getIdPengguna(), "ATUR_BOBOT", "Bobot SAW muatan/dock/durasi = "
                        + p.getBobotMuatan() + "/" + p.getBobotDock() + "/" + p.getBobotDurasi()
                        + "; nilai muatan bahan baku/barang jadi = " + p.getNilaiBahanBaku() + "/" + p.getNilaiBarangJadi());
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private void simpanBaris(PreparedStatement ps, String kunci, int nilai) throws SQLException {
        ps.setString(1, kunci);
        ps.setString(2, String.valueOf(nilai));
        ps.executeUpdate();
    }
}
