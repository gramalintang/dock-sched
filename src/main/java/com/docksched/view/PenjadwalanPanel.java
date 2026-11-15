package com.docksched.view;

import com.docksched.controller.PenjadwalanController;
import com.docksched.controller.PenjadwalanController.HasilPenjadwalan;
import com.docksched.controller.Sesi;
import com.docksched.controller.SawCalculator.HasilSaw;
import com.docksched.controller.SlotAllocator.Alokasi;
import com.docksched.model.Dock;
import com.docksched.model.Jadwal;
import com.docksched.model.Pengguna;
import com.docksched.util.BackgroundTask;
import com.docksched.util.Format;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/** Penjadwalan slot dock dengan SAW, hasil jadwal, dan penjadwalan ulang manual (UC-05 - UC-08). Dibuat dengan NetBeans GUI Builder. */
public class PenjadwalanPanel extends javax.swing.JPanel implements Halaman {

    private static final long serialVersionUID = 1L;

    private final PenjadwalanController controller = new PenjadwalanController();
    private final DefaultTableModel modelSaw = UiUtil.modelTabel("Urutan", "Nomor Polisi", "Jenis Muatan",
            "C1 Muatan", "C2 Kapasitas Dock", "C3 Durasi (mnt)", "Skor SAW", "Dock", "Masuk", "Selesai", "Catatan");
    private final DefaultTableModel modelJadwal = UiUtil.modelTabel("ID", "Nomor Polisi", "Sopir", "Jenis Muatan",
            "Dock", "Estimasi Masuk", "Estimasi Selesai", "Skor", "Urutan", "Jenis Jadwal", "Status Truck", "Keterangan");
    private final List<Jadwal> daftarJadwal = new ArrayList<>();

