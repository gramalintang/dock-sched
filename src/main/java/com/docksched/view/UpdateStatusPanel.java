package com.docksched.view;

import com.docksched.controller.Sesi;
import com.docksched.controller.StatusController;
import com.docksched.controller.StatusController.HasilUbahStatus;
import com.docksched.controller.TruckController;
import com.docksched.model.Pengguna;
import com.docksched.model.StatusProses;
import com.docksched.model.Truck;
import com.docksched.util.BackgroundTask;
import com.docksched.util.Format;
import com.docksched.util.Konstanta;
import com.docksched.util.RefreshBerkala;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/** Update status proses truck oleh Petugas Dock (UC-09, UC-10, UC-11; DS-F-04.1 - 04.5). Dibuat dengan NetBeans GUI Builder. */
public class UpdateStatusPanel extends javax.swing.JPanel implements Halaman {

    private static final long serialVersionUID = 1L;

    private final TruckController truckController = new TruckController();
    private final StatusController statusController = new StatusController();
    private final DefaultTableModel model = UiUtil.modelTabel("ID", "Nomor Polisi", "Sopir", "Jenis Muatan",
            "Status Saat Ini", "Dock", "Durasi Proses", "Perkiraan Datang");
    private final List<Truck> daftar = new ArrayList<>();
    private final boolean boleh;

