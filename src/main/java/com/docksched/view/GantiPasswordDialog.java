package com.docksched.view;

import java.awt.Frame;

/** Dialog ganti password sendiri. Dibuat dengan NetBeans GUI Builder (JDialog Form). */
public class GantiPasswordDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;
    private boolean disetujui;

    public GantiPasswordDialog(Frame induk) {
        super(induk, true);
        initComponents();
        getRootPane().setDefaultButton(btnSimpan);
        pack();
        setLocationRelativeTo(induk);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        pnlForm = new javax.swing.JPanel();
        lblLama = new javax.swing.JLabel();
        txtLama = new javax.swing.JPasswordField();
        lblBaru = new javax.swing.JLabel();
        txtBaru = new javax.swing.JPasswordField();
        lblUlang = new javax.swing.JLabel();
        txtUlang = new javax.swing.JPasswordField();
        pnlTombol = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setTitle("Ganti Password");
        setModal(true);
        getContentPane().setLayout(new java.awt.BorderLayout());
        pnlForm.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 4, 12));
        pnlForm.setLayout(new java.awt.GridBagLayout());
        lblLama.setText("Password lama");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblLama, gridBagConstraints);
        txtLama.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtLama, gridBagConstraints);
        lblBaru.setText("Password baru (min. 8 karakter)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblBaru, gridBagConstraints);
        txtBaru.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtBaru, gridBagConstraints);
        lblUlang.setText("Ulangi password baru");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlForm.add(lblUlang, gridBagConstraints);
        txtUlang.setColumns(18);
        javax.swing.JCheckBox chkLihat = new javax.swing.JCheckBox("Tampilkan password");
        chkLihat.addActionListener(e -> { char ch = chkLihat.isSelected() ? (char) 0 : '\u2022'; txtLama.setEchoChar(ch); txtBaru.setEchoChar(ch); txtUlang.setEchoChar(ch); });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(txtUlang, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlForm.add(chkLihat, gridBagConstraints);
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

    public char[] getPasswordLama() {
        return txtLama.getPassword();
    }

    public String getPasswordBaru() {
        return new String(txtBaru.getPassword());
    }

    public String getPasswordUlang() {
        return new String(txtUlang.getPassword());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlForm;
    private javax.swing.JLabel lblLama;
    private javax.swing.JPasswordField txtLama;
    private javax.swing.JLabel lblBaru;
    private javax.swing.JPasswordField txtBaru;
    private javax.swing.JLabel lblUlang;
    private javax.swing.JPasswordField txtUlang;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnBatal;
    // End of variables declaration//GEN-END:variables
}
