package com.docksched.view;

import com.docksched.config.DatabaseInit;
import com.docksched.config.Koneksi;
import com.docksched.util.BackgroundTask;
import java.awt.Color;
import java.awt.Frame;
import java.io.IOException;
import java.sql.Connection;

/** Dialog pengaturan koneksi MySQL (host, port, user, password). Dibuat dengan NetBeans GUI Builder (JDialog Form). */
public class KoneksiDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    public KoneksiDialog(Frame induk) {
        super(induk, true);
        initComponents();
        txtHost.setText(Koneksi.getHost());
        UiUtil.aturSpinnerAngka(spnPort, Koneksi.getPort(), 1, 65535, 1);
        txtUser.setText(Koneksi.getUser());
        txtPassword.setText(Koneksi.getPassword());
        lblNamaDb.setText(Koneksi.NAMA_DATABASE + " (dibuat otomatis)");
        getRootPane().setDefaultButton(btnSimpan);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(induk);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        pnlForm = new javax.swing.JPanel();
        lblHost = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        lblPort = new javax.swing.JLabel();
        spnPort = new javax.swing.JSpinner();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPass = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblDb = new javax.swing.JLabel();
        lblNamaDb = new javax.swing.JLabel();
        pnlBawah = new javax.swing.JPanel();
        lblHasil = new javax.swing.JLabel();
        pnlTombol = new javax.swing.JPanel();
        btnUji = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setTitle("Pengaturan Koneksi MySQL");
        setModal(true);
        getContentPane().setLayout(new java.awt.BorderLayout());
        pnlForm.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 4, 12));
        pnlForm.setLayout(new java.awt.GridBagLayout());
        lblHost.setText("Host server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblHost, gridBagConstraints);
        txtHost.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtHost, gridBagConstraints);
        lblPort.setText("Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblPort, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(spnPort, gridBagConstraints);
        lblUser.setText("Username MySQL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblUser, gridBagConstraints);
        txtUser.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtUser, gridBagConstraints);
        lblPass.setText("Password MySQL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblPass, gridBagConstraints);
        txtPassword.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtPassword, gridBagConstraints);
        lblDb.setText("Nama database");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblDb, gridBagConstraints);
        lblNamaDb.setText("dock_sched (dibuat otomatis)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(lblNamaDb, gridBagConstraints);
        getContentPane().add(pnlForm, java.awt.BorderLayout.CENTER);
        pnlBawah.setLayout(new java.awt.BorderLayout());
        lblHasil.setText(" ");
        lblHasil.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14));
        pnlBawah.add(lblHasil, java.awt.BorderLayout.NORTH);
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnUji.setText("Uji Koneksi");
        btnUji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUjiActionPerformed(evt);
            }
        });
        pnlTombol.add(btnUji);
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnSimpan);
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        pnlTombol.add(btnBatal);
        pnlBawah.add(pnlTombol, java.awt.BorderLayout.SOUTH);
        getContentPane().add(pnlBawah, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUjiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUjiActionPerformed
        uji();
    }//GEN-LAST:event_btnUjiActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpan();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void terapkan() {
        Koneksi.atur(txtHost.getText().trim(), UiUtil.ambilAngka(spnPort), txtUser.getText().trim(),
                new String(txtPassword.getPassword()));
        DatabaseInit.reset();
    }

    private void uji() {
        terapkan();
        btnUji.setEnabled(false);
        lblHasil.setForeground(Color.DARK_GRAY);
        lblHasil.setText("Menguji koneksi...");
        BackgroundTask.<Boolean>jalankan(() -> {
            try (Connection c = Koneksi.bukaServer()) {
                return c.isValid(3);
            }
        }, ok -> {
            btnUji.setEnabled(true);
            lblHasil.setForeground(ok ? UiUtil.HIJAU : UiUtil.MERAH);
            lblHasil.setText(ok ? "Koneksi berhasil." : "Koneksi gagal.");
        }, err -> {
            btnUji.setEnabled(true);
            lblHasil.setForeground(UiUtil.MERAH);
            lblHasil.setText("<html>Gagal: " + UiUtil.ringkas(err.getMessage()).replace("<", "&lt;") + "</html>");
        });
    }

    private void simpan() {
        terapkan();
        try {
            Koneksi.simpan();
            dispose();
        } catch (IOException e) {
            UiUtil.error(this, "Pengaturan tidak dapat disimpan: " + e.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlForm;
    private javax.swing.JLabel lblHost;
    private javax.swing.JTextField txtHost;
    private javax.swing.JLabel lblPort;
    private javax.swing.JSpinner spnPort;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTextField txtUser;
    private javax.swing.JLabel lblPass;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JLabel lblDb;
    private javax.swing.JLabel lblNamaDb;
    private javax.swing.JPanel pnlBawah;
    private javax.swing.JLabel lblHasil;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnUji;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnBatal;
    // End of variables declaration//GEN-END:variables
}
