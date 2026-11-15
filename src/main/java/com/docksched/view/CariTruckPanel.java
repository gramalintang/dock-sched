package com.docksched.view;

import com.docksched.controller.Sesi;
import com.docksched.controller.TruckController;
import com.docksched.model.JenisMuatan;
import com.docksched.model.Truck;
import com.docksched.util.AplikasiException;
import com.docksched.util.BackgroundTask;
import com.docksched.util.Format;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/** Mencari dan menyunting data truck berstatus Menunggu (UC-03, UC-04; DS-F-02.5). Dibuat dengan NetBeans GUI Builder. */
public class CariTruckPanel extends javax.swing.JPanel implements Halaman {

    private static final long serialVersionUID = 1L;
    private static final String SEMUA = "Semua jenis muatan";

    private final TruckController controller = new TruckController();
    private final DefaultTableModel model = UiUtil.modelTabel("ID", "Nomor Polisi", "Sopir", "Jenis Muatan",
            "Pemasok / Pelanggan", "Perkiraan Datang", "Durasi (mnt)", "Status");
    private final FormTruck form = new FormTruck();
    private final List<Truck> hasil = new ArrayList<>();
    private Truck terpilih;

    public CariTruckPanel() {
        initComponents();
        cbJenis.addItem(SEMUA);
        for (JenisMuatan j : JenisMuatan.values()) {
            cbJenis.addItem(j);
        }
        TabelData.pasang(tabel, model);
        tabel.getColumnModel().getColumn(0).setMaxWidth(50);
        pnlSunting.add(form, BorderLayout.CENTER);
        setFormAktif(false);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilih();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblJudul = new javax.swing.JLabel();
        pnlIsi = new javax.swing.JPanel();
        pnlAtas = new javax.swing.JPanel();
        pnlCari = new javax.swing.JPanel();
        lblKata = new javax.swing.JLabel();
        txtKata = new javax.swing.JTextField();
        cbJenis = new javax.swing.JComboBox<>();
        btnCari = new javax.swing.JButton();
        btnSemua = new javax.swing.JButton();
        scrollTabel = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();
        pnlBawah = new javax.swing.JPanel();
        pnlSunting = new javax.swing.JPanel();
        pnlTombol = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new java.awt.BorderLayout(0, 8));
        lblJudul.setText("Cari & Sunting Data Truck");
        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblJudul.setForeground(new java.awt.Color(31, 78, 121));
        add(lblJudul, java.awt.BorderLayout.NORTH);
        pnlIsi.setLayout(new java.awt.BorderLayout(0, 8));
        pnlAtas.setLayout(new java.awt.BorderLayout());
        pnlCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        lblKata.setText("Nomor polisi / sopir / pemasok:");
        pnlCari.add(lblKata);
        txtKata.setColumns(22);
        txtKata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKataActionPerformed(evt);
            }
        });
        pnlCari.add(txtKata);
        pnlCari.add(cbJenis);
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        pnlCari.add(btnCari);
        btnSemua.setText("Tampilkan Semua");
        btnSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSemuaActionPerformed(evt);
            }
        });
        pnlCari.add(btnSemua);
        pnlAtas.add(pnlCari, java.awt.BorderLayout.NORTH);
        scrollTabel.setPreferredSize(new java.awt.Dimension(800, 230));
        scrollTabel.setViewportView(tabel);
        pnlAtas.add(scrollTabel, java.awt.BorderLayout.CENTER);
        lblInfo.setText(" ");
        pnlAtas.add(lblInfo, java.awt.BorderLayout.SOUTH);
        pnlIsi.add(pnlAtas, java.awt.BorderLayout.CENTER);
        pnlBawah.setLayout(new java.awt.BorderLayout());
        pnlSunting.setBorder(javax.swing.BorderFactory.createTitledBorder("Sunting Data Truck (hanya truck berstatus Menunggu)"));
        pnlSunting.setLayout(new java.awt.BorderLayout());
        pnlBawah.add(pnlSunting, java.awt.BorderLayout.CENTER);
        pnlTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        btnSimpan.setText("Simpan Perubahan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        pnlTombol.add(btnSimpan);
        pnlBawah.add(pnlTombol, java.awt.BorderLayout.SOUTH);
        pnlIsi.add(pnlBawah, java.awt.BorderLayout.SOUTH);
        add(pnlIsi, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtKataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKataActionPerformed
        muat();
    }//GEN-LAST:event_txtKataActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        muat();
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSemuaActionPerformed
        txtKata.setText("");
        cbJenis.setSelectedIndex(0);
        muat();
    }//GEN-LAST:event_btnSemuaActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpan();
    }//GEN-LAST:event_btnSimpanActionPerformed

    @Override
    public void saatDitampilkan() {
        muat();
    }

    private void setFormAktif(boolean aktif) {
        form.setEnabled(aktif);
        btnSimpan.setEnabled(aktif);
    }

    private void muat() {
        String kata = txtKata.getText();
        Object pilihan = cbJenis.getSelectedItem();
        JenisMuatan jenis = pilihan instanceof JenisMuatan j ? j : null;
        int idLama = terpilih == null ? -1 : terpilih.getIdTruck();
        BackgroundTask.<List<Truck>>jalankan(() -> controller.cari(kata, jenis), data -> {
            hasil.clear();
            hasil.addAll(data);
            model.setRowCount(0);
            for (Truck t : data) {
                model.addRow(new Object[] {t.getIdTruck(), t.getNomorPolisi(), t.getNamaSopir(),
                    t.getJenisMuatan().getLabel(), t.getPemasokPelanggan(), Format.waktu(t.getPerkiraanKedatangan()),
                    t.getEstimasiDurasiMenit(), t.getStatusProses().getLabel()});
            }
            terpilih = null;
            setFormAktif(false);
            form.bersihkan();
            if (data.isEmpty()) {
                lblInfo.setForeground(UiUtil.MERAH);
                lblInfo.setText("Data truck tidak ditemukan.");
            } else {
                lblInfo.setForeground(java.awt.Color.DARK_GRAY);
                lblInfo.setText(data.size() + " truck berstatus Menunggu. Pilih satu baris untuk menyunting.");
                TabelData.pilihBerdasarkan(tabel, 0, idLama);
            }
        }, err -> UiUtil.tampilkanError(this, err));
    }

    private void pilih() {
        int i = TabelData.barisModelTerpilih(tabel);
        if (i < 0 || i >= hasil.size()) {
            return;
        }
        terpilih = hasil.get(i);
        form.isi(terpilih);
        setFormAktif(true);
    }

    private void simpan() {
        if (terpilih == null) {
            return;
        }
        Truck t = form.ambilTruck();
        t.setIdTruck(terpilih.getIdTruck());
        try {
            controller.validasi(t);
        } catch (AplikasiException e) {
            form.tandaiDariPesan(e.getMessage());
            UiUtil.peringatan(this, e.getMessage());
            return;
        }
        form.hapusTanda();
        btnSimpan.setEnabled(false);
        BackgroundTask.<Boolean>jalankan(() -> {
            controller.perbarui(t, Sesi.getPengguna());
            return true;
        }, ok -> {
            UiUtil.info(this, "Data truck " + t.getNomorPolisi() + " berhasil diperbarui.\n"
                    + "Bila jenis muatan, durasi, atau waktu kedatangan berubah, jalankan penjadwalan ulang.");
            muat();
        }, err -> {
            btnSimpan.setEnabled(true);
            UiUtil.tampilkanError(this, err);
            muat();
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblJudul;
    private javax.swing.JPanel pnlIsi;
    private javax.swing.JPanel pnlAtas;
    private javax.swing.JPanel pnlCari;
    private javax.swing.JLabel lblKata;
    private javax.swing.JTextField txtKata;
    private javax.swing.JComboBox<Object> cbJenis;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnSemua;
    private javax.swing.JScrollPane scrollTabel;
    private javax.swing.JTable tabel;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPanel pnlBawah;
    private javax.swing.JPanel pnlSunting;
    private javax.swing.JPanel pnlTombol;
    private javax.swing.JButton btnSimpan;
    // End of variables declaration//GEN-END:variables
}
