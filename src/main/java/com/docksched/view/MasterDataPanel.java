package com.docksched.view;

import com.docksched.controller.ArsipController;
import com.docksched.controller.BackupService;
import com.docksched.controller.DockController;
import com.docksched.controller.PengaturanController;
import com.docksched.controller.PenggunaController;
import com.docksched.controller.Sesi;
import com.docksched.model.Dock;
import com.docksched.model.JenisMuatan;
import com.docksched.model.PengaturanSpk;
import com.docksched.model.Pengguna;
import com.docksched.model.Peran;
import com.docksched.model.StatusDock;
import com.docksched.util.BackgroundTask;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/** Master data dock, akun pengguna, bobot SAW, serta arsip dan cadangan data (khusus Admin Gudang). Dibuat dengan NetBeans GUI Builder. */
public class MasterDataPanel extends javax.swing.JPanel implements Halaman {

    private static final long serialVersionUID = 1L;

    private final DockController dockController = new DockController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final PengaturanController pengaturanController = new PengaturanController();
    private final ArsipController arsipController = new ArsipController();
    private final BackupService backupService = new BackupService();

    private final DefaultTableModel modelDock = UiUtil.modelTabel("ID", "Kode Dock", "Kapasitas", "Jenis Muatan",
            "Serbaguna", "Status");
    private final List<Dock> daftarDock = new ArrayList<>();
    private Dock dockTerpilih;

    private final DefaultTableModel modelUser = UiUtil.modelTabel("ID", "Username", "Nama Lengkap", "Peran", "Aktif");
    private final List<Pengguna> daftarUser = new ArrayList<>();
    private Pengguna userTerpilih;

