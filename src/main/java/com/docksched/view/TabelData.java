package com.docksched.view;

import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Fungsi bantu JTable + DefaultTableModel: tabel read-only dengan penandaan baris merah
 * (proses melebihi 45 menit). JTable dibuat dari NetBeans GUI Builder, lalu disiapkan lewat {@link #pasang}.
 */
public final class TabelData {

    static final Color MERAH_MUDA = new Color(255, 205, 210);
    static final Color MERAH_TUA = new Color(160, 0, 0);
    private static final String PROP_MERAH = "docksched.barisMerah";

    private TabelData() {
    }

    /** Memasang model dan tampilan standar pada JTable hasil GUI Builder. */
    public static void pasang(JTable tabel, DefaultTableModel model) {
        tabel.setModel(model);
        tabel.setRowHeight(26);
        tabel.setFillsViewportHeight(true);
        tabel.setAutoCreateRowSorter(true);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.setShowGrid(true);
        tabel.setGridColor(new Color(225, 225, 225));
        tabel.setDefaultRenderer(Object.class, new RendererBaris());
    }

    /** Menandai baris (indeks model) yang berwarna merah. */
    public static void setBarisMerah(JTable tabel, Set<Integer> baris) {
        tabel.putClientProperty(PROP_MERAH, new HashSet<>(baris));
        tabel.repaint();
    }

    /** Indeks model dari baris terpilih, atau -1. */
    public static int barisModelTerpilih(JTable tabel) {
        int v = tabel.getSelectedRow();
        return v < 0 ? -1 : tabel.convertRowIndexToModel(v);
    }

    /** Memilih baris yang kolom ke-{@code kolom} (indeks model) nilainya sama dengan {@code nilai}. */
    public static void pilihBerdasarkan(JTable tabel, int kolom, Object nilai) {
        if (nilai == null) {
            return;
        }
        for (int i = 0; i < tabel.getModel().getRowCount(); i++) {
            if (nilai.equals(tabel.getModel().getValueAt(i, kolom))) {
                int v = tabel.convertRowIndexToView(i);
                tabel.getSelectionModel().setSelectionInterval(v, v);
                return;
            }
        }
    }

    private static final class RendererBaris extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        @SuppressWarnings("unchecked")
        public Component getTableCellRendererComponent(JTable tabel, Object nilai, boolean terpilih, boolean fokus,
                int baris, int kolom) {
            Component c = super.getTableCellRendererComponent(tabel, nilai, terpilih, fokus, baris, kolom);
            if (!terpilih) {
                Object o = tabel.getClientProperty(PROP_MERAH);
                boolean merah = o instanceof Set<?> s && s.contains(tabel.convertRowIndexToModel(baris));
                c.setBackground(merah ? MERAH_MUDA : tabel.getBackground());
                c.setForeground(merah ? MERAH_TUA : tabel.getForeground());
            }
            if (c instanceof JComponent jc) {
                jc.setToolTipText(nilai == null ? null : String.valueOf(nilai));
            }
            return c;
        }
    }
}
