package com.docksched.controller;

import com.docksched.model.Dock;
import com.docksched.model.PengaturanSpk;
import com.docksched.model.Truck;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Simple Additive Weighting (SAW) untuk skor prioritas truck (UC-06; DS-F-03.1, DS-F-03.2).
 *
 * <p>Tiga kriteria (SRS 2.3):
 * <ul>
 * <li>C1 Prioritas jenis muatan (benefit): nilai bahan baku &gt; barang jadi sesuai pengaturan Admin.</li>
 * <li>C2 Kesesuaian dan kapasitas dock (benefit): total kapasitas dock yang sesuai jenis muatan dan tidak rusak.</li>
 * <li>C3 Estimasi durasi bongkar-muat (cost): durasi lebih singkat mendapat nilai lebih tinggi.</li>
 * </ul>
 * Normalisasi: benefit r = x / max(x); cost r = min(x) / x. Skor V = (w1*r1 + w2*r2 + w3*r3) / (w1 + w2 + w3).
 */
public final class SawCalculator {

    /** Hasil perhitungan satu truck. c = nilai asli, r = nilai ternormalisasi. */
    public record HasilSaw(Truck truck, double c1, double c2, double c3,
            double r1, double r2, double r3, double skor, int urutan) {
    }

    private SawCalculator() {
    }

    public static List<HasilSaw> hitung(List<Truck> trucks, List<Dock> docks, PengaturanSpk p) {
        int n = trucks.size();
        if (n == 0) {
            return new ArrayList<>();
        }
        double[] c1 = new double[n];
        double[] c2 = new double[n];
        double[] c3 = new double[n];
        double maks1 = 0;
        double maks2 = 0;
        double minDurasi = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            Truck t = trucks.get(i);
            c1[i] = p.nilaiMuatan(t.getJenisMuatan());
            double kapasitas = 0;
            for (Dock d : docks) {
                if (d.berfungsi() && d.cocokUntuk(t.getJenisMuatan())) {
                    kapasitas += d.getKapasitas();
                }
            }
            c2[i] = kapasitas;
            c3[i] = t.getEstimasiDurasiMenit();
            maks1 = Math.max(maks1, c1[i]);
            maks2 = Math.max(maks2, c2[i]);
            if (c3[i] > 0) {
                minDurasi = Math.min(minDurasi, c3[i]);
            }
        }
        double w1 = p.getBobotMuatan();
        double w2 = p.getBobotDock();
        double w3 = p.getBobotDurasi();
        double totalBobot = w1 + w2 + w3;
        if (totalBobot <= 0) {
            w1 = 1;
            w2 = 1;
            w3 = 1;
            totalBobot = 3;
        }

        List<double[]> baris = new ArrayList<>();
        List<Integer> indeks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double r1 = maks1 > 0 ? c1[i] / maks1 : 0;
            double r2 = maks2 > 0 ? c2[i] / maks2 : 0;
            double r3 = c3[i] > 0 && minDurasi != Double.MAX_VALUE ? minDurasi / c3[i] : 0;
            double skor = (w1 * r1 + w2 * r2 + w3 * r3) / totalBobot;
            baris.add(new double[] {r1, r2, r3, skor});
            indeks.add(i);
        }
        // Urut: skor tertinggi dulu; bila sama, yang lebih dulu tiba; bila sama lagi, yang lebih dulu terdaftar.
        indeks.sort(Comparator
                .<Integer>comparingDouble(i -> -Math.round(baris.get(i)[3] * 1_000_000d))
                .thenComparing(i -> trucks.get(i).getPerkiraanKedatangan())
                .thenComparingInt(i -> trucks.get(i).getIdTruck()));
        List<HasilSaw> hasil = new ArrayList<>();
        int urutan = 1;
        for (int i : indeks) {
            double[] b = baris.get(i);
            hasil.add(new HasilSaw(trucks.get(i), c1[i], c2[i], c3[i], b[0], b[1], b[2], b[3], urutan++));
        }
        return hasil;
    }
}
