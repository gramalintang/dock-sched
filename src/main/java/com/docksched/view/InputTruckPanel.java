package com.docksched.view;

import com.docksched.controller.Sesi;
import com.docksched.controller.TruckController;
import com.docksched.model.Truck;
import com.docksched.util.AplikasiException;
import com.docksched.util.BackgroundTask;
import java.awt.BorderLayout;

/** Input data truck & permintaan slot (UC-02; DS-F-02.1 - 02.4). Dibuat dengan NetBeans GUI Builder. */
public class InputTruckPanel extends javax.swing.JPanel implements Halaman {

    private static final long serialVersionUID = 1L;

    private final TruckController controller = new TruckController();
    private final FormTruck form = new FormTruck();

    public InputTruckPanel() {
        initComponents();
        pnlDataTruck.add(form, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblPetunjuk = new javax.swing.JLabel();
        pnlIsi = new javax.swing.JPanel();
        pnlDataTruck = new javax.swing.JPanel();
        pnlBawah = new javax.swing.JPanel();
        pnlTombol = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new java.awt.BorderLayout(0, 10));
        pnlAtas.setLayout(new java.awt.BorderLayout());
        lblJudul.setText("Input Data Truck & Permintaan Slot");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblJudul.setForeground(new java.awt.Color(31, 78, 121));
        pnlAtas.add(lblJudul, java.awt.BorderLayout.NORTH);
        lblPetunjuk.setText("<html>Isi seluruh kolom bertanda *. Setelah disimpan, truck berstatus <b>Menunggu</b> dan siap dijadwalkan pada menu Penjadwalan Slot Dock.</html>");
        pnlAtas.add(lblPetunjuk, java.awt.BorderLayout.SOUTH);
        add(pnlAtas, java.awt.BorderLayout.NORTH);
        pnlIsi.setLayout(new java.awt.BorderLayout());
        pnlDataTruck.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Truck"));
        pnlDataTruck.setLayout(new java.awt.BorderLayout());
        pnlIsi.add(pnlDataTruck, java.awt.BorderLayout.NORTH);
        add(pnlIsi, java.awt.BorderLayout.CENTER);
        pnlBawah.setLayout(new java.awt.BorderLayout());
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnSimpan);
        btnBersihkan.setText("Bersihkan Form");
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnBersihkan);
        pnlBawah.add(pnlTombol, java.awt.BorderLayout.NORTH);
        lblStatus.setText(" ");
        pnlBawah.add(lblStatus, java.awt.BorderLayout.SOUTH);
        add(pnlBawah, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpan();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        form.bersihkan();
        lblStatus.setText(" ");
        form.fokusAwal();
    }//GEN-LAST:event_btnBersihkanActionPerformed

    @Override
    public void saatDitampilkan() {
        form.fokusAwal();
    }

    private void simpan() {
        Truck t = form.ambilTruck();
        try {
            controller.validasi(t); // validasi cepat di UI: field yang salah ditandai merah
        } catch (AplikasiException e) {
            form.tandaiDariPesan(e.getMessage());
            lblStatus.setForeground(UiUtil.MERAH);
            lblStatus.setText("<html>" + e.getMessage().replace("\n", "<br>") + "</html>");
            return;
        }
        form.hapusTanda();
        btnSimpan.setEnabled(false);
        BackgroundTask.<Integer>jalankan(() -> controller.simpan(t, Sesi.getPengguna()), id -> {
            btnSimpan.setEnabled(true);
            lblStatus.setForeground(UiUtil.HIJAU);
            lblStatus.setText("Truck " + t.getNomorPolisi() + " tersimpan dengan status Menunggu.");
            UiUtil.info(this, "Data truck " + t.getNomorPolisi() + " berhasil disimpan.\nStatus: Menunggu.");
            form.bersihkan();
            form.fokusAwal();
        }, err -> {
            btnSimpan.setEnabled(true);
            lblStatus.setForeground(UiUtil.MERAH);
            lblStatus.setText(" ");
            if (err instanceof AplikasiException) {
                form.tandaiDariPesan(err.getMessage());
            }
            UiUtil.tampilkanError(this, err);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblPetunjuk;
    private javax.swing.JPanel pnlIsi;
    private javax.swing.JPanel pnlDataTruck;
    private javax.swing.JPanel pnlBawah;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}
