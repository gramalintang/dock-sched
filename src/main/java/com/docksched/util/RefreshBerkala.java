package com.docksched.util;

import java.awt.event.HierarchyEvent;
import javax.swing.JComponent;
import javax.swing.Timer;

/** Menjalankan tugas segera saat komponen tampil, lalu berkala selama masih terlihat (dashboard, DS-NF-02). */
public final class RefreshBerkala {

    private RefreshBerkala() {
    }

    public static void pasang(JComponent komponen, int detik, Runnable tugas) {
        Timer timer = new Timer(detik * 1000, e -> tugas.run());
        timer.setRepeats(true);
        komponen.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (komponen.isShowing()) {
                    tugas.run();
                    timer.restart();
                } else {
                    timer.stop();
                }
            }
        });
    }
}