    public UpdateStatusPanel() {
        initComponents();
        Pengguna user = Sesi.getPengguna();
        boleh = user != null && (user.isProgrammer() || user.getPeran().bisaUbahStatus());
        lblPetunjuk.setText("<html>Urutan status: Menunggu &rarr; Parkir Dock &rarr; Loading/Unloading &rarr; "
                + "Administrasi &rarr; Selesai/Berangkat. Tahap tidak boleh dilompati. Baris <font color='#A00000'>merah</font> "
                + "= durasi proses melebihi " + Konstanta.TARGET_MENIT + " menit."
                + (boleh ? "" : "<br><b>Anda hanya dapat melihat; perubahan status hanya oleh Petugas Dock.</b>") + "</html>");
        TabelData.pasang(tabel, model);
        tabel.getColumnModel().getColumn(0).setMaxWidth(50);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                perbaruiTombol();
            }
        });
        // Durasi proses diperbarui otomatis berkala (DS-NF-02) selama halaman terlihat.
        RefreshBerkala.pasang(this, Konstanta.REFRESH_DASHBOARD_DETIK, this::muat);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblPetunjuk = new javax.swing.JLabel();
        scrollTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlBawah = new javax.swing.JPanel();
        pnlTombol = new javax.swing.JPanel();
        btnUbah = new javax.swing.JButton();
        btnSegarkan = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new java.awt.BorderLayout(0, 8));
        pnlAtas.setLayout(new java.awt.BorderLayout());
        lblJudul.setText("Update Status Proses Truck");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblJudul.setForeground(new java.awt.Color(31, 78, 121));
        pnlAtas.add(lblJudul, java.awt.BorderLayout.NORTH);
        lblPetunjuk.setText("Urutan status: Menunggu > Parkir Dock > Loading/Unloading > Administrasi > Selesai/Berangkat.");
        pnlAtas.add(lblPetunjuk, java.awt.BorderLayout.SOUTH);
        add(pnlAtas, java.awt.BorderLayout.NORTH);
        scrollTabel.setViewportView(tabel);
        add(scrollTabel, java.awt.BorderLayout.CENTER);
        pnlBawah.setLayout(new java.awt.BorderLayout());
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnUbah.setText("Pilih truck dahulu");
        btnUbah.setEnabled(false);
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        pnlTombol.add(btnUbah);
        btnSegarkan.setText("Segarkan");
        btnSegarkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSegarkanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnSegarkan);
        pnlBawah.add(pnlTombol, java.awt.BorderLayout.NORTH);
        lblInfo.setText(" ");
        pnlBawah.add(lblInfo, java.awt.BorderLayout.SOUTH);
        add(pnlBawah, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubah();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnSegarkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSegarkanActionPerformed
        muat();
    }//GEN-LAST:event_btnSegarkanActionPerformed

    @Override
    public void saatDitampilkan() {
        muat();
    }

    private Truck terpilih() {
        int i = TabelData.barisModelTerpilih(tabel);
        return i < 0 || i >= daftar.size() ? null : daftar.get(i);
    }

    private void perbaruiTombol() {
        Truck t = terpilih();
        StatusProses berikut = t == null ? null : t.getStatusProses().berikutnya();
        if (t == null || berikut == null) {
            btnUbah.setText("Pilih truck dahulu");
            btnUbah.setEnabled(false);
        } else {
            btnUbah.setText("Ubah status ke: " + berikut.getLabel());
            btnUbah.setEnabled(boleh);
        }
    }

    private void muat() {
        Truck sebelumnya = terpilih();
        Integer idLama = sebelumnya == null ? null : sebelumnya.getIdTruck();
        BackgroundTask.<List<Truck>>jalankan(truckController::daftarBerjalan, data -> {
            LocalDateTime sekarang = LocalDateTime.now();
            daftar.clear();
            daftar.addAll(data);
            model.setRowCount(0);
            Set<Integer> merah = new HashSet<>();
            for (int i = 0; i < data.size(); i++) {
                Truck t = data.get(i);
                if (t.melebihiTarget(sekarang)) {
                    merah.add(i);
                }
                model.addRow(new Object[] {t.getIdTruck(), t.getNomorPolisi(), t.getNamaSopir(),
                    t.getJenisMuatan().getLabel(), t.getStatusProses().getLabel(),
                    t.getKodeDock() == null ? "-" : t.getKodeDock(), Format.durasi(t.durasiProsesDetik(sekarang)),
                    Format.waktu(t.getPerkiraanKedatangan())});
            }
            TabelData.setBarisMerah(tabel, merah);
            if (idLama != null) {
                TabelData.pilihBerdasarkan(tabel, 0, idLama);
            }
            perbaruiTombol();
        }, err -> {
            lblInfo.setForeground(UiUtil.MERAH);
            lblInfo.setText("Gagal memuat data: " + UiUtil.ringkas(err.getMessage()));
        });
    }

    private void ubah() {
        Truck t = terpilih();
        if (t == null) {
            return;
        }
        StatusProses tujuan = t.getStatusProses().berikutnya();
        if (tujuan == null) {
            return;
        }
        if (!UiUtil.konfirmasi(this, "Ubah status truck " + t.getNomorPolisi() + " dari \""
                + t.getStatusProses().getLabel() + "\" ke \"" + tujuan.getLabel() + "\"?")) {
            return;
        }
        btnUbah.setEnabled(false);
        BackgroundTask.<HasilUbahStatus>jalankan(() -> statusController.ubahStatus(t.getIdTruck(), tujuan, Sesi.getPengguna()),
                h -> {
                    lblInfo.setForeground(UiUtil.HIJAU);
                    lblInfo.setText(h.nomorPolisi() + ": " + h.statusLama().getLabel() + " -> " + h.statusBaru().getLabel()
                            + " (durasi tahap sebelumnya " + Format.durasi(h.durasiTahapDetik()) + ")");
                    if (h.melebihiTarget()) {
                        UiUtil.peringatan(this, "Truck " + h.nomorPolisi() + " melebihi target " + Konstanta.TARGET_MENIT
                                + " menit. Total durasi proses: " + Format.durasi(h.totalDetik()) + ".");
                    }
                    muat();
                }, err -> {
                    UiUtil.tampilkanError(this, err);
                    muat();
                });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblPetunjuk;
    private javax.swing.JScrollPane scrollTabel;
    private javax.swing.JTable tabel;
    private javax.swing.JPanel pnlBawah;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JLabel lblInfo;
    // End of variables declaration//GEN-END:variables
}
