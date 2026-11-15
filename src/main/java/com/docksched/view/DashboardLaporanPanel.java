package com.docksched.view;

import com.docksched.controller.EksporExcel;
import com.docksched.controller.EksporPdf;
import com.docksched.controller.LaporanController;
import com.docksched.controller.LaporanController.BarisHarian;
import com.docksched.controller.LaporanController.BarisTahap;
import com.docksched.controller.LaporanController.BarisUtilisasi;
import com.docksched.controller.LaporanController.DataDashboard;
import com.docksched.controller.LaporanController.Rekap;
import com.docksched.controller.LaporanController.StatusDockTampil;
import com.docksched.controller.LaporanDokumen;
import com.docksched.controller.Sesi;
import com.docksched.model.StatusProses;
import com.docksched.model.Truck;
import com.docksched.util.BackgroundTask;
import com.docksched.util.Format;
import com.docksched.util.Konstanta;
import com.docksched.util.RefreshBerkala;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/** Dashboard status truck & dock (UC-12) serta laporan performa dan ekspor (UC-13, UC-14). Dibuat dengan NetBeans GUI Builder. */
public class DashboardLaporanPanel extends javax.swing.JPanel implements Halaman {

    private static final long serialVersionUID = 1L;

    private final LaporanController controller = new LaporanController();

    private final DefaultTableModel modelDock = UiUtil.modelTabel("Dock", "Jenis", "Kapasitas", "Ketersediaan",
            "Truck di Dock", "Status Truck", "Durasi Proses");
    private final DefaultTableModel modelTruck = UiUtil.modelTabel("Nomor Polisi", "Sopir", "Jenis Muatan", "Status",
            "Dock", "Durasi Proses", "Perkiraan Datang");
    private final DefaultTableModel modelHarian = UiUtil.modelTabel("Tanggal", "Terdaftar", "Selesai",
            "Rata-rata Durasi (mnt)", "Melebihi 45 mnt");
    private final DefaultTableModel modelTahap = UiUtil.modelTabel("Tahap", "Rata-rata (mnt)",
            "Tahap Terpanjang pada Truck Terlambat");
    private final DefaultTableModel modelUtil = UiUtil.modelTabel("Dock", "Jenis", "Terpakai (mnt)", "Utilisasi (%)");
    private Rekap rekapTerakhir;

