package com.docksched.view;

import com.docksched.controller.AuthController;
import com.docksched.controller.BackupService;
import com.docksched.controller.Sesi;
import com.docksched.model.Pengguna;
import com.docksched.model.Peran;
import com.docksched.util.BackgroundTask;
import com.docksched.util.Konstanta;
import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

/**
 * Jendela utama: menu di kiri sesuai peran (DS-F-01.3, DS-NF-07), isi di kanan, dan logout otomatis setelah
 * tidak aktif 30 menit (DS-F-01.5). Dibuat dengan NetBeans GUI Builder (JFrame Form).
 */
public class MainFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private record Menu(String kunci, String label, Supplier<JPanel> pembuat) {
    }

    private final Pengguna pengguna;
    private final AuthController auth = new AuthController();
    private final CardLayout kartu;
    private final Map<String, JPanel> halaman = new LinkedHashMap<>();
    private final Map<String, Menu> daftarMenu = new LinkedHashMap<>();
    private final Map<String, JToggleButton> tombolMenu = new LinkedHashMap<>();
    private volatile long aktivitasTerakhir = System.currentTimeMillis();
    private final AWTEventListener pemantauAktivitas = e -> aktivitasTerakhir = System.currentTimeMillis();
    private final Timer timerBerkala;
    private final Timer timerJam;
    private final Timer timerBackup;
    private boolean sudahKeluar;
    private LocalDate tanggalPeringatanBackup;

    public MainFrame(Pengguna pengguna) {
        this.pengguna = pengguna;
        initComponents();
        kartu = (CardLayout) pnlIsi.getLayout();
        setTitle(Konstanta.NAMA_APLIKASI + " - " + Konstanta.SUB_JUDUL);
        lblUser.setText(pengguna.getNamaLengkap() + "  |  " + (pengguna.isProgrammer() ? "Programmer" : pengguna.getPeran().getLabel()));
        lblLogout.setText("Logout otomatis bila tidak aktif " + Konstanta.IDLE_MENIT + " menit.");
        Peran peran = pengguna.getPeran();

        List<Menu> menu = new ArrayList<>();
        if (pengguna.isProgrammer() || peran.bisaKelolaTruck()) {
            menu.add(new Menu("input", "Input Data Truck & Permintaan Slot", InputTruckPanel::new));
            menu.add(new Menu("cari", "Cari & Sunting Data Truck", CariTruckPanel::new));
        }
        if (pengguna.isProgrammer() || peran.bisaPenjadwalan()) {
            menu.add(new Menu("jadwal", "Penjadwalan Slot Dock (SPK)", PenjadwalanPanel::new));
        }
        if (pengguna.isProgrammer() || peran.bisaLihatStatus()) {
            menu.add(new Menu("status", "Update Status Proses", UpdateStatusPanel::new));
        }
        if (pengguna.isProgrammer() || peran.bisaDashboardLaporan()) {
            menu.add(new Menu("dashboard", "Dashboard & Laporan", DashboardLaporanPanel::new));
        }
        if (pengguna.isProgrammer() || peran.bisaMasterData()) {
            menu.add(new Menu("master", "Master Data & Pengaturan", MasterDataPanel::new));
        }
        isiMenuSamping(menu);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (UiUtil.konfirmasi(MainFrame.this, "Keluar dari aplikasi DOCK-SCHED?")) {
                    keluar(false, false);
                }
            }
        });

        Toolkit.getDefaultToolkit().addAWTEventListener(pemantauAktivitas, AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        timerBerkala = new Timer(5000, e -> periksaIdle());
        timerBerkala.start();
        timerJam = new Timer(1000, e -> lblJam.setText(LocalTime.now().format(Konstanta.FMT_JAM_DETIK)));
        timerJam.setInitialDelay(0);
        timerJam.start();

        setMinimumSize(new Dimension(1000, 640));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        if (!menu.isEmpty()) {
            tampilkan(daftarMenu.containsKey("dashboard") ? "dashboard" : menu.get(0).kunci());
        }
        // Dicek ulang tiap jam agar aplikasi yang terbuka melewati tengah malam tetap membuat cadangan hari baru.
        timerBackup = new Timer(60 * 60 * 1000, e -> jalankanBackupHarian());
        timerBackup.setInitialDelay(0);
        timerBackup.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlKepala = new javax.swing.JPanel();
        pnlKiri = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblSub = new javax.swing.JLabel();
        pnlKanan = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();
        btnPassword = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        pnlMenu = new javax.swing.JPanel();
        pnlIsi = new javax.swing.JPanel();
        pnlKaki = new javax.swing.JPanel();
        lblLogout = new javax.swing.JLabel();
        lblJam = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("DOCK-SCHED");
        setMinimumSize(new java.awt.Dimension(1000, 640));
        getContentPane().setLayout(new java.awt.BorderLayout());
        pnlKepala.setBackground(new java.awt.Color(31, 78, 121));
        pnlKepala.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 16, 10, 16));
        pnlKepala.setLayout(new java.awt.BorderLayout());
        pnlKiri.setOpaque(false);
        pnlKiri.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        lblJudul.setText("DOCK-SCHED");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblJudul.setForeground(new java.awt.Color(255, 255, 255));
        pnlKiri.add(lblJudul);
        lblSub.setText("  Sistem Penjadwalan Slot Loading-Unloading Truck Delivery di Gudang");
        lblSub.setForeground(new java.awt.Color(220, 230, 242));
        pnlKiri.add(lblSub);
        pnlKepala.add(pnlKiri, java.awt.BorderLayout.WEST);
        pnlKanan.setOpaque(false);
        pnlKanan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        lblUser.setText("Pengguna");
        lblUser.setForeground(new java.awt.Color(255, 255, 255));
        pnlKanan.add(lblUser);
        btnPassword.setText("Ganti Password");
        btnPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasswordActionPerformed(evt);
            }
        });
        pnlKanan.add(btnPassword);
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        pnlKanan.add(btnKeluar);
        pnlKepala.add(pnlKanan, java.awt.BorderLayout.EAST);
        getContentPane().add(pnlKepala, java.awt.BorderLayout.NORTH);
        pnlMenu.setBackground(new java.awt.Color(220, 230, 242));
        pnlMenu.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 8, 10, 8));
        pnlMenu.setLayout(new javax.swing.BoxLayout(pnlMenu, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(pnlMenu, java.awt.BorderLayout.WEST);
        pnlIsi.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pnlIsi.setLayout(new java.awt.CardLayout());
        getContentPane().add(pnlIsi, java.awt.BorderLayout.CENTER);
        pnlKaki.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 12, 3, 12));
        pnlKaki.setLayout(new java.awt.BorderLayout());
        lblLogout.setText("Logout otomatis bila tidak aktif 30 menit.");
        pnlKaki.add(lblLogout, java.awt.BorderLayout.WEST);
        lblJam.setText(" ");
        pnlKaki.add(lblJam, java.awt.BorderLayout.EAST);
        getContentPane().add(pnlKaki, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasswordActionPerformed
        gantiPassword();
    }//GEN-LAST:event_btnPasswordActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        if (UiUtil.konfirmasi(this, "Keluar dari akun " + pengguna.getUsername() + "?")) {
            keluar(true, false);
        }
    }//GEN-LAST:event_btnKeluarActionPerformed

    /** Menambahkan tombol menu (sesuai peran) ke panel menu samping hasil GUI Builder. */
    private void isiMenuSamping(List<Menu> menu) {
        ButtonGroup grup = new ButtonGroup();
        for (Menu m : menu) {
            daftarMenu.put(m.kunci(), m);
            JToggleButton b = new JToggleButton(m.label());
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setHorizontalAlignment(JToggleButton.LEFT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setPreferredSize(new Dimension(250, 40));
            b.setFocusPainted(false);
            b.addActionListener(e -> tampilkan(m.kunci()));
            grup.add(b);
            tombolMenu.put(m.kunci(), b);
            pnlMenu.add(b);
            pnlMenu.add(Box.createVerticalStrut(6));
        }
        pnlMenu.add(Box.createVerticalGlue());
    }

    private void tampilkan(String kunci) {
        Menu m = daftarMenu.get(kunci);
        if (m == null) {
            return;
        }
        JPanel panel = halaman.get(kunci);
        if (panel == null) {
            panel = m.pembuat().get();
            halaman.put(kunci, panel);
            pnlIsi.add(panel, kunci);
        }
        tombolMenu.get(kunci).setSelected(true);
        kartu.show(pnlIsi, kunci);
        if (panel instanceof Halaman h) {
            h.saatDitampilkan();
        }
    }

    private void periksaIdle() {
        long batas = Konstanta.IDLE_MENIT * 60_000L;
        if (!sudahKeluar && System.currentTimeMillis() - aktivitasTerakhir >= batas) {
            keluar(true, true);
        }
    }

    /** Menutup sesi, mencatat log, lalu kembali ke layar login (atau menutup aplikasi). */
    private void keluar(boolean kembaliKeLogin, boolean otomatis) {
        if (sudahKeluar) {
            return;
        }
        sudahKeluar = true;
        timerBerkala.stop();
        timerJam.stop();
        timerBackup.stop();
        Toolkit.getDefaultToolkit().removeAWTEventListener(pemantauAktivitas);
        Pengguna p = pengguna;
        Sesi.akhiri();
        Thread logout = new Thread(() -> auth.catatLogout(p, otomatis), "catat-logout");
        logout.start();
        for (Window w : Window.getWindows()) {
            if (w != this && w.isDisplayable() && !(w instanceof LoginFrame)) {
                w.dispose();
            }
        }
        dispose();
        if (kembaliKeLogin) {
            new LoginFrame(otomatis ? "Anda keluar otomatis karena tidak aktif selama "
                    + Konstanta.IDLE_MENIT + " menit." : null).setVisible(true);
        } else {
            try {
                logout.join(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(0);
        }
    }

    private void gantiPassword() {
        GantiPasswordDialog dlg = new GantiPasswordDialog(this);
        dlg.setVisible(true);
        if (!dlg.isDisetujui()) {
            return;
        }
        String passBaru = dlg.getPasswordBaru();
        if (!passBaru.equals(dlg.getPasswordUlang())) {
            UiUtil.peringatan(this, "Konfirmasi password baru tidak sama.");
            return;
        }
        char[] passLama = dlg.getPasswordLama();
        BackgroundTask.<Boolean>jalankan(() -> {
            auth.gantiPassword(pengguna, passLama, passBaru);
            return true;
        }, ok -> UiUtil.info(this, "Password berhasil diganti."), err -> UiUtil.tampilkanError(this, err));
    }

    /** Cadangan basis data otomatis minimal sekali sehari (DS-NF-05). Kegagalan diberitahukan sekali per hari. */
    private void jalankanBackupHarian() {
        BackgroundTask.<Boolean>jalankan(() -> {
            new BackupService().cadangkanBilaPerlu();
            return true;
        }, ok -> { }, err -> {
            System.err.println("Backup harian gagal: " + err);
            if (!LocalDate.now().equals(tanggalPeringatanBackup)) {
                tanggalPeringatanBackup = LocalDate.now();
                UiUtil.peringatan(this, "Cadangan database harian gagal dibuat dan akan dicoba lagi dalam 1 jam.\n\n"
                        + "Detail: " + UiUtil.ringkas(String.valueOf(err.getMessage())));
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlKepala;
    private javax.swing.JPanel pnlKiri;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblSub;
    private javax.swing.JPanel pnlKanan;
    private javax.swing.JLabel lblUser;
    private javax.swing.JButton btnPassword;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlIsi;
    private javax.swing.JPanel pnlKaki;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblJam;
    // End of variables declaration//GEN-END:variables
}
