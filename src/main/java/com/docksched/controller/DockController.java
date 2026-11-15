package com.docksched.controller;

import com.docksched.config.Koneksi;
import com.docksched.model.Dock;
import com.docksched.model.JenisMuatan;
import com.docksched.model.Pengguna;
import com.docksched.model.StatusDock;
import com.docksched.util.AplikasiException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Master data dock (dikelola Admin Gudang). Dock tidak dihapus agar riwayat tetap utuh (DS-NF-04). */
public class DockController {

    private static final String SELECT = "SELECT id_dock, kode_dock, kapasitas, jenis_muatan, dock_serbaguna, "
            + "status_ketersediaan FROM dock ";

    static Dock petakan(ResultSet rs) throws SQLException {
        Dock d = new Dock();
        d.setIdDock(rs.getInt("id_dock"));
        d.setKodeDock(rs.getString("kode_dock"));
        d.setKapasitas(rs.getInt("kapasitas"));
        d.setJenisMuatan(JenisMuatan.valueOf(rs.getString("jenis_muatan")));
        d.setSerbaguna(rs.getBoolean("dock_serbaguna"));
        d.setStatus(StatusDock.valueOf(rs.getString("status_ketersediaan")));
        return d;
    }

    public List<Dock> daftar() throws SQLException {
        try (Connection c = Koneksi.buka()) {
            return daftar(c);
        }
    }

    public List<Dock> daftar(Connection c) throws SQLException {
        List<Dock> hasil = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY kode_dock");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        }
        return hasil;
    }

    private void validasi(Dock d) {
        if (d.getKodeDock() == null || d.getKodeDock().trim().isEmpty()) {
            throw new AplikasiException("Kode dock wajib diisi.");
        }
        if (d.getKodeDock().trim().length() > 20) {
            throw new AplikasiException("Kode dock maksimal 20 karakter.");
        }
        if (d.getKapasitas() < 1) {
            throw new AplikasiException("Kapasitas dock minimal 1.");
        }
        if (d.getJenisMuatan() == null) {
            throw new AplikasiException("Jenis muatan dock wajib dipilih.");
        }
        d.setKodeDock(d.getKodeDock().trim().toUpperCase());
    }

    public void tambah(Dock d, Pengguna user) throws SQLException {
        validasi(d);
        String sql = "INSERT INTO dock (kode_dock, kapasitas, jenis_muatan, dock_serbaguna, status_ketersediaan) "
                + "VALUES (?, ?, ?, ?, 'TERSEDIA')";
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                if (kodeAda(c, d.getKodeDock(), 0)) {
                    throw new AplikasiException("Kode dock " + d.getKodeDock() + " sudah dipakai.");
                }
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, d.getKodeDock());
                    ps.setInt(2, d.getKapasitas());
                    ps.setString(3, d.getJenisMuatan().name());
                    ps.setBoolean(4, d.isSerbaguna());
                    ps.executeUpdate();
                }
                Audit.catat(c, user.getIdPengguna(), "TAMBAH_DOCK", "Dock baru: " + d.getKodeDock());
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Memperbarui dock. Status TERISI diatur sistem; Admin hanya boleh memilih Tersedia atau Rusak. */
    public void perbarui(Dock d, Pengguna user) throws SQLException {
        validasi(d);
        try (Connection c = Koneksi.buka()) {
            c.setAutoCommit(false);
            try {
                StatusDock statusLama = null;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT status_ketersediaan FROM dock WHERE id_dock = ? FOR UPDATE")) {
                    ps.setInt(1, d.getIdDock());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            statusLama = StatusDock.valueOf(rs.getString(1));
                        }
                    }
                }
                if (statusLama == null) {
                    throw new AplikasiException("Dock tidak ditemukan.");
                }
                if (kodeAda(c, d.getKodeDock(), d.getIdDock())) {
                    throw new AplikasiException("Kode dock " + d.getKodeDock() + " sudah dipakai.");
                }
                StatusDock statusBaru = d.getStatus();
                if (statusLama == StatusDock.TERISI) {
                    statusBaru = StatusDock.TERISI;
                } else if (statusBaru == StatusDock.TERISI) {
                    throw new AplikasiException("Status Terisi diatur otomatis oleh sistem saat truck parkir di dock.");
                }
                String sql = "UPDATE dock SET kode_dock = ?, kapasitas = ?, jenis_muatan = ?, dock_serbaguna = ?, "
                        + "status_ketersediaan = ? WHERE id_dock = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, d.getKodeDock());
                    ps.setInt(2, d.getKapasitas());
                    ps.setString(3, d.getJenisMuatan().name());
                    ps.setBoolean(4, d.isSerbaguna());
                    ps.setString(5, statusBaru.name());
                    ps.setInt(6, d.getIdDock());
                    ps.executeUpdate();
                }
                Audit.catat(c, user.getIdPengguna(), "UBAH_DOCK", "Dock " + d.getKodeDock() + " status "
                        + statusBaru.getLabel() + ", kapasitas " + d.getKapasitas()
                        + (d.isSerbaguna() ? ", serbaguna" : ", khusus " + d.getJenisMuatan().getLabel()));
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private boolean kodeAda(Connection c, String kode, int kecualiId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM dock WHERE kode_dock = ? AND id_dock <> ?")) {
            ps.setString(1, kode);
            ps.setInt(2, kecualiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