    public PenjadwalanPanel() {
        initComponents();
        TabelData.pasang(tabelSaw, modelSaw);
        TabelData.pasang(tabelJadwal, modelJadwal);
        Pengguna user = Sesi.getPengguna();
        boolean admin = user != null && (user.isProgrammer() || user.getPeran().bisaJadwalUlang());
        btnUlang.setVisible(admin);
        btnBatal.setVisible(admin);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAtas = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        pnlTombol = new javax.swing.JPanel();
        btnJalankan = new javax.swing.JButton();
        btnSegarkan = new javax.swing.JButton();
        btnSalin = new javax.swing.JButton();
        btnUlang = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        splitPane = new javax.swing.JSplitPane();
        pnlSaw = new javax.swing.JPanel();
        scrollSaw = new javax.swing.JScrollPane();
        tabelSaw = new javax.swing.JTable();
        pnlJadwal = new javax.swing.JPanel();
        scrollJadwal = new javax.swing.JScrollPane();
        tabelJadwal = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new java.awt.BorderLayout(0, 8));
        pnlAtas.setLayout(new java.awt.BorderLayout());
        lblJudul.setText("Penjadwalan Slot Dock (Sistem Pendukung Keputusan - SAW)");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblJudul.setForeground(new java.awt.Color(31, 78, 121));
        pnlAtas.add(lblJudul, java.awt.BorderLayout.NORTH);
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnJalankan.setText("Jalankan Penjadwalan (SAW)");
        btnJalankan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJalankanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnJalankan);
        btnSegarkan.setText("Segarkan");
        btnSegarkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSegarkanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnSegarkan);
        btnSalin.setText("Salin Informasi untuk Sopir");
        btnSalin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalinActionPerformed(evt);
            }
        });
        pnlTombol.add(btnSalin);
        btnUlang.setText("Jadwal Ulang Manual...");
        btnUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUlangActionPerformed(evt);
            }
        });
        pnlTombol.add(btnUlang);
        btnBatal.setText("Batalkan Truck...");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        pnlTombol.add(btnBatal);
        pnlAtas.add(pnlTombol, java.awt.BorderLayout.CENTER);
        lblInfo.setText("Tekan \"Jalankan Penjadwalan\" untuk menghitung prioritas truck berstatus Menunggu.");
        pnlAtas.add(lblInfo, java.awt.BorderLayout.SOUTH);
        add(pnlAtas, java.awt.BorderLayout.NORTH);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45);
        pnlSaw.setBorder(javax.swing.BorderFactory.createTitledBorder("Hasil perhitungan SAW terakhir (skor tertinggi = prioritas tertinggi)"));
        pnlSaw.setLayout(new java.awt.BorderLayout());
        scrollSaw.setViewportView(tabelSaw);
        pnlSaw.add(scrollSaw, java.awt.BorderLayout.CENTER);
        splitPane.setTopComponent(pnlSaw);
        pnlJadwal.setBorder(javax.swing.BorderFactory.createTitledBorder("Jadwal aktif (truck yang belum selesai)"));
        pnlJadwal.setLayout(new java.awt.BorderLayout());
        scrollJadwal.setViewportView(tabelJadwal);
        pnlJadwal.add(scrollJadwal, java.awt.BorderLayout.CENTER);
        splitPane.setBottomComponent(pnlJadwal);
        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnJalankanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJalankanActionPerformed
        jalankan();
    }//GEN-LAST:event_btnJalankanActionPerformed

    private void btnSegarkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSegarkanActionPerformed
        muatJadwal();
    }//GEN-LAST:event_btnSegarkanActionPerformed

    private void btnSalinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalinActionPerformed
        salin();
    }//GEN-LAST:event_btnSalinActionPerformed

    private void btnUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUlangActionPerformed
        jadwalUlang();
    }//GEN-LAST:event_btnUlangActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        batalkan();
    }//GEN-LAST:event_btnBatalActionPerformed

    @Override
    public void saatDitampilkan() {
        muatJadwal();
    }

    private void jalankan() {
        btnJalankan.setEnabled(false);
        lblInfo.setForeground(java.awt.Color.DARK_GRAY);
        lblInfo.setText("Menghitung...");
        BackgroundTask.<HasilPenjadwalan>jalankan(() -> controller.jalankan(Sesi.getPengguna()), h -> {
            btnJalankan.setEnabled(true);
            tampilkanSaw(h);
            muatJadwal();
            if (h.saw().isEmpty()) {
                lblInfo.setForeground(UiUtil.MERAH);
                lblInfo.setText("Tidak ada truck berstatus Menunggu yang perlu dijadwalkan"
                        + (h.jumlahManualDipertahankan() > 0 ? " (" + h.jumlahManualDipertahankan()
                        + " jadwal manual dipertahankan)." : "."));
                return;
            }
            lblInfo.setForeground(h.jumlahTanpaDock() > 0 ? UiUtil.MERAH : UiUtil.HIJAU);
            lblInfo.setText("Selesai " + Format.waktu(h.waktu()) + ": " + h.jumlahDijadwalkan() + " truck dijadwalkan, "
                    + h.jumlahTanpaDock() + " tanpa dock, " + h.jumlahManualDipertahankan() + " jadwal manual dipertahankan.");
            if (h.jumlahTanpaDock() > 0) {
                UiUtil.peringatan(this, h.jumlahTanpaDock() + " truck tidak mendapat dock karena tidak ada dock yang "
                        + "sesuai dan berfungsi. Lihat kolom Catatan.");
            }
        }, err -> {
            btnJalankan.setEnabled(true);
            lblInfo.setText(" ");
            UiUtil.tampilkanError(this, err);
        });
    }

    private void tampilkanSaw(HasilPenjadwalan h) {
        modelSaw.setRowCount(0);
        for (Alokasi a : h.alokasi()) {
            HasilSaw s = a.saw();
            modelSaw.addRow(new Object[] {s.urutan(), s.truck().getNomorPolisi(), s.truck().getJenisMuatan().getLabel(),
                Format.desimal(s.c1(), 0), Format.desimal(s.c2(), 0), Format.desimal(s.c3(), 0),
                Format.desimal(s.skor(), 4), a.dock() == null ? "-" : a.dock().getKodeDock(),
                a.masuk() == null ? "-" : Format.waktu(a.masuk()), a.selesai() == null ? "-" : Format.waktu(a.selesai()),
                a.catatan()});
        }
    }

    private void muatJadwal() {
        int idPilih = idJadwalTerpilih();
        BackgroundTask.<List<Jadwal>>jalankan(controller::daftarAktif, data -> {
            daftarJadwal.clear();
            daftarJadwal.addAll(data);
            modelJadwal.setRowCount(0);
            for (Jadwal j : data) {
                modelJadwal.addRow(new Object[] {j.getIdJadwal(), j.getNomorPolisi(), j.getNamaSopir(),
                    j.getJenisMuatan().getLabel(), j.getKodeDock(), Format.waktu(j.getEstimasiWaktuMasuk()),
                    Format.waktu(j.getEstimasiWaktuSelesai()), Format.desimal(j.getSkorPrioritas(), 4), j.getUrutan(),
                    j.getJenis().getLabel(), j.getStatusTruck().getLabel(), j.getKeterangan() == null ? "" : j.getKeterangan()});
            }
            if (idPilih > 0) {
                TabelData.pilihBerdasarkan(tabelJadwal, 0, idPilih);
            }
        }, err -> UiUtil.tampilkanError(this, err));
    }

    private int idJadwalTerpilih() {
        int i = TabelData.barisModelTerpilih(tabelJadwal);
        return i < 0 ? -1 : (Integer) modelJadwal.getValueAt(i, 0);
    }

    private Jadwal jadwalTerpilih() {
        int id = idJadwalTerpilih();
        for (Jadwal j : daftarJadwal) {
            if (j.getIdJadwal() == id) {
                return j;
            }
        }
        UiUtil.peringatan(this, "Pilih satu baris pada tabel Jadwal aktif terlebih dahulu.");
        return null;
    }

    private void salin() {
        if (daftarJadwal.isEmpty()) {
            UiUtil.peringatan(this, "Belum ada jadwal aktif untuk disalin.");
            return;
        }
        String teks = PenjadwalanController.teksInformasiSopir(daftarJadwal);
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(teks), null);
            UiUtil.info(this, "Informasi jadwal disalin ke clipboard. Tempel ke pesan/WhatsApp untuk sopir:\n\n" + teks);
        } catch (IllegalStateException e) {
            UiUtil.info(this, "Clipboard tidak tersedia. Informasi jadwal:\n\n" + teks);
        }
    }

    private void jadwalUlang() {
        Jadwal j = jadwalTerpilih();
        if (j == null) {
            return;
        }
        BackgroundTask.<List<Dock>>jalankan(() -> controller.dockUntukJadwalUlang(j.getJenisMuatan()), docks -> {
            if (docks.isEmpty()) {
                UiUtil.peringatan(this, "Tidak ada dock yang sesuai dan berfungsi untuk muatan " + j.getJenisMuatan().getLabel() + ".");
                return;
            }
            JadwalUlangDialog dlg = new JadwalUlangDialog(UiUtil.frameDari(this), j, docks);
            dlg.setVisible(true);
            if (!dlg.isDisetujui()) {
                return;
            }
            Dock dipilih = dlg.getDockDipilih();
            java.time.LocalDateTime masuk = dlg.getMasuk();
            String alasan = dlg.getAlasan();
            BackgroundTask.<Boolean>jalankan(() -> {
                controller.jadwalUlang(j.getIdJadwal(), dipilih.getIdDock(), masuk, alasan, Sesi.getPengguna());
                return true;
            }, ok -> {
                UiUtil.info(this, "Jadwal truck " + j.getNomorPolisi() + " diperbarui.");
                muatJadwal();
            }, err -> UiUtil.tampilkanError(this, err));
        }, err -> UiUtil.tampilkanError(this, err));
    }

    private void batalkan() {
        Jadwal j = jadwalTerpilih();
        if (j == null) {
            return;
        }
        String alasan = JOptionPane.showInputDialog(this, "Alasan pembatalan truck " + j.getNomorPolisi() + ":",
                "Batalkan Truck", JOptionPane.QUESTION_MESSAGE);
        if (alasan == null) {
            return;
        }
        BackgroundTask.<Boolean>jalankan(() -> {
            controller.batalkanTruck(j.getIdJadwal(), alasan, Sesi.getPengguna());
            return true;
        }, ok -> {
            UiUtil.info(this, "Truck " + j.getNomorPolisi() + " dibatalkan dan dikeluarkan dari antrean.");
            muatJadwal();
        }, err -> UiUtil.tampilkanError(this, err));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnJalankan;
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JButton btnSalin;
    private javax.swing.JButton btnUlang;
    private javax.swing.JButton btnBatal;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JPanel pnlSaw;
    private javax.swing.JScrollPane scrollSaw;
    private javax.swing.JTable tabelSaw;
    private javax.swing.JPanel pnlJadwal;
    private javax.swing.JScrollPane scrollJadwal;
    private javax.swing.JTable tabelJadwal;
    // End of variables declaration//GEN-END:variables
}
