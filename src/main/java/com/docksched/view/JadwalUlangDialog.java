package com.docksched.view;

import com.docksched.model.Dock;
import com.docksched.model.Jadwal;
import java.awt.Frame;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/** Dialog jadwal ulang manual oleh Admin (UC-07). Dibuat dengan NetBeans GUI Builder (JDialog Form). */
public class JadwalUlangDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private boolean disetujui;

    public JadwalUlangDialog(Frame induk, Jadwal jadwal, List<Dock> docks) {
        super(induk, true);
        initComponents();
        lblInfoTruck.setText(jadwal.getNomorPolisi() + " - " + jadwal.getJenisMuatan().getLabel());
        cbDock.setModel(new DefaultComboBoxModel<>(docks.toArray(new Dock[0])));
        for (Dock d : docks) {
            if (d.getIdDock() == jadwal.getIdDock()) {
                cbDock.setSelectedItem(d);
            }
        }
        UiUtil.aturSpinnerWaktu(spnMasuk, jadwal.getEstimasiWaktuMasuk().isAfter(LocalDateTime.now())
                ? jadwal.getEstimasiWaktuMasuk() : LocalDateTime.now().plusMinutes(5).truncatedTo(ChronoUnit.MINUTES));
        getRootPane().setDefaultButton(btnSimpan);
        pack();
        setLocationRelativeTo(induk);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        pnlForm = new javax.swing.JPanel();
        lblTruck = new javax.swing.JLabel();
        lblInfoTruck = new javax.swing.JLabel();
        lblDockBaru = new javax.swing.JLabel();
        cbDock = new javax.swing.JComboBox<>();
        lblMasukBaru = new javax.swing.JLabel();
        spnMasuk = new javax.swing.JSpinner();
        lblAlasanTeks = new javax.swing.JLabel();
        txtAlasan = new javax.swing.JTextField();
        pnlTombol = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setTitle("Jadwal Ulang Manual");
        setModal(true);
        getContentPane().setLayout(new java.awt.BorderLayout());
        pnlForm.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 4, 12));
        pnlForm.setLayout(new java.awt.GridBagLayout());
        lblTruck.setText("Truck");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblTruck, gridBagConstraints);
        lblInfoTruck.setText("Truck");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(lblInfoTruck, gridBagConstraints);
        lblDockBaru.setText("Dock baru");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblDockBaru, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(cbDock, gridBagConstraints);
        lblMasukBaru.setText("Waktu masuk baru");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblMasukBaru, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(spnMasuk, gridBagConstraints);
        lblAlasanTeks.setText("Alasan (opsional)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblAlasanTeks, gridBagConstraints);
        txtAlasan.setColumns(24);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtAlasan, gridBagConstraints);
        getContentPane().add(pnlForm, java.awt.BorderLayout.CENTER);
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
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
        getContentPane().add(pnlTombol, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        disetujui = true;
        dispose();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    public boolean isDisetujui() {
        return disetujui;
    }

    public Dock getDockDipilih() {
        return (Dock) cbDock.getSelectedItem();
    }

    public LocalDateTime getMasuk() {
        return UiUtil.ambilWaktu(spnMasuk);
    }

    public String getAlasan() {
        return txtAlasan.getText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlForm;
    private javax.swing.JLabel lblTruck;
    private javax.swing.JLabel lblInfoTruck;
    private javax.swing.JLabel lblDockBaru;
    private javax.swing.JComboBox<Dock> cbDock;
    private javax.swing.JLabel lblMasukBaru;
    private javax.swing.JSpinner spnMasuk;
    private javax.swing.JLabel lblAlasanTeks;
    private javax.swing.JTextField txtAlasan;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnBatal;
    // End of variables declaration//GEN-END:variables
}
