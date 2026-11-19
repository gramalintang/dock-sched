# DOCK-SCHED
Sistem Penjadwalan Slot Loading-Unloading Truck Delivery di Gudang.

Aplikasi desktop **Java Swing + H2 tertanam (JDBC, mode MySQL)** dengan penjadwalan berbasis **SAW (Simple Additive Weighting)**.

## Teknologi
Apache NetBeans IDE 29 · Java JDK 25 · Java Swing · OOP · H2 Database (mode MySQL) · JDBC (PreparedStatement, ResultSet) · JTable + DefaultTableModel · Maven

## Unduh & jalankan (tanpa instalasi)
Buka halaman **Releases** di GitHub, unduh sesuai OS. Java dan database sudah ikut di dalam paket; data tersimpan di folder `~/.docksched` komputer masing-masing.
- **Windows**: unduh `DOCK-SCHED-<versi>-windows.zip`, ekstrak, buka `DOCK-SCHED\DOCK-SCHED.exe`.
  Bila muncul "Windows protected your PC", klik **More info > Run anyway** (sekali saja).
- **macOS (Apple Silicon)**: unduh `DOCK-SCHED-<versi>-macos.dmg`, seret DOCK-SCHED ke Applications, buka.
  Bila diblokir, buka **System Settings > Privacy & Security > Open Anyway** (sekali saja).

### Untuk pengelola rilis
Buat tag: `git tag v1.1 && git push origin v1.1`. Workflow membuat paket Windows + macOS dan memasangnya di Releases.

## Cara menjalankan (NetBeans 29)
1. Pasang JDK 25 (tidak perlu server database).
2. NetBeans: **File > Open Project** > pilih folder `dock-sched` (berikon Maven).
3. Tunggu Maven mengunduh dependensi (H2) - perlu internet pertama kali.
4. Klik kanan proyek > **Properties > Build > Compile**, pastikan JDK 25 terpilih (Tools > Java Platforms).
5. Tekan **Run (F5)** atau F6. Database `dock_sched` dan data awal dibuat otomatis di `~/.docksched/data`.
6. Pada database yang belum memiliki pengguna, aplikasi meminta pembuatan akun Programmer pertama. Buat username dan password sendiri; tidak ada akun/password bawaan.

Dari terminal: `mvn package` lalu `java -jar target/DOCK-SCHED.jar`.

## Database
Aplikasi memakai **H2 tertanam** dalam mode kompatibel MySQL, jadi tidak perlu memasang server database.
Struktur tabel ada di `src/main/resources/database/schema.sql` dan dibuat otomatis saat pertama kali dibuka.
Data disimpan di `~/.docksched/data`, cadangan harian di `~/.docksched/backup`.
`database/dock_sched.sql` tetap tersedia sebagai referensi skema versi MySQL.
Satu berkas database hanya bisa dibuka oleh satu jendela aplikasi sekaligus.

## Pengaturan akun dan keamanan
- Tidak ada username/password bawaan pada source code, README, maupun SQL.
- Pada database baru, buat akun Programmer pertama melalui dialog setup saat aplikasi dibuka. Akun Programmer memiliki akses penuh.
- Programmer membuat akun Admin Gudang, Petugas Dock, dan Kepala Gudang melalui menu Master Data.
- Password disimpan menggunakan PBKDF2-HMAC-SHA256.
- Setelah 5 percobaan login gagal dalam 15 menit, username tersebut dikunci sementara selama 15 menit.
- Identitas Programmer dicatat pada tabel `pengaturan` yang sudah ada; struktur tabel dan relasi database tidak ditambah/diubah.
- **Penting untuk database lama:** akun yang sebelumnya sudah tersimpan tidak dihapus otomatis agar data dan relasi tetap aman. Sebelum digunakan, nonaktifkan atau ubah password akun lama melalui pengelolaan akun setelah login dengan otoritas yang sah.

## GUI Builder
Semua layar dibuat dengan **NetBeans GUI Builder** (pasangan `.java` + `.form` di `src/main/java/com/docksched/view`):
`LoginFrame`, `MainFrame` (JFrame Form), `GantiPasswordDialog`, `JadwalUlangDialog` (JDialog Form),
`FormTruck`, `InputTruckPanel`, `CariTruckPanel`, `PenjadwalanPanel`, `UpdateStatusPanel`, `DashboardLaporanPanel`,
`MasterDataPanel` (JPanel Form). Buka file `.java`-nya di NetBeans lalu klik tab **Design** untuk mengedit secara visual.

## Fitur
- Input, cari, sunting data truck (validasi plat, anti-duplikat)
- Penjadwalan slot otomatis SAW (bobot dapat diatur), tanpa bentrok, dock sesuai jenis muatan
- Jadwal ulang manual & pembatalan oleh Admin
- Update status: Menunggu > Parkir Dock > Loading/Unloading > Administrasi > Selesai (hanya Petugas Dock, tidak boleh lompat)
- Dashboard real-time, baris merah bila melebihi target 45 menit
- Laporan performa + ekspor PDF/Excel
- Master data dock & pengguna, backup harian, log aktivitas, logout otomatis 30 menit

## Struktur
`config` (koneksi, init DB) · `model` · `controller` (logika & JDBC) · `view` (Swing) · `util`

## Kolaborasi Git
```
git init
git add .
git commit -m "Initial commit DOCK-SCHED"
git branch -M main
git remote add origin https://github.com/<username>/<repo>.git
git push -u origin main
```
