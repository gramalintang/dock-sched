-- =====================================================================
-- DOCK-SCHED : skema basis data H2 tertanam (MODE=MySQL)
-- Tabel minimal sesuai SRS 2.3: Pengguna, Truck, Dock, Jadwal, LogStatus
-- Tabel tambahan: log_aktivitas (DS-NF-08) dan pengaturan (bobot SAW, DS-F-03.2)
-- File ini dijalankan otomatis oleh aplikasi saat pertama kali login.
-- =====================================================================

CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna   INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nama_lengkap  VARCHAR(100) NOT NULL,
    peran         ENUM('ADMIN_GUDANG','PETUGAS_DOCK','KEPALA_GUDANG') NOT NULL,
    aktif         TINYINT(1)   NOT NULL DEFAULT 1,
    dibuat_pada   DATETIME     NOT NULL,
    PRIMARY KEY (id_pengguna),
    UNIQUE KEY uq_pengguna_username (username)
);

CREATE TABLE IF NOT EXISTS dock (
    id_dock             INT         NOT NULL AUTO_INCREMENT,
    kode_dock           VARCHAR(20) NOT NULL,
    kapasitas           INT         NOT NULL,
    jenis_muatan        ENUM('BAHAN_BAKU_MASUK','BARANG_JADI_KELUAR') NOT NULL,
    dock_serbaguna      TINYINT(1)  NOT NULL DEFAULT 0,
    status_ketersediaan ENUM('TERSEDIA','TERISI','RUSAK') NOT NULL DEFAULT 'TERSEDIA',
    PRIMARY KEY (id_dock),
    UNIQUE KEY uq_dock_kode (kode_dock)
);

CREATE TABLE IF NOT EXISTS truck (
    id_truck                   INT          NOT NULL AUTO_INCREMENT,
    nomor_polisi               VARCHAR(15)  NOT NULL,
    nama_sopir                 VARCHAR(100) NOT NULL,
    jenis_muatan               ENUM('BAHAN_BAKU_MASUK','BARANG_JADI_KELUAR') NOT NULL,
    pemasok_pelanggan          VARCHAR(100) NOT NULL,
    perkiraan_waktu_kedatangan DATETIME     NOT NULL,
    estimasi_durasi_menit      INT          NOT NULL,
    status_proses              ENUM('MENUNGGU','PARKIR_DOCK','LOADING_UNLOADING','ADMINISTRASI','SELESAI_BERANGKAT') NOT NULL DEFAULT 'MENUNGGU',
    waktu_pendaftaran          DATETIME     NOT NULL,
    diarsipkan                 TINYINT(1)   NOT NULL DEFAULT 0,
    id_pengguna_input          INT          NULL,
    PRIMARY KEY (id_truck),
    KEY idx_truck_status (status_proses, diarsipkan),
    KEY idx_truck_plat (nomor_polisi),
    CONSTRAINT fk_truck_pengguna FOREIGN KEY (id_pengguna_input) REFERENCES pengguna (id_pengguna)
);

CREATE TABLE IF NOT EXISTS jadwal (
    id_jadwal              INT          NOT NULL AUTO_INCREMENT,
    id_truck               INT          NOT NULL,
    id_dock                INT          NOT NULL,
    skor_prioritas         DECIMAL(8,4) NOT NULL DEFAULT 0,
    urutan                 INT          NOT NULL DEFAULT 0,
    estimasi_waktu_masuk   DATETIME     NOT NULL,
    estimasi_waktu_selesai DATETIME     NOT NULL,
    waktu_keputusan        DATETIME     NOT NULL,
    jenis                  ENUM('OTOMATIS','MANUAL') NOT NULL DEFAULT 'OTOMATIS',
    status                 ENUM('AKTIF','DIGANTI','BATAL','SELESAI') NOT NULL DEFAULT 'AKTIF',
    id_pengguna            INT          NULL,
    keterangan             VARCHAR(255) NULL,
    PRIMARY KEY (id_jadwal),
    KEY idx_jadwal_truck (id_truck, status),
    KEY idx_jadwal_dock (id_dock, status),
    CONSTRAINT fk_jadwal_truck    FOREIGN KEY (id_truck)    REFERENCES truck (id_truck),
    CONSTRAINT fk_jadwal_dock     FOREIGN KEY (id_dock)     REFERENCES dock (id_dock),
    CONSTRAINT fk_jadwal_pengguna FOREIGN KEY (id_pengguna) REFERENCES pengguna (id_pengguna)
);

CREATE TABLE IF NOT EXISTS log_status (
    id_log             INT        NOT NULL AUTO_INCREMENT,
    id_truck           INT        NOT NULL,
    id_pengguna        INT        NULL,
    status_proses      ENUM('MENUNGGU','PARKIR_DOCK','LOADING_UNLOADING','ADMINISTRASI','SELESAI_BERANGKAT') NOT NULL,
    waktu              DATETIME   NOT NULL,
    durasi_tahap_detik INT        NULL,
    PRIMARY KEY (id_log),
    KEY idx_log_truck (id_truck, waktu),
    KEY idx_log_waktu (waktu),
    CONSTRAINT fk_log_truck    FOREIGN KEY (id_truck)    REFERENCES truck (id_truck),
    CONSTRAINT fk_log_pengguna FOREIGN KEY (id_pengguna) REFERENCES pengguna (id_pengguna)
);

CREATE TABLE IF NOT EXISTS log_aktivitas (
    id_log_aktivitas INT          NOT NULL AUTO_INCREMENT,
    id_pengguna      INT          NULL,
    aktivitas        VARCHAR(40)  NOT NULL,
    detail           VARCHAR(255) NULL,
    waktu            DATETIME     NOT NULL,
    PRIMARY KEY (id_log_aktivitas),
    KEY idx_aktivitas_waktu (waktu)
);

CREATE TABLE IF NOT EXISTS pengaturan (
    kunci VARCHAR(50)  NOT NULL,
    nilai VARCHAR(100) NOT NULL,
    PRIMARY KEY (kunci)
);

-- Nilai bawaan pengaturan SPK (dapat diubah Admin Gudang dari menu Master Data & Pengaturan)
INSERT IGNORE INTO pengaturan (kunci, nilai) VALUES
    ('bobot_muatan', '50'),
    ('bobot_dock', '30'),
    ('bobot_durasi', '20'),
    ('nilai_bahan_baku', '5'),
    ('nilai_barang_jadi', '3');