    public DashboardLaporanPanel() {
        initComponents();
        TabelData.pasang(tabelDock, modelDock);
        TabelData.pasang(tabelTruck, modelTruck);
        TabelData.pasang(tabelHarian, modelHarian);
        TabelData.pasang(tabelTahap, modelTahap);
        TabelData.pasang(tabelUtil, modelUtil);
        pnlTruck.setBorder(BorderFactory.createTitledBorder("Truck hari ini (merah = melebihi "
                + Konstanta.TARGET_MENIT + " menit sejak parkir di dock)"));
        UiUtil.aturSpinnerTanggal(spnDari, LocalDate.now().minusDays(6));
        UiUtil.aturSpinnerTanggal(spnSampai, LocalDate.now());
        // Pembaruan otomatis berkala tanpa refresh manual (DS-NF-02).
        RefreshBerkala.pasang(pnlDashboard, Konstanta.REFRESH_DASHBOARD_DETIK, this::muatDashboard);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblJudul = new javax.swing.JLabel();
        tab = new javax.swing.JTabbedPane();
        pnlDashboard = new javax.swing.JPanel();
        pnlRingkas = new javax.swing.JPanel();
        pnlKartu = new javax.swing.JPanel();
        lblPembaruan = new javax.swing.JLabel();
        splitDashboard = new javax.swing.JSplitPane();
        pnlDock = new javax.swing.JPanel();
        scrollDock = new javax.swing.JScrollPane();
        tabelDock = new javax.swing.JTable();
        pnlTruck = new javax.swing.JPanel();
        scrollTruck = new javax.swing.JScrollPane();
        tabelTruck = new javax.swing.JTable();
        pnlLaporan = new javax.swing.JPanel();
        pnlKepala = new javax.swing.JPanel();
        pnlFilter = new javax.swing.JPanel();
        lblDari = new javax.swing.JLabel();
        spnDari = new javax.swing.JSpinner();
        lblSampai = new javax.swing.JLabel();
        spnSampai = new javax.swing.JSpinner();
        btnTampil = new javax.swing.JButton();
        btnPdf = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        lblRingkas = new javax.swing.JLabel();
        tabLaporan = new javax.swing.JTabbedPane();
        scrollHarian = new javax.swing.JScrollPane();
        tabelHarian = new javax.swing.JTable();
        scrollTahap = new javax.swing.JScrollPane();
        tabelTahap = new javax.swing.JTable();
        scrollUtil = new javax.swing.JScrollPane();
        tabelUtil = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new java.awt.BorderLayout());
        lblJudul.setText("Dashboard & Laporan");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblJudul.setForeground(new java.awt.Color(31, 78, 121));
        add(lblJudul, java.awt.BorderLayout.NORTH);
        pnlDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pnlDashboard.setLayout(new java.awt.BorderLayout(0, 8));
        pnlRingkas.setLayout(new java.awt.BorderLayout(0, 4));
        pnlKartu.setLayout(new java.awt.GridLayout(1, 0, 8, 0));
        pnlRingkas.add(pnlKartu, java.awt.BorderLayout.CENTER);
        lblPembaruan.setText(" ");
        pnlRingkas.add(lblPembaruan, java.awt.BorderLayout.SOUTH);
        pnlDashboard.add(pnlRingkas, java.awt.BorderLayout.NORTH);
        splitDashboard.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitDashboard.setResizeWeight(0.4);
        pnlDock.setBorder(javax.swing.BorderFactory.createTitledBorder("Status dock"));
        pnlDock.setLayout(new java.awt.BorderLayout());
        scrollDock.setViewportView(tabelDock);
        pnlDock.add(scrollDock, java.awt.BorderLayout.CENTER);
        splitDashboard.setTopComponent(pnlDock);
        pnlTruck.setBorder(javax.swing.BorderFactory.createTitledBorder("Truck hari ini"));
        pnlTruck.setLayout(new java.awt.BorderLayout());
        scrollTruck.setViewportView(tabelTruck);
        pnlTruck.add(scrollTruck, java.awt.BorderLayout.CENTER);
        splitDashboard.setBottomComponent(pnlTruck);
        pnlDashboard.add(splitDashboard, java.awt.BorderLayout.CENTER);
        tab.addTab("Dashboard Status", pnlDashboard);
        pnlLaporan.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        pnlLaporan.setLayout(new java.awt.BorderLayout(0, 8));
        pnlKepala.setLayout(new java.awt.BorderLayout());
        pnlFilter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        lblDari.setText("Dari tanggal");
        pnlFilter.add(lblDari);
        pnlFilter.add(spnDari);
        lblSampai.setText("sampai");
        pnlFilter.add(lblSampai);
        pnlFilter.add(spnSampai);
        btnTampil.setText("Tampilkan Laporan");
        btnTampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilActionPerformed(evt);
            }
        });
        pnlFilter.add(btnTampil);
        btnPdf.setText("Ekspor PDF");
        btnPdf.setEnabled(false);
        btnPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPdfActionPerformed(evt);
            }
        });
        pnlFilter.add(btnPdf);
        btnExcel.setText("Ekspor Excel");
        btnExcel.setEnabled(false);
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });
        pnlFilter.add(btnExcel);
        pnlKepala.add(pnlFilter, java.awt.BorderLayout.NORTH);
        lblRingkas.setText("Pilih rentang tanggal, lalu tekan Tampilkan Laporan.");
        pnlKepala.add(lblRingkas, java.awt.BorderLayout.SOUTH);
        pnlLaporan.add(pnlKepala, java.awt.BorderLayout.NORTH);
        scrollHarian.setViewportView(tabelHarian);
        tabLaporan.addTab("Rekap per Hari", scrollHarian);
        scrollTahap.setViewportView(tabelTahap);
        tabLaporan.addTab("Rata-rata per Tahap", scrollTahap);
        scrollUtil.setViewportView(tabelUtil);
        tabLaporan.addTab("Utilisasi Dock", scrollUtil);
        pnlLaporan.add(tabLaporan, java.awt.BorderLayout.CENTER);
        tab.addTab("Laporan Performa", pnlLaporan);
        add(tab, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilActionPerformed
        tampilkanLaporan();
    }//GEN-LAST:event_btnTampilActionPerformed

    private void btnPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPdfActionPerformed
        ekspor(true);
    }//GEN-LAST:event_btnPdfActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        ekspor(false);
    }//GEN-LAST:event_btnExcelActionPerformed

    @Override
    public void saatDitampilkan() {
        muatDashboard();
    }

    private void muatDashboard() {
        BackgroundTask.<DataDashboard>jalankan(controller::ambilDashboard, this::tampilkanDashboard,
                err -> {
                    lblPembaruan.setForeground(UiUtil.MERAH);
                    lblPembaruan.setText("Gagal memperbarui: " + UiUtil.ringkas(err.getMessage()));
                });
    }

    private void tampilkanDashboard(DataDashboard d) {
        pnlKartu.removeAll();
        for (StatusProses s : StatusProses.values()) {
            pnlKartu.add(kartu(s.getLabel(), String.valueOf(d.hitung(s)), UiUtil.BIRU));
        }
        long lewat = d.jumlahMelebihi();
        pnlKartu.add(kartu("Melebihi " + Konstanta.TARGET_MENIT + " mnt", String.valueOf(lewat),
                lewat > 0 ? UiUtil.MERAH : UiUtil.HIJAU));
        pnlKartu.revalidate();
        pnlKartu.repaint();

        modelDock.setRowCount(0);
        Set<Integer> merahDock = new HashSet<>();
        List<StatusDockTampil> docks = d.docks();
        for (int i = 0; i < docks.size(); i++) {
            StatusDockTampil s = docks.get(i);
            if (s.melebihi()) {
                merahDock.add(i);
            }
            modelDock.addRow(new Object[] {s.dock().getKodeDock(), s.dock().getKeteranganJenis(), s.dock().getKapasitas(),
                s.dock().getStatus().getLabel(), s.nomorPolisi() == null ? "-" : s.nomorPolisi(),
                s.statusTruck() == null ? "-" : s.statusTruck().getLabel(), Format.durasi(s.durasiDetik())});
        }
        TabelData.setBarisMerah(tabelDock, merahDock);

        modelTruck.setRowCount(0);
        Set<Integer> merahTruck = new HashSet<>();
        List<Truck> trucks = d.trucks();
        for (int i = 0; i < trucks.size(); i++) {
            Truck t = trucks.get(i);
            if (t.getStatusProses() != StatusProses.SELESAI_BERANGKAT && t.melebihiTarget(d.waktu())) {
                merahTruck.add(i);
            }
            modelTruck.addRow(new Object[] {t.getNomorPolisi(), t.getNamaSopir(), t.getJenisMuatan().getLabel(),
                t.getStatusProses().getLabel(), t.getKodeDock() == null ? "-" : t.getKodeDock(),
                Format.durasi(t.durasiProsesDetik(d.waktu())), Format.waktu(t.getPerkiraanKedatangan())});
        }
        TabelData.setBarisMerah(tabelTruck, merahTruck);
        lblPembaruan.setForeground(Color.DARK_GRAY);
        lblPembaruan.setText("Diperbarui otomatis tiap " + Konstanta.REFRESH_DASHBOARD_DETIK + " detik. Terakhir: "
                + d.waktu().format(Konstanta.FMT_JAM_DETIK));
    }

    private JPanel kartu(String judul, String nilai, Color warna) {
        JPanel k = new JPanel(new BorderLayout());
        k.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(warna, 2),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        JLabel angka = new JLabel(nilai, JLabel.CENTER);
        angka.setFont(angka.getFont().deriveFont(Font.BOLD, 26f));
        angka.setForeground(warna);
        JLabel teks = new JLabel(judul, JLabel.CENTER);
        k.add(angka, BorderLayout.CENTER);
        k.add(teks, BorderLayout.SOUTH);
        return k;
    }

    private void tampilkanLaporan() {
        LocalDate dari = UiUtil.ambilTanggal(spnDari);
        LocalDate sampai = UiUtil.ambilTanggal(spnSampai);
        BackgroundTask.<Rekap>jalankan(() -> controller.rekap(dari, sampai), r -> {
            rekapTerakhir = r;
            lblRingkas.setForeground(Color.DARK_GRAY);
            lblRingkas.setText("<html>Terdaftar <b>" + r.totalTerdaftar() + "</b> | Selesai <b>" + r.totalSelesai()
                    + "</b> | Rata-rata durasi proses <b>" + Format.desimal(r.rataDurasiProsesMenit(), 1)
                    + " mnt</b> | Rata-rata menunggu <b>" + Format.desimal(r.rataMenungguMenit(), 1)
                    + " mnt</b> | Melebihi " + Konstanta.TARGET_MENIT + " mnt: <b>" + r.terlambat() + " ("
                    + Format.desimal(r.persenTerlambat(), 1) + "%)</b></html>");
            modelHarian.setRowCount(0);
            for (BarisHarian h : r.harian()) {
                modelHarian.addRow(new Object[] {h.tanggal().format(Konstanta.FMT_TANGGAL), h.terdaftar(), h.selesai(),
                    Format.desimal(h.rataDurasiMenit(), 1), h.terlambat()});
            }
            modelTahap.setRowCount(0);
            for (BarisTahap t : r.tahap()) {
                modelTahap.addRow(new Object[] {t.tahap(), Format.desimal(t.rataMenit(), 1), t.terlambatTerpanjang()});
            }
            modelUtil.setRowCount(0);
            for (BarisUtilisasi u : r.utilisasi()) {
                modelUtil.addRow(new Object[] {u.kodeDock(), u.jenis(), Format.desimal(u.terpakaiMenit(), 1),
                    Format.desimal(u.persen(), 1)});
            }
            btnPdf.setEnabled(true);
            btnExcel.setEnabled(true);
        }, err -> UiUtil.tampilkanError(this, err));
    }

    private void ekspor(boolean pdf) {
        if (rekapTerakhir == null) {
            UiUtil.peringatan(this, "Tampilkan laporan terlebih dahulu.");
            return;
        }
        String ext = pdf ? "pdf" : "xlsx";
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(pdf ? "Berkas PDF (*.pdf)" : "Berkas Excel (*.xlsx)", ext));
        fc.setSelectedFile(new File("laporan-docksched-" + rekapTerakhir.dari() + "_" + rekapTerakhir.sampai() + "." + ext));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith("." + ext)) {
            f = new File(f.getParentFile(), f.getName() + "." + ext);
        }
        if (f.exists() && !UiUtil.konfirmasi(this, "Berkas " + f.getName() + " sudah ada. Timpa?")) {
            return;
        }
        LaporanDokumen dok = LaporanController.buatDokumen(rekapTerakhir, Sesi.getPengguna().getNamaLengkap());
        File tujuan = f;
        BackgroundTask.<File>jalankan(() -> {
            if (pdf) {
                EksporPdf.tulis(tujuan.toPath(), dok);
            } else {
                EksporExcel.tulis(tujuan.toPath(), dok);
            }
            return tujuan;
        }, hasil -> UiUtil.info(this, "Laporan tersimpan:\n" + hasil.getAbsolutePath()),
                err -> {
                    if (err instanceof IOException) {
                        UiUtil.error(this, "Berkas tidak dapat ditulis: " + err.getMessage());
                    } else {
                        UiUtil.tampilkanError(this, err);
                    }
                });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblJudul;
    private javax.swing.JTabbedPane tab;
    private javax.swing.JPanel pnlDashboard;
    private javax.swing.JPanel pnlRingkas;
    private javax.swing.JPanel pnlKartu;
    private javax.swing.JLabel lblPembaruan;
    private javax.swing.JSplitPane splitDashboard;
    private javax.swing.JPanel pnlDock;
    private javax.swing.JScrollPane scrollDock;
    private javax.swing.JTable tabelDock;
    private javax.swing.JPanel pnlTruck;
    private javax.swing.JScrollPane scrollTruck;
    private javax.swing.JTable tabelTruck;
    private javax.swing.JPanel pnlLaporan;
    private javax.swing.JPanel pnlKepala;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JLabel lblDari;
    private javax.swing.JSpinner spnDari;
    private javax.swing.JLabel lblSampai;
    private javax.swing.JSpinner spnSampai;
    private javax.swing.JButton btnTampil;
    private javax.swing.JButton btnPdf;
    private javax.swing.JButton btnExcel;
    private javax.swing.JLabel lblRingkas;
    private javax.swing.JTabbedPane tabLaporan;
    private javax.swing.JScrollPane scrollHarian;
    private javax.swing.JTable tabelHarian;
    private javax.swing.JScrollPane scrollTahap;
    private javax.swing.JTable tabelTahap;
    private javax.swing.JScrollPane scrollUtil;
    private javax.swing.JTable tabelUtil;
    // End of variables declaration//GEN-END:variables
}
