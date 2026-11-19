package com.docksched.view;

import com.docksched.config.DatabaseInit;
import com.docksched.controller.AuthController;
import com.docksched.controller.Sesi;
import com.docksched.model.Pengguna;
import com.docksched.util.BackgroundTask;
import java.awt.Color;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JOptionPane;

/** Layar login (UC-01; DS-F-01.1, 01.2, 01.4). Dibuat dengan NetBeans GUI Builder (JFrame Form). */
public class LoginFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private final AuthController auth = new AuthController();

    /** @param pesanAwal pesan di bawah form (mis. alasan logout otomatis), boleh null */
    public LoginFrame(String pesanAwal) {
        initComponents();
        if (pesanAwal != null) {
            lblStatus.setText("<html><div style='text-align:center'>" + pesanAwal + "</div></html>");
        }
        getRootPane().setDefaultButton(btnMasuk);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        pnlKepala = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblSub = new javax.swing.JLabel();
        pnlForm = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnMasuk = new javax.swing.JButton();
        pnlKaki = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        pnlKakiTombol = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DOCK-SCHED - Login");
        setMinimumSize(new java.awt.Dimension(440, 330));
        getContentPane().setLayout(new java.awt.BorderLayout());
        pnlKepala.setBackground(new java.awt.Color(31, 78, 121));
        pnlKepala.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 20, 18, 20));
        pnlKepala.setLayout(new java.awt.BorderLayout());
        lblJudul.setText("DOCK-SCHED");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 28));
        lblJudul.setForeground(new java.awt.Color(255, 255, 255));
        lblJudul.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlKepala.add(lblJudul, java.awt.BorderLayout.CENTER);
        lblSub.setText("<html><div style='text-align:center;width:300px'>Sistem Penjadwalan Slot Loading-Unloading Truck Delivery di Gudang</div></html>");
        lblSub.setForeground(new java.awt.Color(220, 230, 242));
        lblSub.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlKepala.add(lblSub, java.awt.BorderLayout.SOUTH);
        getContentPane().add(pnlKepala, java.awt.BorderLayout.NORTH);
        pnlForm.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 8, 30));
        pnlForm.setLayout(new java.awt.GridBagLayout());
        lblUsername.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 5, 4);
        pnlForm.add(lblUsername, gridBagConstraints);
        txtUsername.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 5, 4);
        pnlForm.add(txtUsername, gridBagConstraints);
        lblPassword.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 5, 4);
        pnlForm.add(lblPassword, gridBagConstraints);
        txtPassword.setColumns(20);
        javax.swing.JCheckBox chkLihatPassword = new javax.swing.JCheckBox("Tampilkan password");
        chkLihatPassword.addActionListener(e -> txtPassword.setEchoChar(chkLihatPassword.isSelected() ? (char) 0 : '\u2022'));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 5, 4);
        pnlForm.add(txtPassword, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        pnlForm.add(chkLihatPassword, gridBagConstraints);
        btnMasuk.setText("Masuk");
        btnMasuk.setPreferredSize(new java.awt.Dimension(100, 34));
        btnMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMasukActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(14, 4, 4, 4);
        pnlForm.add(btnMasuk, gridBagConstraints);
        getContentPane().add(pnlForm, java.awt.BorderLayout.CENTER);
        pnlKaki.setLayout(new java.awt.BorderLayout());
        lblStatus.setText(" ");
        lblStatus.setForeground(new java.awt.Color(192, 0, 0));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        pnlKaki.add(lblStatus, java.awt.BorderLayout.NORTH);
        pnlKakiTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        pnlKaki.add(pnlKakiTombol, java.awt.BorderLayout.SOUTH);
        getContentPane().add(pnlKaki, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMasukActionPerformed
        masuk();
    }//GEN-LAST:event_btnMasukActionPerformed

    private void masuk() {
        String username = txtUsername.getText();
        char[] password = txtPassword.getPassword();
        btnMasuk.setEnabled(false);
        lblStatus.setForeground(Color.DARK_GRAY);
        lblStatus.setText("Menghubungkan ke database...");
        BackgroundTask.<Pengguna>jalankan(() -> {
            DatabaseInit.siapkan();
            if (DatabaseInit.perluProgrammerAwal()) return new Pengguna();
            if (username.trim().isEmpty() || password.length == 0) return null;
            return auth.login(username, password);
        }, pengguna -> {
            Arrays.fill(password, '\0');
            btnMasuk.setEnabled(true);
            if (pengguna != null && pengguna.getIdPengguna() == 0) {
                buatAkunProgrammerPertama();
                return;
            }
            if (pengguna == null) {
                // Pesan umum: tidak menyebut apakah username atau password yang keliru (DS-F-01.4).
                lblStatus.setForeground(UiUtil.MERAH);
                lblStatus.setText("Username atau password salah.");
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
                return;
            }
            Sesi.mulai(pengguna);
            new MainFrame(pengguna).setVisible(true);
            dispose();
        }, err -> {
            Arrays.fill(password, '\0');
            btnMasuk.setEnabled(true);
            lblStatus.setText(" ");
            Throwable dasar = err;
            while (dasar.getCause() != null && !(dasar instanceof SQLException)) {
                dasar = dasar.getCause();
            }
            if (dasar instanceof SQLException) {
                JOptionPane.showMessageDialog(this,
                        "Tidak dapat membuka database aplikasi.\n\n"
                        + "Pastikan DOCK-SCHED tidak sedang terbuka di jendela lain, lalu coba lagi.\n\n"
                        + "Detail: " + UiUtil.ringkas(dasar.getMessage()),
                        "Database Gagal Dibuka", JOptionPane.ERROR_MESSAGE);
            } else {
                UiUtil.tampilkanError(this, err);
            }
        });
    }

    private void buatAkunProgrammerPertama() {
        javax.swing.JTextField username = new javax.swing.JTextField(20);
        javax.swing.JTextField nama = new javax.swing.JTextField(20);
        javax.swing.JPasswordField pass = new javax.swing.JPasswordField(20);
        javax.swing.JPasswordField ulang = new javax.swing.JPasswordField(20);
        javax.swing.JCheckBox lihat = new javax.swing.JCheckBox("Tampilkan password");
        lihat.addActionListener(e -> { char ch = lihat.isSelected() ? (char) 0 : '\u2022'; pass.setEchoChar(ch); ulang.setEchoChar(ch); });
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 4, 4));
        panel.add(new javax.swing.JLabel("Pembuatan Programmer pertama (akses penuh)"));
        panel.add(new javax.swing.JLabel("Username")); panel.add(username);
        panel.add(new javax.swing.JLabel("Nama lengkap")); panel.add(nama);
        panel.add(new javax.swing.JLabel("Password (minimal 8 karakter)")); panel.add(pass);
        panel.add(new javax.swing.JLabel("Ulangi password")); panel.add(ulang); panel.add(lihat);
        int pilih = JOptionPane.showConfirmDialog(this, panel, "Setup Programmer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (pilih != JOptionPane.OK_OPTION) { lblStatus.setText("Buat akun Programmer untuk mulai menggunakan aplikasi."); return; }
        char[] a = pass.getPassword(), b = ulang.getPassword();
        try {
            if (!java.util.Arrays.equals(a, b)) throw new IllegalArgumentException("Konfirmasi password tidak sama.");
            com.docksched.config.DatabaseInit.buatProgrammerPertama(username.getText(), nama.getText(), a);
            JOptionPane.showMessageDialog(this, "Akun Programmer berhasil dibuat. Silakan login.", "Setup berhasil", JOptionPane.INFORMATION_MESSAGE);
            txtUsername.setText(username.getText()); txtPassword.setText("");
            lblStatus.setText("Silakan login menggunakan akun Programmer yang baru dibuat.");
        } catch (Exception ex) { UiUtil.tampilkanError(this, ex); }
        finally { java.util.Arrays.fill(a, '\0'); java.util.Arrays.fill(b, '\0'); }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlKepala;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblSub;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JButton btnMasuk;
    private javax.swing.JPanel pnlKaki;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlKakiTombol;
    // End of variables declaration//GEN-END:variables
}