    public MasterDataPanel() {
        initComponents();
        // Dock
        TabelData.pasang(tabelDock, modelDock);
        tabelDock.getColumnModel().getColumn(0).setMaxWidth(50);
        UiUtil.aturSpinnerAngka(spnKapasitas, 10, 1, 1000, 1);
        cbJenisDock.setModel(new DefaultComboBoxModel<>(JenisMuatan.values()));
        cbStatusDock.setModel(new DefaultComboBoxModel<>(new StatusDock[] {StatusDock.TERSEDIA, StatusDock.RUSAK}));
        tabelDock.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilihDock();
            }
        });
        // Pengguna
        TabelData.pasang(tabelUser, modelUser);
        tabelUser.getColumnModel().getColumn(0).setMaxWidth(50);
        cbPeran.setModel(new DefaultComboBoxModel<>(Peran.values()));
        tabelUser.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilihUser();
            }
        });
        // Bobot
        UiUtil.aturSpinnerAngka(spnBobotMuatan, 50, 0, 100, 1);
        UiUtil.aturSpinnerAngka(spnBobotDock, 30, 0, 100, 1);
        UiUtil.aturSpinnerAngka(spnBobotDurasi, 20, 0, 100, 1);
        UiUtil.aturSpinnerAngka(spnNilaiBahan, 5, 1, 100, 1);
        UiUtil.aturSpinnerAngka(spnNilaiJadi, 3, 1, 100, 1);
        // Arsip
        UiUtil.aturSpinnerTanggal(spnTanggal, LocalDate.now().minusDays(30));
        lblFolder.setText("Folder cadangan: " + backupService.folder());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        lblJudul = new javax.swing.JLabel();
        tab = new javax.swing.JTabbedPane();
        pnlTabDock = new javax.swing.JPanel();
        scrollDock = new javax.swing.JScrollPane();
        tabelDock = new javax.swing.JTable();
        pnlBawahDock = new javax.swing.JPanel();
        pnlFormDock = new javax.swing.JPanel();
        lblKodeDock = new javax.swing.JLabel();
        txtKodeDock = new javax.swing.JTextField();
        lblKapasitas = new javax.swing.JLabel();
        spnKapasitas = new javax.swing.JSpinner();
        lblJenisDock = new javax.swing.JLabel();
        cbJenisDock = new javax.swing.JComboBox<>();
        lblKosong1 = new javax.swing.JLabel();
        chkSerbaguna = new javax.swing.JCheckBox();
        lblStatusDock = new javax.swing.JLabel();
        cbStatusDock = new javax.swing.JComboBox<>();
        pnlTombolDock = new javax.swing.JPanel();
        btnBaruDock = new javax.swing.JButton();
        btnTambahDock = new javax.swing.JButton();
        btnSimpanDock = new javax.swing.JButton();
        pnlTabUser = new javax.swing.JPanel();
        scrollUser = new javax.swing.JScrollPane();
        tabelUser = new javax.swing.JTable();
        pnlBawahUser = new javax.swing.JPanel();
        pnlFormUser = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblPeran = new javax.swing.JLabel();
        cbPeran = new javax.swing.JComboBox<>();
        lblKosong2 = new javax.swing.JLabel();
        chkAktif = new javax.swing.JCheckBox();
        lblPassUser = new javax.swing.JLabel();
        txtPassUser = new javax.swing.JPasswordField();
        pnlTombolUser = new javax.swing.JPanel();
        btnBaruUser = new javax.swing.JButton();
        btnTambahUser = new javax.swing.JButton();
        btnSimpanUser = new javax.swing.JButton();
        pnlTabBobot = new javax.swing.JPanel();
        lblPenjelasan = new javax.swing.JLabel();
        pnlTengah = new javax.swing.JPanel();
        pnlFormBobot = new javax.swing.JPanel();
        lblW1 = new javax.swing.JLabel();
        spnBobotMuatan = new javax.swing.JSpinner();
        lblW2 = new javax.swing.JLabel();
        spnBobotDock = new javax.swing.JSpinner();
        lblW3 = new javax.swing.JLabel();
        spnBobotDurasi = new javax.swing.JSpinner();
        lblNilaiBahan = new javax.swing.JLabel();
        spnNilaiBahan = new javax.swing.JSpinner();
        lblNilaiJadi = new javax.swing.JLabel();
        spnNilaiJadi = new javax.swing.JSpinner();
        pnlTombolBobot = new javax.swing.JPanel();
        btnSimpanBobot = new javax.swing.JButton();
        pnlTabArsip = new javax.swing.JPanel();
        pnlAtasArsip = new javax.swing.JPanel();
        pnlArsip = new javax.swing.JPanel();
        lblArsip = new javax.swing.JLabel();
        spnTanggal = new javax.swing.JSpinner();
        btnArsip = new javax.swing.JButton();
        pnlBackup = new javax.swing.JPanel();
        lblCadangan = new javax.swing.JLabel();
        pnlBarisBackup = new javax.swing.JPanel();
        btnBackup = new javax.swing.JButton();
        lblFolder = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new java.awt.BorderLayout());
        lblJudul.setText("Master Data & Pengaturan");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblJudul.setForeground(new java.awt.Color(31, 78, 121));
        add(lblJudul, java.awt.BorderLayout.NORTH);
        pnlTabDock.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pnlTabDock.setLayout(new java.awt.BorderLayout(0, 8));
        scrollDock.setViewportView(tabelDock);
        pnlTabDock.add(scrollDock, java.awt.BorderLayout.CENTER);
        pnlBawahDock.setLayout(new java.awt.BorderLayout());
        pnlFormDock.setBorder(javax.swing.BorderFactory.createTitledBorder("Data dock"));
        pnlFormDock.setLayout(new java.awt.GridBagLayout());
        lblKodeDock.setText("Kode dock");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormDock.add(lblKodeDock, gridBagConstraints);
        txtKodeDock.setColumns(14);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormDock.add(txtKodeDock, gridBagConstraints);
        lblKapasitas.setText("Kapasitas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormDock.add(lblKapasitas, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormDock.add(spnKapasitas, gridBagConstraints);
        lblJenisDock.setText("Jenis muatan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormDock.add(lblJenisDock, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormDock.add(cbJenisDock, gridBagConstraints);
        lblKosong1.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormDock.add(lblKosong1, gridBagConstraints);
        chkSerbaguna.setText("Dock serbaguna (menerima kedua jenis muatan)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormDock.add(chkSerbaguna, gridBagConstraints);
        lblStatusDock.setText("Status (Terisi diatur otomatis)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormDock.add(lblStatusDock, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormDock.add(cbStatusDock, gridBagConstraints);
        pnlBawahDock.add(pnlFormDock, java.awt.BorderLayout.CENTER);
        pnlTombolDock.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnBaruDock.setText("Kosongkan Form");
        btnBaruDock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruDockActionPerformed(evt);
            }
        });
        pnlTombolDock.add(btnBaruDock);
        btnTambahDock.setText("Tambah Dock");
        btnTambahDock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahDockActionPerformed(evt);
            }
        });
        pnlTombolDock.add(btnTambahDock);
        btnSimpanDock.setText("Simpan Perubahan");
        btnSimpanDock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanDockActionPerformed(evt);
            }
        });
        pnlTombolDock.add(btnSimpanDock);
        pnlBawahDock.add(pnlTombolDock, java.awt.BorderLayout.SOUTH);
        pnlTabDock.add(pnlBawahDock, java.awt.BorderLayout.SOUTH);
        tab.addTab("Dock", pnlTabDock);
        pnlTabUser.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pnlTabUser.setLayout(new java.awt.BorderLayout(0, 8));
        scrollUser.setViewportView(tabelUser);
        pnlTabUser.add(scrollUser, java.awt.BorderLayout.CENTER);
        pnlBawahUser.setLayout(new java.awt.BorderLayout());
        pnlFormUser.setBorder(javax.swing.BorderFactory.createTitledBorder("Data akun (password hanya diisi untuk akun baru atau reset)"));
        pnlFormUser.setLayout(new java.awt.GridBagLayout());
        lblUsername.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormUser.add(lblUsername, gridBagConstraints);
        txtUsername.setColumns(16);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormUser.add(txtUsername, gridBagConstraints);
        lblNama.setText("Nama lengkap");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormUser.add(lblNama, gridBagConstraints);
        txtNama.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormUser.add(txtNama, gridBagConstraints);
        lblPeran.setText("Peran");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormUser.add(lblPeran, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormUser.add(cbPeran, gridBagConstraints);
        lblKosong2.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormUser.add(lblKosong2, gridBagConstraints);
        chkAktif.setText("Akun aktif");
        chkAktif.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormUser.add(chkAktif, gridBagConstraints);
        lblPassUser.setText("Password (min. 8 karakter)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormUser.add(lblPassUser, gridBagConstraints);
        txtPassUser.setColumns(16);
        javax.swing.JCheckBox chkLihatPassUser = new javax.swing.JCheckBox("Tampilkan password");
        chkLihatPassUser.addActionListener(e -> txtPassUser.setEchoChar(chkLihatPassUser.isSelected() ? (char) 0 : '\u2022'));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormUser.add(txtPassUser, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        pnlFormUser.add(chkLihatPassUser, gridBagConstraints);
        pnlBawahUser.add(pnlFormUser, java.awt.BorderLayout.CENTER);
        pnlTombolUser.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnBaruUser.setText("Kosongkan Form");
        btnBaruUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruUserActionPerformed(evt);
            }
        });
        pnlTombolUser.add(btnBaruUser);
        btnTambahUser.setText("Tambah Akun");
        btnTambahUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahUserActionPerformed(evt);
            }
        });
        pnlTombolUser.add(btnTambahUser);
        btnSimpanUser.setText("Simpan Perubahan");
        btnSimpanUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanUserActionPerformed(evt);
            }
        });
        pnlTombolUser.add(btnSimpanUser);
        pnlBawahUser.add(pnlTombolUser, java.awt.BorderLayout.SOUTH);
        pnlTabUser.add(pnlBawahUser, java.awt.BorderLayout.SOUTH);
        tab.addTab("Akun Pengguna", pnlTabUser);
        pnlTabBobot.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pnlTabBobot.setLayout(new java.awt.BorderLayout(0, 8));
        lblPenjelasan.setText("<html>Skor prioritas = (w1&times;C1 + w2&times;C2 + w3&times;C3) / (w1+w2+w3), dengan C1 = nilai prioritas muatan, C2 = total kapasitas dock yang sesuai, C3 = estimasi durasi (lebih singkat lebih baik).<br>Nilai bawaan (50/30/20 dan 5:3) hanyalah asumsi awal; sesuaikan dengan kebijakan gudang.</html>");
        pnlTabBobot.add(lblPenjelasan, java.awt.BorderLayout.NORTH);
        pnlTengah.setLayout(new java.awt.BorderLayout());
        pnlFormBobot.setBorder(javax.swing.BorderFactory.createTitledBorder("Bobot dan nilai"));
        pnlFormBobot.setLayout(new java.awt.GridBagLayout());
        lblW1.setText("Bobot C1 - prioritas jenis muatan (w1)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormBobot.add(lblW1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormBobot.add(spnBobotMuatan, gridBagConstraints);
        lblW2.setText("Bobot C2 - kesesuaian & kapasitas dock (w2)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormBobot.add(lblW2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormBobot.add(spnBobotDock, gridBagConstraints);
        lblW3.setText("Bobot C3 - estimasi durasi (w3)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormBobot.add(lblW3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormBobot.add(spnBobotDurasi, gridBagConstraints);
        lblNilaiBahan.setText("Nilai prioritas Bahan Baku Masuk");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormBobot.add(lblNilaiBahan, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormBobot.add(spnNilaiBahan, gridBagConstraints);
        lblNilaiJadi.setText("Nilai prioritas Barang Jadi Keluar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 10);
        pnlFormBobot.add(lblNilaiJadi, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        pnlFormBobot.add(spnNilaiJadi, gridBagConstraints);
        pnlTengah.add(pnlFormBobot, java.awt.BorderLayout.NORTH);
        pnlTombolBobot.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnSimpanBobot.setText("Simpan Bobot");
        btnSimpanBobot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanBobotActionPerformed(evt);
            }
        });
        pnlTombolBobot.add(btnSimpanBobot);
        pnlTengah.add(pnlTombolBobot, java.awt.BorderLayout.CENTER);
        pnlTabBobot.add(pnlTengah, java.awt.BorderLayout.CENTER);
        tab.addTab("Bobot Kriteria SAW", pnlTabBobot);
        pnlTabArsip.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pnlTabArsip.setLayout(new java.awt.BorderLayout(0, 10));
        pnlAtasArsip.setLayout(new java.awt.BorderLayout(0, 10));
        pnlArsip.setBorder(javax.swing.BorderFactory.createTitledBorder("Arsip data lama (data tidak dihapus permanen)"));
        pnlArsip.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        lblArsip.setText("Arsipkan truck yang selesai sebelum tanggal");
        pnlArsip.add(lblArsip);
        pnlArsip.add(spnTanggal);
        btnArsip.setText("Arsipkan");
        btnArsip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArsipActionPerformed(evt);
            }
        });
        pnlArsip.add(btnArsip);
        pnlAtasArsip.add(pnlArsip, java.awt.BorderLayout.NORTH);
        pnlBackup.setBorder(javax.swing.BorderFactory.createTitledBorder("Cadangan basis data"));
        pnlBackup.setLayout(new java.awt.BorderLayout());
        lblCadangan.setText("<html>Cadangan otomatis dibuat sekali sehari saat aplikasi dibuka. Berkas .sql dapat dipulihkan lewat MySQL.</html>");
        pnlBackup.add(lblCadangan, java.awt.BorderLayout.NORTH);
        pnlBarisBackup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnBackup.setText("Buat Cadangan Sekarang");
        btnBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackupActionPerformed(evt);
            }
        });
        pnlBarisBackup.add(btnBackup);
        pnlBackup.add(pnlBarisBackup, java.awt.BorderLayout.CENTER);
        lblFolder.setText("Folder cadangan:");
        pnlBackup.add(lblFolder, java.awt.BorderLayout.SOUTH);
        pnlAtasArsip.add(pnlBackup, java.awt.BorderLayout.CENTER);
        pnlTabArsip.add(pnlAtasArsip, java.awt.BorderLayout.NORTH);
        tab.addTab("Arsip & Cadangan", pnlTabArsip);
        add(tab, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruDockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruDockActionPerformed
        kosongkanDock();
    }//GEN-LAST:event_btnBaruDockActionPerformed

    private void btnTambahDockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahDockActionPerformed
        simpanDock(true);
    }//GEN-LAST:event_btnTambahDockActionPerformed

    private void btnSimpanDockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanDockActionPerformed
        simpanDock(false);
    }//GEN-LAST:event_btnSimpanDockActionPerformed

    private void btnBaruUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruUserActionPerformed
        kosongkanUser();
    }//GEN-LAST:event_btnBaruUserActionPerformed

    private void btnTambahUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahUserActionPerformed
        simpanUser(true);
    }//GEN-LAST:event_btnTambahUserActionPerformed

    private void btnSimpanUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanUserActionPerformed
        simpanUser(false);
    }//GEN-LAST:event_btnSimpanUserActionPerformed

    private void btnSimpanBobotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanBobotActionPerformed
        simpanBobot();
    }//GEN-LAST:event_btnSimpanBobotActionPerformed

    private void btnArsipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArsipActionPerformed
        arsipkan();
    }//GEN-LAST:event_btnArsipActionPerformed

    private void btnBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackupActionPerformed
        cadangkan();
    }//GEN-LAST:event_btnBackupActionPerformed

    @Override
    public void saatDitampilkan() {
        muatDock();
        muatUser();
        muatBobot();
    }

    // ---- Dock ----------------------------------------------------------------

    private void kosongkanDock() {
        tabelDock.clearSelection();
        dockTerpilih = null;
        txtKodeDock.setText("");
        spnKapasitas.setValue(10);
        cbJenisDock.setSelectedIndex(0);
        chkSerbaguna.setSelected(false);
        cbStatusDock.setSelectedItem(StatusDock.TERSEDIA);
    }

    private void pilihDock() {
        int i = TabelData.barisModelTerpilih(tabelDock);
        if (i < 0 || i >= daftarDock.size()) {
            return;
        }
        dockTerpilih = daftarDock.get(i);
        txtKodeDock.setText(dockTerpilih.getKodeDock());
        spnKapasitas.setValue(dockTerpilih.getKapasitas());
        cbJenisDock.setSelectedItem(dockTerpilih.getJenisMuatan());
        chkSerbaguna.setSelected(dockTerpilih.isSerbaguna());
        cbStatusDock.setSelectedItem(dockTerpilih.getStatus() == StatusDock.RUSAK ? StatusDock.RUSAK : StatusDock.TERSEDIA);
    }

    private void muatDock() {
        BackgroundTask.<List<Dock>>jalankan(dockController::daftar, data -> {
            daftarDock.clear();
            daftarDock.addAll(data);
            modelDock.setRowCount(0);
            for (Dock d : data) {
                modelDock.addRow(new Object[] {d.getIdDock(), d.getKodeDock(), d.getKapasitas(),
                    d.getJenisMuatan().getLabel(), d.isSerbaguna() ? "Ya" : "Tidak", d.getStatus().getLabel()});
            }
        }, err -> UiUtil.tampilkanError(this, err));
    }

    private void simpanDock(boolean baru) {
        if (!baru && dockTerpilih == null) {
            UiUtil.peringatan(this, "Pilih dock pada tabel terlebih dahulu, atau gunakan Tambah Dock.");
            return;
        }
        Dock d = new Dock();
        d.setIdDock(baru || dockTerpilih == null ? 0 : dockTerpilih.getIdDock());
        d.setKodeDock(txtKodeDock.getText());
        d.setKapasitas(UiUtil.ambilAngka(spnKapasitas));
        d.setJenisMuatan((JenisMuatan) cbJenisDock.getSelectedItem());
        d.setSerbaguna(chkSerbaguna.isSelected());
        d.setStatus((StatusDock) cbStatusDock.getSelectedItem());
        BackgroundTask.<Integer>jalankan(() -> {
            if (baru) {
                dockController.tambah(d, Sesi.getPengguna());
                return 0;
            }
            return dockController.perbarui(d, Sesi.getPengguna());
        }, dibatalkan -> {
            UiUtil.info(this, baru ? "Dock ditambahkan." : "Dock diperbarui." + (dibatalkan > 0
                    ? "\n" + dibatalkan + " jadwal truck pada dock ini dibatalkan karena tidak lagi sesuai.\n"
                    + "Jalankan Penjadwalan Slot Dock agar truck tersebut mendapat dock baru." : ""));
            muatDock();
        }, err -> UiUtil.tampilkanError(this, err));
    }

    // ---- Pengguna -------------------------------------------------------------

    private void kosongkanUser() {
        tabelUser.clearSelection();
        userTerpilih = null;
        txtUsername.setText("");
        txtNama.setText("");
        cbPeran.setSelectedIndex(0);
        chkAktif.setSelected(true);
        txtPassUser.setText("");
    }

    private void pilihUser() {
        int i = TabelData.barisModelTerpilih(tabelUser);
        if (i < 0 || i >= daftarUser.size()) {
            return;
        }
        userTerpilih = daftarUser.get(i);
        txtUsername.setText(userTerpilih.getUsername());
        txtNama.setText(userTerpilih.getNamaLengkap());
        cbPeran.setSelectedItem(userTerpilih.getPeran());
        chkAktif.setSelected(userTerpilih.isAktif());
        txtPassUser.setText("");
    }

    private void muatUser() {
        BackgroundTask.<List<Pengguna>>jalankan(penggunaController::daftar, data -> {
            daftarUser.clear();
            daftarUser.addAll(data);
            modelUser.setRowCount(0);
            for (Pengguna u : data) {
                modelUser.addRow(new Object[] {u.getIdPengguna(), u.getUsername(), u.getNamaLengkap(),
                    u.getPeran().getLabel(), u.isAktif() ? "Ya" : "Tidak"});
            }
        }, err -> UiUtil.tampilkanError(this, err));
    }

    private void simpanUser(boolean baru) {
        if (!baru && userTerpilih == null) {
            UiUtil.peringatan(this, "Pilih akun pada tabel terlebih dahulu, atau gunakan Tambah Akun.");
            return;
        }
        Pengguna u = new Pengguna();
        u.setIdPengguna(baru || userTerpilih == null ? 0 : userTerpilih.getIdPengguna());
        u.setUsername(txtUsername.getText());
        u.setNamaLengkap(txtNama.getText());
        u.setPeran((Peran) cbPeran.getSelectedItem());
        u.setAktif(chkAktif.isSelected());
        String password = new String(txtPassUser.getPassword());
        BackgroundTask.<Boolean>jalankan(() -> {
            if (baru) {
                penggunaController.tambah(u, password, Sesi.getPengguna());
            } else {
                penggunaController.perbarui(u, password, Sesi.getPengguna());
            }
            return true;
        }, ok -> {
            UiUtil.info(this, baru ? "Akun ditambahkan." : "Akun diperbarui.");
            txtPassUser.setText("");
            muatUser();
        }, err -> UiUtil.tampilkanError(this, err));
    }

    // ---- Bobot SAW --------------------------------------------------------------

    private void simpanBobot() {
        PengaturanSpk s = new PengaturanSpk();
        s.setBobotMuatan(UiUtil.ambilAngka(spnBobotMuatan));
        s.setBobotDock(UiUtil.ambilAngka(spnBobotDock));
        s.setBobotDurasi(UiUtil.ambilAngka(spnBobotDurasi));
        s.setNilaiBahanBaku(UiUtil.ambilAngka(spnNilaiBahan));
        s.setNilaiBarangJadi(UiUtil.ambilAngka(spnNilaiJadi));
        BackgroundTask.<Boolean>jalankan(() -> {
            pengaturanController.simpan(s, Sesi.getPengguna());
            return true;
        }, ok -> UiUtil.info(this, "Bobot tersimpan. Berlaku pada penjadwalan berikutnya."),
                err -> UiUtil.tampilkanError(this, err));
    }

    private void muatBobot() {
        BackgroundTask.<PengaturanSpk>jalankan(pengaturanController::ambil, s -> {
            spnBobotMuatan.setValue(s.getBobotMuatan());
            spnBobotDock.setValue(s.getBobotDock());
            spnBobotDurasi.setValue(s.getBobotDurasi());
            spnNilaiBahan.setValue(s.getNilaiBahanBaku());
            spnNilaiJadi.setValue(s.getNilaiBarangJadi());
        }, err -> UiUtil.tampilkanError(this, err));
    }

    // ---- Arsip & cadangan ---------------------------------------------------------

    private void arsipkan() {
        LocalDate tgl = UiUtil.ambilTanggal(spnTanggal);
        if (!UiUtil.konfirmasi(this, "Arsipkan semua truck yang selesai sebelum " + tgl + "?")) {
            return;
        }
        BackgroundTask.<Integer>jalankan(() -> arsipController.arsipkanSelesaiSebelum(tgl, Sesi.getPengguna()),
                n -> UiUtil.info(this, n + " truck diarsipkan."), err -> UiUtil.tampilkanError(this, err));
    }

    private void cadangkan() {
        btnBackup.setEnabled(false);
        BackgroundTask.<Path>jalankan(backupService::cadangkan, berkas -> {
            btnBackup.setEnabled(true);
            UiUtil.info(this, "Cadangan dibuat:\n" + berkas);
        }, err -> {
            btnBackup.setEnabled(true);
            UiUtil.tampilkanError(this, err);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblJudul;
    private javax.swing.JTabbedPane tab;
    private javax.swing.JPanel pnlTabDock;
    private javax.swing.JScrollPane scrollDock;
    private javax.swing.JTable tabelDock;
    private javax.swing.JPanel pnlBawahDock;
    private javax.swing.JPanel pnlFormDock;
    private javax.swing.JLabel lblKodeDock;
    private javax.swing.JTextField txtKodeDock;
    private javax.swing.JLabel lblKapasitas;
    private javax.swing.JSpinner spnKapasitas;
    private javax.swing.JLabel lblJenisDock;
    private javax.swing.JComboBox<JenisMuatan> cbJenisDock;
    private javax.swing.JLabel lblKosong1;
    private javax.swing.JCheckBox chkSerbaguna;
    private javax.swing.JLabel lblStatusDock;
    private javax.swing.JComboBox<StatusDock> cbStatusDock;
    private javax.swing.JPanel pnlTombolDock;
    private javax.swing.JButton btnBaruDock;
    private javax.swing.JButton btnTambahDock;
    private javax.swing.JButton btnSimpanDock;
    private javax.swing.JPanel pnlTabUser;
    private javax.swing.JScrollPane scrollUser;
    private javax.swing.JTable tabelUser;
    private javax.swing.JPanel pnlBawahUser;
    private javax.swing.JPanel pnlFormUser;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JLabel lblNama;
    private javax.swing.JTextField txtNama;
    private javax.swing.JLabel lblPeran;
    private javax.swing.JComboBox<Peran> cbPeran;
    private javax.swing.JLabel lblKosong2;
    private javax.swing.JCheckBox chkAktif;
    private javax.swing.JLabel lblPassUser;
    private javax.swing.JPasswordField txtPassUser;
    private javax.swing.JPanel pnlTombolUser;
    private javax.swing.JButton btnBaruUser;
    private javax.swing.JButton btnTambahUser;
    private javax.swing.JButton btnSimpanUser;
    private javax.swing.JPanel pnlTabBobot;
    private javax.swing.JLabel lblPenjelasan;
    private javax.swing.JPanel pnlTengah;
    private javax.swing.JPanel pnlFormBobot;
    private javax.swing.JLabel lblW1;
    private javax.swing.JSpinner spnBobotMuatan;
    private javax.swing.JLabel lblW2;
    private javax.swing.JSpinner spnBobotDock;
    private javax.swing.JLabel lblW3;
    private javax.swing.JSpinner spnBobotDurasi;
    private javax.swing.JLabel lblNilaiBahan;
    private javax.swing.JSpinner spnNilaiBahan;
    private javax.swing.JLabel lblNilaiJadi;
    private javax.swing.JSpinner spnNilaiJadi;
    private javax.swing.JPanel pnlTombolBobot;
    private javax.swing.JButton btnSimpanBobot;
    private javax.swing.JPanel pnlTabArsip;
    private javax.swing.JPanel pnlAtasArsip;
    private javax.swing.JPanel pnlArsip;
    private javax.swing.JLabel lblArsip;
    private javax.swing.JSpinner spnTanggal;
    private javax.swing.JButton btnArsip;
    private javax.swing.JPanel pnlBackup;
    private javax.swing.JLabel lblCadangan;
    private javax.swing.JPanel pnlBarisBackup;
    private javax.swing.JButton btnBackup;
    private javax.swing.JLabel lblFolder;
    // End of variables declaration//GEN-END:variables
}
