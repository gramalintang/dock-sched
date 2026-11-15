package com.docksched;

import com.docksched.view.LoginFrame;
import com.docksched.view.UiUtil;
import java.awt.EventQueue;
import javax.swing.JOptionPane;

/** Titik masuk aplikasi DOCK-SCHED. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        // Kesalahan tak terduga ditampilkan sebagai pesan, bukan membuat aplikasi diam-diam berhenti.
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan yang tidak terduga:\n" + UiUtil.ringkas(String.valueOf(e)),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE));
        });
        EventQueue.invokeLater(() -> {
            UiUtil.pasangTampilan();
            new LoginFrame(null).setVisible(true);
        });
    }
}
