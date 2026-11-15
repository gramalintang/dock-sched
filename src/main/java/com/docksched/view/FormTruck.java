package com.docksched.view;

import com.docksched.model.JenisMuatan;
import com.docksched.model.Truck;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.swing.DefaultComboBoxModel;

/** Formulir data truck: nomor polisi, sopir, jenis muatan, pemasok/pelanggan, ETA, estimasi durasi (DS-F-02.1). */
public class FormTruck extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    public FormTruck() {
        initComponents();
        cbJenis.setModel(new DefaultComboBoxModel<>(JenisMuatan.values()));
        UiUtil.aturSpinnerWaktu(spnKedatangan, LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES));
        UiUtil.aturSpinnerAngka(spnDurasi, 30, 1, 600, 5);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        lblPlat = new javax.swing.JLabel();
        txtPlat = new javax.swing.JTextField();
        lblSopir = new javax.swing.JLabel();
        txtSopir = new javax.swing.JTextField();
        lblJenis = new javax.swing.JLabel();
        cbJenis = new javax.swing.JComboBox<>();
        lblPemasok = new javax.swing.JLabel();
        txtPemasok = new javax.swing.JTextField();
        lblKedatangan = new javax.swing.JLabel();
        spnKedatangan = new javax.swing.JSpinner();
        lblDurasi = new javax.swing.JLabel();
        spnDurasi = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());
        lblPlat.setText("Nomor polisi *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        add(lblPlat, gridBagConstraints);
        txtPlat.setColumns(20);
        txtPlat.setToolTipText("Contoh: B 1234 XYZ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(txtPlat, gridBagConstraints);
        lblSopir.setText("Nama sopir *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        add(lblSopir, gridBagConstraints);
        txtSopir.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(txtSopir, gridBagConstraints);
        lblJenis.setText("Jenis muatan *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        add(lblJenis, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(cbJenis, gridBagConstraints);
        lblPemasok.setText("Pemasok / pelanggan *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        add(lblPemasok, gridBagConstraints);
        txtPemasok.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(txtPemasok, gridBagConstraints);
        lblKedatangan.setText("Perkiraan waktu kedatangan *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        add(lblKedatangan, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(spnKedatangan, gridBagConstraints);
        lblDurasi.setText("Estimasi durasi bongkar-muat (menit) *");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        add(lblDurasi, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        add(spnDurasi, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public Truck ambilTruck() {
        Truck t = new Truck();
        t.setNomorPolisi(txtPlat.getText());
        t.setNamaSopir(txtSopir.getText());
        t.setJenisMuatan((JenisMuatan) cbJenis.getSelectedItem());
        t.setPemasokPelanggan(txtPemasok.getText());
        t.setPerkiraanKedatangan(UiUtil.ambilWaktu(spnKedatangan));
        t.setEstimasiDurasiMenit(UiUtil.ambilAngka(spnDurasi));
        return t;
    }

    public void isi(Truck t) {
        txtPlat.setText(t.getNomorPolisi());
        txtSopir.setText(t.getNamaSopir());
        cbJenis.setSelectedItem(t.getJenisMuatan());
        txtPemasok.setText(t.getPemasokPelanggan());
        UiUtil.setWaktu(spnKedatangan, t.getPerkiraanKedatangan());
        spnDurasi.setValue(t.getEstimasiDurasiMenit());
        hapusTanda();
    }

    public void bersihkan() {
        txtPlat.setText("");
        txtSopir.setText("");
        cbJenis.setSelectedIndex(0);
        txtPemasok.setText("");
        UiUtil.setWaktu(spnKedatangan, LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES));
        spnDurasi.setValue(30);
        hapusTanda();
    }

    @Override
    public void setEnabled(boolean aktif) {
        super.setEnabled(aktif);
        // Dipanggil juga oleh konstruktor induk sebelum komponen dibuat.
        if (txtPlat == null) {
            return;
        }
        txtPlat.setEnabled(aktif);
        txtSopir.setEnabled(aktif);
        cbJenis.setEnabled(aktif);
        txtPemasok.setEnabled(aktif);
        spnKedatangan.setEnabled(aktif);
        spnDurasi.setEnabled(aktif);
    }

    public void fokusAwal() {
        txtPlat.requestFocusInWindow();
    }

    public void hapusTanda() {
        UiUtil.tandaiSalah(txtPlat, false);
        UiUtil.tandaiSalah(txtSopir, false);
        UiUtil.tandaiSalah(cbJenis, false);
        UiUtil.tandaiSalah(txtPemasok, false);
        UiUtil.tandaiSalah(spnKedatangan, false);
        UiUtil.tandaiSalah(spnDurasi, false);
    }

    /** Memberi bingkai merah pada field yang disebut dalam pesan kesalahan validasi. */
    public void tandaiDariPesan(String pesan) {
        hapusTanda();
        if (pesan == null) {
            return;
        }
        UiUtil.tandaiSalah(txtPlat, pesan.contains("nomor polisi") || pesan.contains("Nomor polisi"));
        UiUtil.tandaiSalah(txtSopir, pesan.contains("Nama sopir"));
        UiUtil.tandaiSalah(cbJenis, pesan.contains("Jenis muatan wajib"));
        UiUtil.tandaiSalah(txtPemasok, pesan.contains("Pemasok/pelanggan"));
        UiUtil.tandaiSalah(spnKedatangan, pesan.contains("kedatangan"));
        UiUtil.tandaiSalah(spnDurasi, pesan.contains("durasi"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblPlat;
    private javax.swing.JTextField txtPlat;
    private javax.swing.JLabel lblSopir;
    private javax.swing.JTextField txtSopir;
    private javax.swing.JLabel lblJenis;
    private javax.swing.JComboBox<JenisMuatan> cbJenis;
    private javax.swing.JLabel lblPemasok;
    private javax.swing.JTextField txtPemasok;
    private javax.swing.JLabel lblKedatangan;
    private javax.swing.JSpinner spnKedatangan;
    private javax.swing.JLabel lblDurasi;
    private javax.swing.JSpinner spnDurasi;
    // End of variables declaration//GEN-END:variables
}
