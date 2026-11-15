package com.docksched.view;

import com.docksched.util.AplikasiException;
import com.docksched.util.Konstanta;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

/** Fungsi bantu tampilan Swing yang dipakai banyak layar. */
public final class UiUtil {

    public static final Color BIRU = new Color(0x1F4E79);
    public static final Color BIRU_MUDA = new Color(0xDCE6F2);
    public static final Color MERAH = new Color(0xC00000);
    public static final Color HIJAU = new Color(0x2E7D32);
    private static final String PROP_BORDER_ASLI = "docksched.borderAsli";

    private UiUtil() {
    }

    /** Memasang look and feel sistem (tampilan Windows di Windows) dan ukuran font yang nyaman. */
    public static void pasangTampilan() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Tampilan bawaan Swing tetap dipakai.
        }
        Font dasar = UIManager.getFont("Label.font");
        String nama = dasar != null ? dasar.getFamily() : "Dialog";
        Enumeration<Object> kunci = UIManager.getDefaults().keys();
        while (kunci.hasMoreElements()) {
            Object k = kunci.nextElement();
            Object nilai = UIManager.get(k);
            if (k.toString().endsWith(".font") && nilai instanceof Font f) {
                UIManager.put(k, new FontUIResource(nama, f.getStyle(), Math.max(13, f.getSize())));
            }
        }
    }

    public static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, Konstanta.NAMA_APLIKASI, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void peringatan(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean konfirmasi(Component induk, String pesan) {
        return JOptionPane.showConfirmDialog(induk, pesan, "Konfirmasi", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Menampilkan kesalahan dengan bahasa yang mudah dipahami pengguna. */
    public static void tampilkanError(Component induk, Throwable t) {
        Throwable dasar = t;
        while (dasar.getCause() != null && !(dasar instanceof AplikasiException) && !(dasar instanceof SQLException)) {
            dasar = dasar.getCause();
        }
        if (dasar instanceof AplikasiException) {
            peringatan(induk, dasar.getMessage());
        } else if (dasar instanceof SQLException) {
            error(induk, "Terjadi kesalahan pada database.\nPastikan MySQL berjalan dan pengaturan koneksi benar.\n\n"
                    + "Detail: " + ringkas(dasar.getMessage()));
        } else {
            error(induk, "Terjadi kesalahan yang tidak terduga:\n" + ringkas(String.valueOf(dasar)));
        }
    }

    public static String ringkas(String teks) {
        if (teks == null) {
            return "-";
        }
        String t = teks.replace('\n', ' ').trim();
        return t.length() > 300 ? t.substring(0, 300) + "..." : t;
    }

    public static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // ---- tanggal & waktu -------------------------------------------------

    public static void aturSpinnerWaktu(JSpinner s, LocalDateTime awal) {
        s.setModel(new SpinnerDateModel(toDate(awal), null, null, java.util.Calendar.MINUTE));
        s.setEditor(new JSpinner.DateEditor(s, "dd/MM/yyyy HH:mm"));
    }

    public static void aturSpinnerTanggal(JSpinner s, LocalDate awal) {
        s.setModel(new SpinnerDateModel(toDate(awal.atStartOfDay()), null, null, java.util.Calendar.DAY_OF_MONTH));
        s.setEditor(new JSpinner.DateEditor(s, "dd/MM/yyyy"));
    }

    public static void aturSpinnerAngka(JSpinner s, int awal, int min, int maks, int langkah) {
        s.setModel(new SpinnerNumberModel(awal, min, maks, langkah));
    }

    /** Frame induk dari sebuah komponen (untuk dialog modal), atau null. */
    public static java.awt.Frame frameDari(Component c) {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(c);
        return w instanceof java.awt.Frame f ? f : null;
    }

    public static LocalDateTime ambilWaktu(JSpinner s) {
        try {
            s.commitEdit();
        } catch (java.text.ParseException e) {
            // Nilai terakhir yang valid dipakai.
        }
        return fromDate((Date) s.getValue());
    }

    public static LocalDate ambilTanggal(JSpinner s) {
        return ambilWaktu(s).toLocalDate();
    }

    public static void setWaktu(JSpinner s, LocalDateTime w) {
        s.setValue(toDate(w));
    }

    public static int ambilAngka(JSpinner s) {
        try {
            s.commitEdit();
        } catch (java.text.ParseException e) {
            // Nilai terakhir yang valid dipakai.
        }
        return ((Number) s.getValue()).intValue();
    }

    private static Date toDate(LocalDateTime w) {
        return Date.from(w.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime fromDate(Date d) {
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    // ---- penandaan field tidak valid (activity diagram: "Tandai Field yang Belum Valid") ----

    public static void tandaiSalah(JComponent c, boolean salah) {
        if (c.getClientProperty(PROP_BORDER_ASLI) == null) {
            c.putClientProperty(PROP_BORDER_ASLI, c.getBorder() == null ? "" : c.getBorder());
        }
        Object asli = c.getClientProperty(PROP_BORDER_ASLI);
        if (salah) {
            c.setBorder(BorderFactory.createLineBorder(MERAH, 2));
        } else {
            c.setBorder(asli instanceof Border b ? b : null);
        }
    }
}
