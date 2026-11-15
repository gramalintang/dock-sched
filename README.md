# DOCK-SCHED
Sistem Penjadwalan Slot Loading-Unloading Truck Delivery di Gudang.

Aplikasi desktop **Java Swing + MySQL (JDBC)** dengan penjadwalan berbasis **SAW (Simple Additive Weighting)**.

## Teknologi
Apache NetBeans IDE 29 · Java JDK 25 · Java Swing · OOP · MySQL · JDBC (PreparedStatement, ResultSet) · JTable + DefaultTableModel · Maven

## Cara menjalankan (NetBeans 29)
1. Pasang JDK 25 dan MySQL Server 8.x, lalu jalankan MySQL.
2. NetBeans: **File > Open Project** > pilih folder `dock-sched` (berikon Maven).
3. Tunggu Maven mengunduh dependensi (mysql-connector-j) - perlu internet pertama kali.
4. Klik kanan proyek > **Properties > Build > Compile**, pastikan JDK 25 terpilih (Tools > Java Platforms).
5. Tekan **Run (F5)** atau F6. Database `dock_sched` dan data awal dibuat otomatis.
6. Bila MySQL `root` memakai password, di layar login klik **Pengaturan Koneksi**, isi host/port/user/password, simpan.
7. Pada database yang belum memiliki pengguna, aplikasi meminta pembuatan akun Programmer pertama. Buat username dan password sendiri; tidak ada akun/password bawaan.

Dari terminal: `mvn package` lalu `java -jar target/DOCK-SCHED.jar`.

## Database
Folder `database/` berisi:
- `dock_sched.sql` - struktur tabel + data awal dock dan bobot SAW (tanpa akun bawaan). Impor ke MySQL bila ingin membuat database manual.
- `contoh_data_truck.sql` - opsional, 6 truck contoh untuk demo (jalankan setelah `dock_sched.sql`).

Impor manual tidak wajib: aplikasi membuat database yang sama secara otomatis saat login pertama.
Perintah: `mysql -u root -p < database/dock_sched.sql`

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
`LoginFrame`, `MainFrame` (JFrame Form), `KoneksiDialog`, `GantiPasswordDialog`, `JadwalUlangDialog` (JDialog Form),
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
