package com.docksched.controller;

import com.docksched.model.Dock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mengalokasikan dock dan menghitung estimasi jadwal masuk untuk truck yang sudah diurutkan SAW
 * (DS-F-03.3, DS-F-03.4). Hanya dock yang sesuai jenis muatan (DS-NF-13) dan tidak rusak yang dipertimbangkan,
 * dan slot tidak boleh bertabrakan dengan truck lain pada dock yang sama.
 */
public final class SlotAllocator {

    /** Rentang waktu dock sedang/akan dipakai. */
    public record Interval(LocalDateTime mulai, LocalDateTime selesai) {
    }

    /** Hasil alokasi satu truck. {@code dock} null bila tidak ada dock yang sesuai. */
    public record Alokasi(SawCalculator.HasilSaw saw, Dock dock, LocalDateTime masuk,
            LocalDateTime selesai, String catatan) {
    }

    private SlotAllocator() {
    }

    /**
     * @param urut     truck terurut hasil SAW
     * @param docks    seluruh dock
     * @param sibuk    rentang sibuk awal per id dock (truck yang sedang diproses, jadwal manual); tidak diubah
     * @param sekarang waktu acuan; truck tidak dijadwalkan sebelum waktu ini
     */
    public static List<Alokasi> alokasikan(List<SawCalculator.HasilSaw> urut, List<Dock> docks,
            Map<Integer, List<Interval>> sibuk, LocalDateTime sekarang) {
        Map<Integer, List<Interval>> kerja = new HashMap<>();
        for (Dock d : docks) {
            List<Interval> salinan = new ArrayList<>(sibuk.getOrDefault(d.getIdDock(), new ArrayList<>()));
            salinan.sort(Comparator.comparing(Interval::mulai));
            kerja.put(d.getIdDock(), salinan);
        }
        List<Alokasi> hasil = new ArrayList<>();
        for (SawCalculator.HasilSaw h : urut) {
            Duration durasi = Duration.ofMinutes(h.truck().getEstimasiDurasiMenit());
            LocalDateTime dasar = h.truck().getPerkiraanKedatangan().isAfter(sekarang)
                    ? h.truck().getPerkiraanKedatangan() : sekarang;
            Dock terbaik = null;
            LocalDateTime mulaiTerbaik = null;
            for (Dock d : docks) {
                if (!d.berfungsi() || !d.cocokUntuk(h.truck().getJenisMuatan())) {
                    continue;
                }
                LocalDateTime mulai = cariMulai(kerja.get(d.getIdDock()), dasar, durasi);
                if (terbaik == null || lebihBaik(d, mulai, terbaik, mulaiTerbaik)) {
                    terbaik = d;
                    mulaiTerbaik = mulai;
                }
            }
            if (terbaik == null) {
                hasil.add(new Alokasi(h, null, null, null,
                        "Tidak ada dock " + h.truck().getJenisMuatan().getLabel() + " yang berfungsi"));
                continue;
            }
            LocalDateTime selesai = mulaiTerbaik.plus(durasi);
            List<Interval> daftar = kerja.get(terbaik.getIdDock());
            daftar.add(new Interval(mulaiTerbaik, selesai));
            daftar.sort(Comparator.comparing(Interval::mulai));
            hasil.add(new Alokasi(h, terbaik, mulaiTerbaik, selesai, ""));
        }
        return hasil;
    }

    /** Waktu paling awal (>= dasar) saat slot sepanjang {@code durasi} tidak bertabrakan dengan rentang sibuk. */
    static LocalDateTime cariMulai(List<Interval> sibuk, LocalDateTime dasar, Duration durasi) {
        LocalDateTime t = dasar;
        boolean geser = true;
        while (geser) {
            geser = false;
            for (Interval iv : sibuk) {
                if (t.isBefore(iv.selesai()) && t.plus(durasi).isAfter(iv.mulai())) {
                    t = iv.selesai();
                    geser = true;
                }
            }
        }
        return t;
    }

    /** Pilih mulai paling awal; bila sama, utamakan dock khusus (bukan serbaguna), lalu kode dock. */
    private static boolean lebihBaik(Dock kandidat, LocalDateTime mulai, Dock terbaik, LocalDateTime mulaiTerbaik) {
        int banding = mulai.compareTo(mulaiTerbaik);
        if (banding != 0) {
            return banding < 0;
        }
        if (kandidat.isSerbaguna() != terbaik.isSerbaguna()) {
            return !kandidat.isSerbaguna();
        }
        return kandidat.getKodeDock().compareTo(terbaik.getKodeDock()) < 0;
    }
}
