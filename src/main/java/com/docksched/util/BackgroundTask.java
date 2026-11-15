package com.docksched.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Menjalankan akses database di thread terpisah agar jendela tidak membeku, hasilnya kembali ke Event Dispatch Thread. */
public final class BackgroundTask {

    private BackgroundTask() {
    }

    public static <T> void jalankan(Callable<T> kerja, Consumer<T> sukses, Consumer<Throwable> gagal) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return kerja.call();
            }

            @Override
            protected void done() {
                try {
                    sukses.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    gagal.accept(e);
                } catch (ExecutionException e) {
                    gagal.accept(e.getCause() != null ? e.getCause() : e);
                }
            }
        }.execute();
    }
}
