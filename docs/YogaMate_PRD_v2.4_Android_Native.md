# PRD — YogaMate

> **Product Requirements Document**
>
> **Version:** 2.4  
> **Status:** Revised Draft — Android Native Architecture Selected  
> **Target platform:** Android  
> **Target completion:** 24 November 2026  
> **Team:** 3 people  
> **Courses:** Computer Vision dan Pemrosesan Citra  
> **Primary dataset:** Yoga-82

> **Revision note (v2.4):** Arsitektur mobile pada PRD v2.3 yang menggunakan React Native + Expo diganti menjadi **Android Native menggunakan Kotlin dan Jetpack Compose**. Integrasi kamera ditetapkan menggunakan CameraX, pose estimation menggunakan MediaPipe Tasks Vision Android, dan inference model GCN menggunakan ONNX Runtime Android. Alur produk, dataset Yoga-82, pendekatan GNN/GCN, backend Go + PostgreSQL, serta aturan Total Yoga Accuracy pada v2.3 dipertahankan. Detail performa, kompatibilitas operator ONNX, dan konfigurasi perangkat tetap harus divalidasi melalui proof of concept pada perangkat Android.

---

## 1. Product Overview

### 1.1 Product Name

**YogaMate**

### 1.2 Product Description

YogaMate adalah aplikasi mobile Android untuk membantu pengguna berlatih yoga secara mandiri di rumah. Aplikasi menyediakan sesi latihan berdasarkan tingkat kesulitan, memilih pose secara acak melalui fitur **Gacha Gerakan**, menampilkan preview gerakan, menjalankan timer latihan, dan menggunakan kamera smartphone untuk mengenali pose yang sedang dilakukan.

Inti computer vision pada YogaMate menggunakan **MediaPipe Pose Landmarker** untuk memperoleh 33 landmark tubuh dari gambar atau kamera secara real-time. Landmark direpresentasikan sebagai graf kerangka tubuh: landmark menjadi node, sedangkan koneksi antartitik mengikuti struktur skeleton MediaPipe. Graf tersebut diproses oleh model **Graph Neural Network (GNN)** yang dilatih menggunakan gambar dari dataset **Yoga-82** untuk mengklasifikasikan pose yoga. Implementasi dan kelayakan ekstraksi landmark pada gambar Yoga-82 harus divalidasi sebelum target seluruh 82 kelas dikunci untuk eksperimen.

Yoga-82 memiliki **82 kelas pose** dengan struktur label hierarkis tiga tingkat. YogaMate menargetkan seluruh 82 pose tersebut, tetapi pose akan dikelompokkan ke dalam beberapa tingkat kesulitan agar pengguna dapat berlatih secara bertahap. [1]

### 1.3 Core Value

YogaMate berfungsi sebagai **asisten latihan yoga berbasis computer vision**. Produk ditujukan untuk latihan dan edukasi, bukan diagnosis medis atau pengganti instruktur profesional.

Nilai utama produk:

1. Pengguna dapat memilih kategori kesulitan sebelum latihan.
2. Pose latihan dipilih dari pool pose berdasarkan level melalui Gacha Gerakan.
3. Pengguna melihat contoh/preview pose sebelum memulai.
4. Pengguna melakukan pose di depan kamera secara real-time.
5. Sistem menampilkan skeleton tubuh di atas kamera.
6. Sistem mengenali apakah pose yang dilakukan sesuai dengan pose target.
7. Sistem menyaring prediksi dengan probabilitas rendah agar tidak langsung dianggap sebagai pose yang valid.
8. Instruksi pose diputar satu kali sebagai panduan awal, bukan sebagai feedback koreksi berulang.
9. Setelah timer selesai, pengguna memperoleh **Total Yoga Accuracy** sebagai hasil sesi.
10. Gamifikasi berfokus pada **achievement**, bukan pemberian skor/XP sebagai mekanisme utama gamifikasi.

---

## 2. Problem Statement

### 2.1 Problem

Pengguna yang berlatih yoga sendiri di rumah dapat mengalami kesulitan memahami nama pose dan memastikan bahwa pose yang sedang dilakukan sesuai dengan pose latihan yang dipilih. Ketika tidak ada instruktur, pengguna memerlukan panduan visual yang sederhana serta indikator apakah pose yang dilakukan berhasil dikenali oleh sistem.

### 2.2 Problem Statement

> **Bagaimana menyediakan aplikasi mobile yang membantu pengguna berlatih yoga secara mandiri melalui pemilihan pose berdasarkan level, panduan gerakan, deteksi pose secara real-time, dan pengukuran kesesuaian pose tanpa memerlukan koneksi internet untuk proses computer vision?**

### 2.3 Product Positioning

YogaMate diposisikan sebagai **self-guided yoga practice application** dengan computer vision on-device.

Produk tidak melakukan:

- diagnosis cedera;
- penilaian kesehatan medis;
- rekomendasi terapi/rehabilitasi;
- klaim bahwa sebuah pose aman untuk semua kondisi pengguna;
- koreksi biomekanik klinis.

---

## 3. Product Goals

### 3.1 Product Goals

1. Menyediakan latihan yoga berbasis beberapa tingkat kesulitan.
2. Menyediakan target 82 pose Yoga-82 secara bertahap.
3. Memungkinkan pengguna memperoleh pose latihan melalui Gacha Gerakan berdasarkan level.
4. Menampilkan preview sebelum latihan dimulai.
5. Menjalankan latihan menggunakan kamera real-time.
6. Menampilkan skeleton/landmark tubuh.
7. Mengenali pose yang dilakukan menggunakan model machine learning.
8. Mengabaikan prediksi dengan probabilitas rendah dari proses pose matching.
9. Menampilkan Total Yoga Accuracy setelah timer selesai.
10. Menyimpan riwayat latihan.
11. Menyediakan achievement dengan progress bar.
12. Menyediakan edukasi kesehatan/yoga dasar.
13. Menjalankan inference computer vision secara offline/on-device.

### 3.2 Computer Vision Goals

1. Mengambil frame kamera secara real-time.
2. Melakukan preprocessing citra yang relevan terhadap pipeline.
3. Menggunakan MediaPipe Pose untuk pose landmark extraction.
4. Mengubah 33 landmark menjadi graf skeleton dengan node feature dan adjacency matrix yang konsisten.
5. Melatih model GNN menggunakan gambar Yoga-82 yang berhasil diekstraksi landmark-nya.
6. Menerapkan klasifikasi flat ke 82 kelas sebagai baseline.
7. Menghasilkan skor/logit dan probabilitas kelas pose.
8. Menerapkan threshold untuk menyaring prediksi GNN dengan probabilitas rendah.
9. Menguji model pada data test Yoga-82 dan data kamera nyata.
10. Menjalankan inference model di perangkat Android.

### 3.3 Gamification Goals

Gamifikasi YogaMate berorientasi pada:

- penyelesaian sesi;
- eksplorasi pose;
- konsistensi latihan;
- penyelesaian level;
- jumlah pose yang telah dipelajari.

Gamifikasi **tidak menggunakan XP sebagai requirement MVP**.

---

## 4. Product Scope

### 4.1 MVP Scope

MVP mencakup:

- Register/Login;
- Home;
- Profile;
- Achievement;
- Health Education;
- pemilihan level/kategori kesulitan;
- Gacha Gerakan;
- preview gerakan;
- pemilihan durasi;
- real-time camera;
- full-body detection;
- skeleton overlay;
- pose recognition;
- filtering prediksi probabilitas rendah;
- voice instruction satu kali pada awal pose;
- timer;
- Total Yoga Accuracy;
- session result;
- history latihan;
- achievement progress.

### 4.2 Out of Scope MVP

- multi-person pose recognition;
- body-part error detection;
- feedback koreksi suara secara berulang;
- chatbot/AI instructor;
- diagnosis medis;
- cloud inference per frame;
- social feed;
- kompetisi online/leaderboard;
- kamera multi-angle;
- wearable integration;
- kalori, detak jantung, atau pengukuran fisiologis lain.

---

## 5. User Flow — Primary Flow

> **IMPORTANT BEHAVIOR:** Pengguna harus mengikuti alur utama yang telah ditetapkan pada konsep produk. Fitur tambahan tidak boleh mengubah urutan inti tersebut pada MVP.

### 5.1 Main Application Flow

```text
Buka Aplikasi
    ↓
Login / Register
    ↓
Home
    ↓
Navbar:
- Beranda
- Profile
- Tombol Olahraga
- Achievement + Progress Bar
- Edukasi Kesehatan
```

### 5.2 Workout Flow

```text
Tombol Olahraga
    ↓
Kategori Kesulitan / Level
    ↓
Gacha Gerakan
    ↓
Preview Gerakan
    ↓
Pilih Durasi
    ↓
Melakukan Yoga
    ↓
Timer Selesai
    ↓
Hasil / Total Yoga Accuracy
    ↓
Simpan Riwayat + Update Achievement
```

### 5.3 Yoga Session Flow

```text
Pose dipilih melalui Gacha
        ↓
Preview Pose
        ↓
Start
        ↓
Voice Instruction diputar sekali
        ↓
Kamera aktif
        ↓
MediaPipe Pose
        ↓
Landmark tubuh
        ↓
Feature Extraction
        ↓
ML Classifier
        ↓
Pose cocok / prediksi tidak cukup meyakinkan / pose lain
        ↓
Timer berjalan
        ↓
Timer selesai
        ↓
Total Yoga Accuracy
```

---

## 6. User Journey

### Stage 1 — Entry

```text
Buka YogaMate
    ↓
Login / Register
    ↓
Masuk Home
```

### Stage 2 — Home Navigation

Pengguna melihat:

- tombol olahraga;
- progress achievement;
- profile;
- edukasi kesehatan.

### Stage 3 — Start Yoga

```text
Klik Tombol Olahraga
    ↓
Pilih Kategori Kesulitan
    ↓
Gacha Gerakan
```

Gacha tidak berfungsi sebagai sistem monetisasi atau loot box berbayar. Gacha merupakan mekanisme random selection untuk menentukan pose latihan dari pool level yang dipilih.

### Stage 4 — Preview

Pengguna melihat:

- nama pose;
- gambar referensi;
- deskripsi singkat;
- instruksi dasar;
- durasi yang akan dipilih setelah preview.

### Stage 5 — Yoga Session

```text
Start Session
    ↓
Voice Instruction diputar satu kali
    ↓
Kamera aktif
    ↓
User melakukan pose
```

Voice instruction hanya berfungsi memberikan **instruksi bagaimana melakukan pose**. Voice tidak digunakan untuk mengoreksi pengguna secara berulang selama timer.

### Stage 6 — Result

Setelah timer selesai, aplikasi menampilkan:

- nama pose;
- durasi;
- Total Yoga Accuracy;
- status penyelesaian;
- achievement yang baru terbuka, jika ada.

---

## 7. User Personas

### 7.1 Primary User

Orang yang berlatih yoga sendiri di rumah.

### 7.2 Needs

Pengguna membutuhkan:

- panduan pose yang mudah diikuti;
- contoh visual sebelum latihan;
- pengenalan pose melalui kamera;
- indikator apakah pose target berhasil dikenali;
- latihan yang dapat dilakukan tanpa internet setelah aplikasi dan model tersedia;
- riwayat latihan;
- motivasi melalui achievement.

---

## 8. Information Architecture

```text
YogaMate
│
├── Authentication
│   ├── Register
│   ├── Login
│   └── Logout
│
├── Beranda
│   ├── Start Workout
│   ├── Achievement Progress
│   └── Recent History
│
├── Tombol Olahraga
│   ├── Difficulty Selection
│   ├── Gacha Gerakan
│   ├── Pose Preview
│   ├── Duration Selection
│   ├── Yoga Session
│   └── Result
│
├── Achievement
│   ├── Achievement List
│   └── Progress Bar
│
├── Edukasi Kesehatan
│   ├── Dasar Yoga
│   ├── Tips Latihan
│   ├── Pemanasan
│   └── Safety Notes
│
└── Profile
    ├── Account
    ├── Personal Profile
    └── Practice History
```

---

## 9. Functional Requirements

## 9.1 Authentication

### Requirements

- User dapat register.
- User dapat login.
- User dapat logout.
- Session authentication harus dipertahankan secara aman.
- Data history hanya dapat diakses oleh user yang sesuai.

### Acceptance Criteria

- Register dengan data valid berhasil.
- Login dengan kredensial benar berhasil.
- Login dengan kredensial salah ditolak.
- User yang logout tidak dapat mengakses endpoint yang membutuhkan autentikasi.

---

## 9.2 Home / Beranda

### Requirements

Home minimal menampilkan:

- greeting/personalized header;
- tombol olahraga;
- ringkasan achievement progress;
- history terbaru;
- shortcut edukasi kesehatan.

---

## 9.3 Profile

Profile minimal dapat menyimpan:

- nama;
- email/username;
- foto profil, jika diperlukan;
- tinggi badan, opsional;
- berat badan, opsional;
- gender, opsional;
- tujuan latihan, misalnya fitness, weight management, atau flexibility.

Data profile tersebut **tidak digunakan sebagai input model computer vision** pada MVP.

---

## 9.4 Difficulty / Level Selection

Yoga-82 dibagi ke beberapa level kesulitan. Untuk MVP, baseline taxonomy:

1. **Beginner**
2. **Intermediate**
3. **Advanced**

Pembagian final 82 pose ke level tersebut harus ditetapkan sebagai **content taxonomy project**, karena Yoga-82 menyediakan label pose dan hierarki kategorinya, tetapi bukan label tingkat kesulitan resmi untuk aplikasi YogaMate. [1]

### Requirements

- User dapat memilih level yang tersedia.
- Gacha hanya mengambil pose dari pool level yang dipilih.
- Pose yang belum termasuk dalam pool level tidak boleh muncul.
- Mapping 82 pose → level disimpan sebagai data konfigurasi.

---

## 9.5 Gacha Gerakan

### Purpose

Gacha memilih pose secara acak dari level yang dipilih.

### Requirements

- Satu gacha menghasilkan satu target pose untuk satu sesi pose.
- Gacha tidak menggunakan uang atau transaksi.
- Hasil gacha menampilkan nama pose.
- Hasil gacha mengarah ke halaman preview.
- Sistem menyimpan pose target untuk sesi tersebut.

### Optional Future Feature

- reroll terbatas;
- daily random pose;
- pose collection.

Fitur tersebut bukan requirement MVP.

---

## 9.6 Pose Preview

Preview harus menampilkan:

- nama pose;
- gambar referensi;
- deskripsi singkat;
- instruksi dasar;
- tombol lanjut ke pemilihan durasi.

### Acceptance Criteria

User memahami pose target sebelum kamera dimulai.

---

## 9.7 Duration Selection

User memilih durasi latihan.

Baseline pilihan:

- 1 menit;
- 2 menit;
- 3 menit;
- 5 menit.

Pilihan final dapat disesuaikan berdasarkan hasil usability testing.

Timer dimulai setelah user menekan Start.

---

## 9.8 Real-Time Camera

### Requirements

- Menggunakan kamera smartphone secara real-time melalui **CameraX**.
- Menggunakan `Preview` untuk menampilkan tampilan kamera dan `ImageAnalysis` untuk menyediakan frame bagi pipeline computer vision.
- Meminta izin `android.permission.CAMERA` pada saat fitur kamera akan digunakan; aplikasi harus menangani izin ditolak, ditolak permanen, atau tidak tersedia.
- User diarahkan untuk menempatkan seluruh tubuh dalam frame.
- Kamera menampilkan skeleton/landmark tubuh melalui overlay yang mengikuti koordinat preview kamera.
- Analisis frame berjalan di luar main/UI thread.
- Pipeline kamera menggunakan strategi pengendalian antrean frame yang tidak menyebabkan backlog; baseline yang direkomendasikan adalah `ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST`.
- Setiap `ImageProxy` harus ditutup setelah frame selesai digunakan/dikonversi, termasuk ketika terjadi error.
- Computer vision berjalan tanpa mengirim frame kamera ke backend.
- Kamera dan analyzer dihentikan ketika sesi selesai, layar latihan ditinggalkan, atau lifecycle aplikasi tidak lagi mengizinkan kamera aktif.
- Sistem memberi status ketika tubuh tidak terdeteksi dengan cukup baik.

### Camera States

```text
CAMERA_LOADING
NO_PERSON
BODY_INCOMPLETE
POSE_DETECTING
POSE_MATCHED
POSE_NOT_MATCHED
ANALYSIS_UNAVAILABLE
SESSION_COMPLETED
```

---

## 9.9 Body Landmark Detection

YogaMate menggunakan **MediaPipe Pose Landmarker melalui MediaPipe Tasks Vision Android SDK** sebagai pose estimation layer.

Implementasi Android menggunakan dependency Gradle `com.google.mediapipe:tasks-vision` dan model task `.task` yang disimpan sebagai asset aplikasi. Untuk input kamera real-time, baseline menggunakan `RunningMode.LIVE_STREAM` dengan callback hasil asinkron. MediaPipe Pose Landmarker menyediakan **33 pose landmarks** per orang, koordinat gambar ternormalisasi, koordinat world 3D, serta informasi `visibility` dan `presence` jika tersedia. Parameter `numPoses` tersedia, tetapi MVP tetap memproses satu orang. [2][3]

MediaPipe dipanggil dari lapisan data/engine computer vision Kotlin, bukan langsung dari composable UI. Landmark digunakan untuk membangun graf dan node feature untuk klasifikasi, bukan langsung disimpan sebagai data history user.

---

## 9.10 Pose Classification

### Target

Mengenali pose dari graf kerangka tubuh yang dibentuk dari 33 landmark MediaPipe Pose Landmarker dan menentukan apakah hasil klasifikasi sesuai dengan pose target dari Gacha.

### Architecture

```text
Camera Frame / Yoga-82 Image
        ↓
MediaPipe Pose Landmarker
        ↓
33 Body Landmarks
        ↓
Graph Construction
(nodes + skeleton edges)
        ↓
Node Feature Normalization
        ↓
Graph Neural Network (GCN baseline)
        ↓
82-Class Logits / Probabilities
        ↓
Target Pose Matching
```

### Graph Representation

Setiap gambar direpresentasikan sebagai graf pose \(G=(V,E,X)\):

- **Nodes (V):** 33 landmark tubuh dengan urutan indeks yang konsisten sesuai MediaPipe Pose Landmarker.
- **Edges (E):** koneksi ant landmark berdasarkan koneksi skeleton MediaPipe. Untuk baseline GCN, koneksi dibuat dua arah dan self-loop ditambahkan saat membangun adjacency matrix.
- **Node features (X):** baseline berupa koordinat landmark \(x,y,z\) dan informasi visibility/presence jika tersedia. Koordinat dapat dinormalisasi terhadap posisi dan skala tubuh untuk mengurangi pengaruh posisi tubuh di dalam frame.
- **Graph topology:** menggunakan struktur skeleton yang sama untuk seluruh sampel. Fitur edge tambahan belum menjadi requirement MVP.

Skema fitur akhir harus ditetapkan setelah pilot ekstraksi landmark dan dipakai secara identik pada training, validation, testing, serta inference di Android.

### Recognition Behavior

GNN menghasilkan skor untuk 82 kelas pose. Softmax digunakan untuk memperoleh probabilitas kelas apabila diperlukan oleh proses filtering.

Contoh prediksi yang diterima:

```text
Predicted Pose:
Tree Pose

Probability:
0.93

Status:
Accepted
```

Contoh prediksi yang tidak diterima:

```text
Predicted Pose:
Tree Pose

Probability:
0.41

Status:
Not Accepted
```

Prediksi yang tidak melewati threshold tidak dianggap sebagai pose valid untuk pose matching. Tidak ada kelas ML tambahan bernama `Unknown`.

### Target Labels

Target produk tetap **82 pose Yoga-82**. Namun, kelayakan klasifikasi 82 kelas bergantung pada jumlah dan kualitas gambar yang berhasil diekstraksi landmark-nya. Audit per kelas harus dilakukan sebelum eksperimen final; kelas dengan kegagalan ekstraksi yang tinggi perlu dianalisis dan dilaporkan, bukan diam-diam dihapus dari dataset.

## 9.11 Machine Learning Model

### Selected Model

Model deep learning baseline yang direncanakan untuk MVP:

> **Graph Convolutional Network (GCN), sebagai salah satu arsitektur Graph Neural Network (GNN)**

GNN dipilih karena input utama YogaMate bukan citra RGB secara langsung, melainkan graf kerangka tubuh yang terdiri atas landmark dan hubungan ant landmark. GCN dapat mempelajari representasi node dengan menggabungkan informasi dari node-node yang terhubung pada struktur skeleton. Pemilihan GCN merupakan keputusan desain awal, bukan klaim bahwa GCN pasti lebih akurat daripada model lain. [4]

### Baseline Architecture

```text
Input Graph
33 nodes × F node features
        ↓
Fixed Skeleton Adjacency + Self-Loops
        ↓
Graph Convolution Layer 1 + ReLU
        ↓
Graph Convolution Layer 2 + ReLU
        ↓
Global Mean Pooling
        ↓
Fully Connected Layer
        ↓
82-Class Logits
        ↓
Softmax (for class probabilities)
```

Jumlah layer, ukuran hidden dimension, dropout, dan optimizer akan ditentukan melalui eksperimen pada training/validation set. Baseline awal menggunakan dua graph convolution layers agar model tetap sederhana untuk tim beranggotakan tiga orang dan kebutuhan inference di perangkat.

### Graph Convolution Implementation

Untuk menjaga representasi graf konsisten dan mempermudah ekspor ONNX, baseline menggunakan adjacency matrix skeleton berukuran tetap. Secara konseptual, setiap layer mengagregasikan fitur tetangga menggunakan adjacency matrix yang telah dinormalisasi, lalu menerapkan transformasi bobot dan fungsi aktivasi. Implementasi awal sebaiknya menggunakan operasi tensor standar yang dapat diekspor, bukan operasi sparse/dynamic graph yang belum diuji kompatibilitasnya dengan runtime Android.

### Training

Training dilakukan dengan **Python dan PyTorch**. Setiap gambar Yoga-82 yang berhasil diproses menjadi satu graf berlabel kelas pose. Model dilatih sebagai graph-level multiclass classifier menggunakan label Yoga-82. Baseline loss adalah cross-entropy; optimizer dan hyperparameter final ditentukan menggunakan validation set.

### Classification Strategy

MVP menggunakan satu classifier GCN yang memetakan satu graf pose langsung ke salah satu dari 82 kelas:

```text
33 MediaPipe Landmarks
        ↓
Graph + Node Features
        ↓
1 GCN Graph Classifier
        ↓
82 Pose Classes
```

Hierarchical classification berdasarkan tiga tingkat label Yoga-82 bukan requirement MVP. Jika klasifikasi flat sulit mencapai hasil yang layak, pemanfaatan label hierarkis dapat dipertimbangkan sebagai eksperimen lanjutan setelah baseline dievaluasi.

### Probability Threshold

Output logits dikonversi menjadi probabilitas kelas untuk menyaring prediksi dengan confidence rendah. Threshold ditentukan menggunakan validation set dan tidak boleh dituning menggunakan test set. Probabilitas softmax bukan jaminan bahwa model terkalibrasi secara sempurna; threshold perlu dievaluasi bersama precision, recall, dan tingkat prediksi yang ditolak.

```text
GCN
 ↓
82-Class Logits
 ↓
Softmax Probabilities
 ↓
Maximum Probability >= threshold?
      ├── Yes → prediction accepted
      └── No  → prediction ignored for pose matching
```

### Model Export & Deployment

Model PyTorch direncanakan diekspor ke **ONNX** menggunakan `torch.onnx.export`. Model ONNX kemudian dijalankan langsung pada Android melalui dependency **ONNX Runtime Android** (`com.microsoft.onnxruntime:onnxruntime-android`) dan Java API yang dapat dipanggil dari Kotlin. Dukungan ekspor harus dibuktikan dengan model GCN aktual, karena keberhasilan ekspor bergantung pada operasi tensor yang digunakan dan operator yang tersedia pada runtime Android. [5][6][7]

```text
Python / PyTorch
        ↓
GCN Training
        ↓
PyTorch ONNX Export
        ↓
GCN Model (.onnx)
        ↓
ONNX Runtime Android (Kotlin API)
        ↓
Android Device
```

Sebelum integrasi penuh, lakukan prototype ekspor ONNX dan bandingkan output PyTorch dengan ONNX Runtime Android pada input graf yang sama. Inference GNN tidak dilakukan oleh backend Go. Baseline inference menggunakan CPU execution provider terlebih dahulu; opsi akselerasi seperti NNAPI atau XNNPACK hanya digunakan setelah kompatibilitas operator, stabilitas, dan peningkatan performanya terukur.

## 9.12 Feature Extraction & Graph Construction

### Node Features

Baseline node feature untuk setiap landmark:

- koordinat `x`;
- koordinat `y`;
- koordinat `z`;
- visibility/presence jika tersedia dari MediaPipe.

Koordinat dan strategi normalisasi harus konsisten. Normalisasi terhadap titik referensi tubuh dan skala tubuh dapat diuji untuk mengurangi pengaruh jarak serta posisi pengguna dalam frame. Feature schema final ditentukan melalui pilot experiment dan disimpan sebagai spesifikasi bersama.

### Graph Edges

- Menggunakan koneksi skeleton MediaPipe Pose Landmarker yang telah ditetapkan.
- Setiap koneksi diperlakukan sebagai edge dua arah untuk baseline GCN.
- Self-loop ditambahkan pada adjacency matrix.
- Topologi graf tetap sama pada setiap sampel; landmark yang tidak terlihat ditangani melalui visibility/presence dan aturan validitas data, bukan dengan mengubah indeks node.

### Optional Geometric Features

Sudut sendi dan jarak relatif dapat diuji sebagai fitur tambahan, tetapi bukan requirement awal. Jika digunakan, fitur tersebut harus dimasukkan dengan cara yang jelas ke node feature atau representasi edge, lalu dievaluasi terpisah agar manfaat tambahannya dapat diukur.

### Landmark Quality

Landmark dengan confidence terlalu rendah perlu ditangani dengan aturan yang konsisten. Sampel dengan ekstraksi yang gagal tidak boleh diberi koordinat buatan tanpa dasar. Catat jumlah sampel gagal secara keseluruhan dan per kelas karena kegagalan tersebut dapat mengubah distribusi data training.

## 9.13 Low-Probability Prediction Filtering

YogaMate tidak memiliki kelas ML khusus bernama **Unknown**. Sistem menggunakan threshold untuk mencegah prediksi dengan probabilitas rendah langsung dianggap sebagai pose yang benar.

### Logic

```text
Landmark valid?
    ↓ no → Body Incomplete / tidak dianalisis
    ↓ yes
Graph Construction + Feature Normalization
    ↓
GCN
    ↓
Class Probabilities
    ↓
Maximum Probability >= threshold?
    ↓ yes → prediction accepted
    ↓ no  → prediction ignored for matching
```

Prediksi dengan probabilitas di bawah threshold tidak perlu ditampilkan sebagai label tersendiri kepada pengguna.

### Threshold

Threshold ditentukan berdasarkan validation set dan disimpan bersama versi model serta skema fitur. Test set hanya digunakan untuk evaluasi final, bukan untuk memilih threshold.

### Product Behavior

Jika target pose adalah Warrior II dan probabilitas prediksi tertinggi tidak melewati threshold, sistem tidak menyatakan bahwa pengguna sedang melakukan Warrior II. Frame tersebut diperlakukan sebagai **non-match** pada evaluasi sesi.

Tidak ada body-part correction pada MVP.

## 9.14 Pose Matching

Karena pose target sudah ditentukan melalui Gacha, computer vision tidak bertugas memilih pose latihan. Computer vision bertugas mencocokkan hasil classifier dengan **target pose**.

### Match Rule

```text
Predicted Label == Target Label
AND
Probability >= threshold
→ Matched Frame
```

Jika salah satu kondisi tidak terpenuhi:

```text
→ Non-Matched Frame
```

Prediksi yang berada di bawah threshold diperlakukan sebagai non-match, bukan sebagai kelas tambahan.

## 9.15 Total Yoga Accuracy

### Important Definition

**Total Yoga Accuracy bukan accuracy model machine learning.**

Total Yoga Accuracy adalah metrik sesi yang menunjukkan proporsi frame yang dapat dianalisis dan berhasil dicocokkan dengan pose target.

### Baseline Formula

```text
Total Yoga Accuracy (%)
=
(matched frames / total analyzable frames) × 100
```

`Analyzable frame` adalah frame yang memiliki satu tubuh pengguna dengan landmark yang memenuhi syarat minimum untuk diproses oleh classifier.

Jika GCN menghasilkan probabilitas tertinggi di bawah threshold, frame tersebut tetap termasuk dalam `total analyzable frames` dan dihitung sebagai **non-matched frame**.

### Example

```text
Analyzable frames : 1,000
Matched frames    : 700

Total Yoga Accuracy = 70%
```

### Why This Definition

Definisi ini digunakan karena:

- mudah dipahami pengguna;
- dapat dihitung selama sesi berlangsung;
- tidak membutuhkan body-part correction model;
- tidak membutuhkan kelas tambahan bernama `Unknown`;
- tetap menghitung prediksi probabilitas rendah sebagai ketidaksesuaian pada frame yang dapat dianalisis.

### Important Limitation

Total Yoga Accuracy menunjukkan **tingkat kecocokan pose terhadap target menurut sistem computer vision**, bukan pengukuran kebenaran biomekanik manusia secara klinis.

## 9.16 Voice Instruction

Voice hanya digunakan sebagai **instruction satu kali**.

### Flow

```text
Start Pose
    ↓
Play Instruction Audio
    ↓
Audio selesai
    ↓
Kamera + Timer berjalan
```

### Example

> “Untuk Tree Pose, berdiri tegak, pindahkan berat badan ke satu kaki, lalu letakkan telapak kaki lainnya pada sisi paha bagian dalam dan pertahankan kedua tangan di atas kepala.”

### Rules

- Audio diputar satu kali per pose/session.
- Tidak ada voice correction berulang.
- Tidak ada cooldown 5 detik karena tidak ada voice feedback berulang.
- Audio dapat diputar dari file yang telah disiapkan sebelumnya.

### Implementation

Rekomendasi MVP: audio instruction disimpan sebagai asset lokal dan diputar menggunakan Android audio playback. TTS dinamis dapat menjadi alternatif jika tim membutuhkannya, tetapi bukan requirement.

---

## 9.17 Skeleton Overlay

Skeleton harus ditampilkan pada live camera.

Minimum:

```text
33 landmarks
+
connections antar landmark
```

Overlay harus tetap berada pada koordinat yang tepat terhadap preview kamera.

Skeleton tidak dimaksudkan sebagai diagnostic visualization.

---

## 9.18 Timer

Timer hanya berjalan ketika user telah memulai sesi.

### Requirement

- Display remaining duration.
- Menandai start.
- Mengakhiri session ketika duration habis.
- Mengirim event completion ke session manager.

---

## 9.19 Session Result

Setelah timer selesai, result screen menampilkan minimal:

```text
Pose
Tree Pose

Duration
02:00

Total Yoga Accuracy
85%

Status
Completed
```

Prediksi dengan probabilitas rendah tetap dianggap sebagai non-match pada frame yang dapat dianalisis dan tidak perlu ditampilkan sebagai label khusus kepada pengguna.

---

## 9.20 History

History menyimpan:

- session ID;
- user ID;
- target pose;
- difficulty level;
- date/time;
- duration;
- total yoga accuracy;
- completion status.

History dapat ditampilkan berdasarkan tanggal terbaru.

---

# 10. Gamification — Achievement System

Gamifikasi utama YogaMate menggunakan **achievement**.

Achievement harus memiliki progress bar sehingga pengguna dapat melihat kemajuan.

## 10.1 Achievement List — MVP

| Achievement | Condition | Progress |
|---|---|---|
| First Flow | Menyelesaikan 1 sesi latihan | 0/1 |
| Getting Started | Menyelesaikan 3 sesi | 0/3 |
| Yoga Routine | Menyelesaikan 7 sesi | 0/7 |
| Consistent Yogi | Menyelesaikan latihan pada 7 hari berbeda | 0/7 |
| Pose Explorer I | Mencoba 10 pose unik | 0/10 |
| Pose Explorer II | Mencoba 25 pose unik | 0/25 |
| Pose Explorer III | Mencoba 50 pose unik | 0/50 |
| Yoga 82 | Mencoba seluruh 82 pose | 0/82 |
| Beginner Complete | Menyelesaikan seluruh pose pada pool Beginner | 0/N |
| Intermediate Complete | Menyelesaikan seluruh pose pada pool Intermediate | 0/N |
| Advanced Complete | Menyelesaikan seluruh pose pada pool Advanced | 0/N |
| Long Practice | Total durasi latihan mencapai 60 menit | 0/60 |

`N` mengikuti jumlah pose final pada masing-masing level setelah mapping 82 pose ditetapkan.

## 10.2 Achievement Rules

- Achievement hanya terbuka setelah condition terpenuhi.
- Progress dihitung dari data session yang tersimpan.
- Achievement tidak menggunakan pembayaran.
- Achievement tidak memberikan XP sebagai requirement MVP.
- Achievement dapat digunakan sebagai motivasi dan penanda progress.

---

# 11. Health Education

Modul edukasi kesehatan/yoga menyediakan konten statis atau semi-statis mengenai:

- pengenalan yoga;
- manfaat umum aktivitas fisik;
- pemanasan sebelum latihan;
- pentingnya ruang latihan yang aman;
- penggunaan matras;
- hidrasi;
- kapan pengguna sebaiknya berhenti berlatih;
- catatan bahwa aplikasi bukan alat diagnosis medis.

Konten kesehatan harus menggunakan sumber yang dapat dipertanggungjawabkan dan tidak memberikan diagnosis personal.

---

# 12. Technical Architecture

## 12.1 Recommended Stack

### Android Mobile — Native Kotlin

**Android Native menggunakan Kotlin dan Jetpack Compose.** Aplikasi dibangun sebagai Android application project menggunakan Android Studio, Gradle Kotlin DSL, dan Android SDK.

Komponen utama:

- **Kotlin** sebagai bahasa utama aplikasi Android.
- **Jetpack Compose** untuk seluruh UI dan komponen visual.
- **Navigation 3** untuk navigasi antar-screen Compose; alternatif navigasi hanya boleh dipilih jika kebutuhan/kompatibilitas proyek membuktikannya lebih sesuai.
- **ViewModel + StateFlow + Kotlin Coroutines** untuk state management dan pekerjaan asinkron dengan pola unidirectional data flow.
- **Repository pattern** untuk memisahkan sumber data lokal, remote API, dan engine computer vision.
- **CameraX** (`Preview`, `ImageAnalysis`, dan lifecycle binding) untuk preview dan analisis frame kamera.
- **MediaPipe Tasks Vision Android** (`com.google.mediapipe:tasks-vision`) untuk Pose Landmarker.
- **ONNX Runtime Android** (`com.microsoft.onnxruntime:onnxruntime-android`) untuk menjalankan model GCN ONNX dari Kotlin/Java API.
- **Room** untuk cache terstruktur, sesi lokal, dan antrean data yang belum tersinkronisasi.
- **DataStore** untuk preferensi aplikasi dan konfigurasi sederhana.
- **WorkManager** untuk sinkronisasi data tertunda yang harus tetap berjalan setelah aplikasi ditutup atau perangkat dimulai ulang; WorkManager tidak digunakan untuk pemrosesan frame kamera atau inference real-time.
- **Retrofit/OkHttp** atau HTTP client Kotlin yang setara untuk komunikasi REST API dengan backend Go.
- **Android audio playback** untuk memutar instruction audio lokal. Text-to-Speech dinamis bukan requirement MVP.

### Android Platform Baseline

- Target platform MVP: Android saja.
- `minSdk` baseline: **24**, mengikuti persyaratan MediaPipe Tasks Android. Kompatibilitas dependency lain dan perangkat uji harus diverifikasi pada proof of concept. [3]
- `compileSdk`, Android Gradle Plugin, Kotlin, Compose BOM, CameraX, MediaPipe, dan ONNX Runtime harus dikunci ke versi stabil yang saling kompatibel ketika proyek dibuat. Jangan menggunakan versi `latest.release` tanpa mencatat versi final ke Gradle dependency configuration/lock convention tim.
- Izin kamera diminta ketika pengguna memulai fitur latihan, bukan saat aplikasi pertama kali dibuka.
- Aplikasi didistribusikan sebagai APK untuk pengujian internal dan AAB untuk distribusi Google Play jika diperlukan.

### Backend

**Go + PostgreSQL**

Recommended implementation:

- Go;
- REST API;
- `net/http` atau Gin;
- PostgreSQL;
- `pgx/v5` untuk akses PostgreSQL.

`pgx` menyediakan driver/toolkit PostgreSQL untuk Go dan sesuai untuk aplikasi yang secara khusus menggunakan PostgreSQL. [13]

### Machine Learning / Data Science

**Python + PyTorch** digunakan untuk:

- audit dataset dan data split;
- landmark extraction dari gambar Yoga-82;
- graph construction dan node feature normalization;
- training GCN;
- validation dan hyperparameter tuning;
- testing dan evaluasi;
- ekspor model ke ONNX.

Python **tidak** digunakan sebagai backend inference real-time pada MVP.

## 12.2 On-Device Inference Architecture

```text
                         ANDROID DEVICE

CameraX Preview + ImageAnalysis
          ↓
Frame Adapter / Rotation / Timestamp
          ↓
MediaPipe Pose Landmarker (LIVE_STREAM)
          ↓
33 Body Landmarks
          ↓
Landmark Quality Check
          ↓
Node Feature Normalization
          ↓
Fixed Skeleton Graph Construction
          ↓
GCN Model (.onnx)
          ↓
ONNX Runtime Android (Kotlin API)
          ↓
82-Class Logits / Probabilities
          ↓
Temporal Smoothing (if validated)
          ↓
Confidence Threshold + Target Pose Matching
          ↓
Session Aggregator / Total Yoga Accuracy
          ↓
ViewModel StateFlow
          ↓
Jetpack Compose UI / Skeleton Overlay / Result
```

Seluruh proses computer vision dan pose recognition berjalan di perangkat tanpa mengirim frame kamera ke server. Model GCN (`.onnx`), MediaPipe task model (`.task`), label kelas, serta konfigurasi preprocessing disediakan sebagai asset aplikasi lokal untuk MVP.

CameraX menangani preview dan analisis frame. Baseline `ImageAnalysis` menggunakan `STRATEGY_KEEP_ONLY_LATEST` agar frame yang sudah tertinggal tidak menumpuk. Analisis frame, MediaPipe, graph construction, dan ONNX inference tidak boleh berjalan pada main thread. Setiap frame harus memiliki timestamp yang valid dan resource kamera harus dilepas setelah diproses. [9]

MediaPipe Pose Landmarker berjalan dalam mode `LIVE_STREAM` untuk kamera real-time dan mengembalikan hasil secara asinkron. Hasil MediaPipe diteruskan ke graph preprocessing dan ONNX Runtime melalui pipeline analisis yang terkontrol. [2][3]

Topologi skeleton, urutan node, skema fitur, normalisasi, dan urutan label yang digunakan di Android harus identik dengan pipeline training. Ekspor ONNX dan inference Android harus diuji menggunakan input graf yang sama sebelum dinyatakan siap digunakan.

# 13. Backend Architecture

## 13.1 Responsibilities

Backend hanya menangani application data, bukan real-time computer vision.

```text
Android App
    │
    │ HTTPS REST API
    ↓
Go API
    ↓
PostgreSQL
```

### Backend Responsibilities

- authentication;
- user profile;
- pose metadata;
- difficulty mapping;
- achievement definitions/progress;
- session history;
- health education content jika disimpan server-side.

### Backend Must NOT

- menerima video camera untuk setiap frame;
- melakukan inference pose pada setiap request;
- menjadi dependency agar kamera dapat berjalan.

---

## 13.2 Suggested Database Entities

### `users`

- id
- name
- email
- password_hash
- created_at
- updated_at

### `profiles`

- id
- user_id
- height
- weight
- gender
- goal
- updated_at

### `poses`

- id
- dataset_class_name
- display_name
- level
- preview_image
- instruction_audio
- description

### `practice_sessions`

- id
- user_id
- pose_id
- level
- duration_seconds
- total_yoga_accuracy
- status
- started_at
- completed_at

### `achievements`

- id
- code
- name
- description
- target_value
- category

### `user_achievements`

- id
- user_id
- achievement_id
- progress_value
- unlocked_at

### `education_contents`

- id
- title
- category
- content
- image
- created_at
- updated_at

---

# 14. Mobile Architecture

YogaMate menggunakan arsitektur berlapis Android Native dengan pemisahan UI, business logic, data, dan engine computer vision. Implementasi mengikuti prinsip unidirectional data flow (UDF): UI mengirim event ke ViewModel, ViewModel berinteraksi dengan use case/repository, lalu UI menerima state terbaru melalui `StateFlow`. [8]

### Application Architecture

```text
Jetpack Compose UI
        ↓ user action / UI event
ViewModel + StateFlow
        ↓
Use Cases / Domain Logic
        ↓
Repositories
   ├── Authentication Repository
   ├── Workout / Session Repository
   ├── Remote API Repository (Go REST API)
   ├── Local Storage Repository (Room / DataStore)
   └── Pose Analysis Repository
             ↓
       On-Device CV Engine
       ├── CameraX Analyzer
       ├── MediaPipe Pose Landmarker
       ├── Graph Preprocessor
       └── ONNX Runtime Android
```

### Camera and ML Execution Architecture

- CameraX `ImageAnalysis.Analyzer` berjalan menggunakan executor/worker thread khusus.
- MediaPipe dan ONNX Runtime tidak dipanggil langsung dari composable UI.
- Pemrosesan graf dan inference dilakukan di thread/dispatcher komputasi, bukan main thread.
- Hasil yang dibutuhkan UI dikirim sebagai state ringkas melalui ViewModel/StateFlow.
- Lifecycle kamera mengikuti lifecycle layar workout. Analyzer dan Pose Landmarker harus ditutup/dilepas ketika sesi berakhir atau layar meninggalkan lifecycle aktif.
- Preview kamera dan skeleton overlay harus menggunakan transformasi koordinat yang konsisten, termasuk rotasi gambar dan front-camera mirroring.

### Data Architecture

- **Remote data source:** Go REST API untuk authentication, profile, metadata pose/level, achievement, dan sinkronisasi session history.
- **Local data source:** Room untuk session lokal/cache/queued sync; DataStore untuk preferensi dan konfigurasi pengguna.
- **Background synchronization:** WorkManager untuk mengirim antrean hasil sesi saat jaringan tersedia. WorkManager tidak menggantikan coroutine/executor untuk pekerjaan inference selama layar latihan aktif. [11][12]

### Required Modules

- `presentation`: Compose screens, reusable UI components, theme, navigation;
- `domain`: model domain, use cases, aturan workout dan pose matching;
- `data`: repository, DTO, mapper, remote/local data source;
- `camera`: CameraX preview, ImageAnalysis, permission dan lifecycle;
- `pose-estimation`: MediaPipe Pose Landmarker wrapper dan landmark mapping;
- `graph-processing`: quality check, node feature normalization, adjacency/graph construction;
- `ml-inference`: ONNX Runtime Android session, tensor creation, prediction output;
- `session`: timer, matched-frame aggregation, Total Yoga Accuracy;
- `audio`: instruction playback;
- `local-storage`: Room, DataStore, pending synchronization;
- `network`: API client dan error handling.

Struktur package dapat disesuaikan selama tanggung jawab modul tetap terpisah dan pipeline computer vision tidak bergantung langsung pada UI.

---

# 15. Local / Offline Data

Karena computer vision harus offline:

### Harus tersedia lokal

- MediaPipe Pose Landmarker model asset (`.task`);
- custom GNN/GCN ONNX model (`.onnx`);
- manifest model yang mencatat model version, urutan label, feature schema version, graph schema/adjacency version, dan threshold yang digunakan;
- pose labels;
- difficulty mapping;
- pose preview metadata yang diperlukan untuk sesi offline;
- instruction audio;
- cache data yang dibutuhkan untuk workout offline.

Penyimpanan lokal:

- **Room** untuk cache terstruktur, workout session lokal, dan data yang menunggu sinkronisasi;
- **DataStore** untuk preferensi aplikasi dan konfigurasi sederhana;
- asset model dan audio disimpan sebagai bundled assets atau disalin ke penyimpanan aplikasi yang sesuai.

Room direkomendasikan untuk data terstruktur lokal, khususnya agar data relevan tetap dapat dibaca saat perangkat offline. [10]

### Dapat menggunakan backend

- login/session sync;
- profile synchronization;
- history synchronization;
- achievement synchronization;
- education content update.

### Offline Requirement

Setelah user telah login dan resource model/content tersedia pada perangkat, fitur berikut harus tetap dapat berjalan tanpa internet:

- membuka workout;
- gacha dari local pose pool;
- melihat preview lokal;
- membuka kamera;
- MediaPipe inference;
- ML classification;
- timer;
- Total Yoga Accuracy;
- menyimpan sementara session result lokal.

Sinkronisasi ke backend dapat dilakukan ketika internet kembali tersedia. WorkManager dapat digunakan untuk menjalankan sinkronisasi tertunda yang perlu bertahan melewati app restart; inference kamera tetap berjalan secara in-process selama sesi latihan aktif. [11]

Computer vision tidak bergantung pada sinkronisasi backend. Koneksi internet diperlukan untuk operasi server-side seperti login awal, mengambil konten yang belum tersimpan, dan sinkronisasi data aplikasi.

---

# 16. Image Processing & Graph Preparation Requirements

Pemrosesan citra tetap menjadi bagian eksplisit dalam project. Pada pipeline ini, MediaPipe Pose Landmarker mengolah frame/gambar untuk menghasilkan landmark, sedangkan GNN memproses representasi graf yang dibangun dari landmark tersebut.

### Baseline Pipeline

```text
Raw Camera Frame / Yoga-82 Image
        ↓
MediaPipe Pose Landmarker Input Processing
(resize, normalization, format sesuai model)
        ↓
33 Pose Landmarks
        ↓
Landmark Quality Check
        ↓
Coordinate Normalization
        ↓
Graph Construction
        ↓
GCN Classification
```

MediaPipe melakukan input processing internal sesuai konfigurasi dan modelnya. Jika tim menambahkan preprocessing eksternal seperti perubahan brightness/contrast, denoising, atau ROI handling, perubahan itu harus diuji dan tidak boleh diterapkan berbeda antara data training dan inference kamera.

### Candidate Experimental Operations

Tidak semua teknik wajib digunakan. Pilih berdasarkan kebutuhan dan hasil eksperimen:

- normalisasi koordinat landmark relatif terhadap tubuh;
- filtering landmark berdasarkan visibility/presence;
- penyesuaian brightness/contrast pada gambar sumber jika dibutuhkan untuk meningkatkan ekstraksi landmark;
- frame sampling/skipping untuk efisiensi inference real-time;
- geometric feature tambahan, jika baseline GCN sudah dievaluasi.

### Experimental Questions

Contoh pertanyaan eksperimen:

- Berapa proporsi gambar Yoga-82 yang dapat diekstraksi menjadi graf dengan landmark yang cukup valid?
- Apakah normalisasi koordinat landmark meningkatkan kinerja klasifikasi GCN?
- Apakah node feature dasar sudah memadai, atau fitur geometris tambahan memberi peningkatan terukur?

Eksperimen harus menggunakan metrik yang terukur. Hindari menambah teknik preprocessing tanpa mengevaluasi dampaknya.

# 17. Dataset & Machine Learning Workflow

## 17.1 Dataset

**Yoga-82** merupakan dataset fine-grained yoga pose yang menyediakan 82 kelas dan hierarchical labels. [1]

### Dataset Source

https://sites.google.com/view/yoga-82/home

### Dataset Usage Note

Sumber resmi menyatakan gambar Yoga-82 berasal dari internet dan hak atas gambar tersebut tidak dimiliki oleh pembuat dataset. Ketentuan penggunaan dan lisensi gambar individual harus diperhatikan untuk kebutuhan publikasi/deployment. [1]

---

## 17.2 Training Pipeline

```text
Yoga-82 Images
   ↓
Dataset Audit + Label Verification
   ↓
Train / Validation / Test Split
   ↓
MediaPipe Pose Landmark Extraction
   ↓
Landmark Quality Check
   ↓
Coordinate Normalization
   ↓
Graph Construction (33 Nodes + Skeleton Edges)
   ↓
GCN Training (PyTorch)
   ↓
Validation & Hyperparameter Tuning
   ↓
Final Test Evaluation
   ↓
Model Export to ONNX
   ↓
ONNX Runtime Android (Kotlin API)
   ↓
Android Device
```

Graph construction, node order, adjacency matrix, dan feature normalization yang digunakan saat training wajib sama dengan yang digunakan pada inference mobile.

### Dataset Feasibility Gate

Sebelum training final, lakukan audit untuk mengukur keberhasilan ekstraksi landmark per kelas. Yoga-82 berisi gambar dari internet dengan variasi sudut pandang, occlusion, framing, dan kompleksitas pose; karena itu, tidak boleh diasumsikan seluruh gambar menghasilkan 33 landmark yang layak. Jika kegagalan ekstraksi terkonsentrasi pada kelas tertentu, dokumentasikan dampaknya terhadap jumlah sampel dan keseimbangan kelas sebelum memutuskan eksperimen 82 kelas penuh.

## 17.3 Data Split

Split final harus ditetapkan sebelum model training.

Baseline:

```text
70% Training
15% Validation
15% Test
```

Pembagian tersebut dapat diubah setelah melihat ukuran nyata tiap kelas.

### Leakage Prevention

- test data tidak boleh digunakan untuk training;
- test data tidak boleh digunakan untuk threshold tuning;
- preprocessing parameter yang dipelajari dari data hanya dihitung dari training set;
- reference/statistical features yang menjadi bagian model hanya dihitung dari data training.

---

# 18. Model Evaluation

## 18.1 Classification Metrics

Minimal:

- Accuracy;
- Precision;
- Recall;
- Macro F1-score;
- Confusion Matrix.

### Class-Level Analysis

Karena MVP menggunakan satu flat classifier, evaluasi utama dilakukan pada 82 kelas pose secara langsung. Analisis dapat mencakup performa setiap kelas dan pola confusion antarpose.

### Additional Analysis

- per-class performance;
- kelas yang paling sering tertukar;
- probability distribution;
- low-confidence prediction rate;
- threshold sensitivity;
- tingkat keberhasilan ekstraksi landmark per kelas;
- konsistensi output PyTorch dan ONNX pada input graf yang sama.

---

## 18.2 Camera Validation

Selain test set Yoga-82, perlu dilakukan pengujian menggunakan kamera nyata.

Variasi pengujian:

- pencahayaan berbeda;
- background sederhana/ramai;
- pakaian berbeda;
- posisi tubuh berbeda dalam frame;
- jarak pengguna yang berbeda;
- smartphone berbeda jika tersedia.

Tujuan pengujian adalah mengetahui domain shift antara gambar dataset dan penggunaan langsung melalui kamera.

---

# 19. Multi-Person Feasibility

## 19.1 Technical Feasibility

**Ya, multi-person memungkinkan secara teknis.** MediaPipe Pose Landmarker menyediakan parameter `num_poses` untuk mengatur jumlah maksimum pose yang ingin dideteksi; default-nya adalah 1. [3]

### Potential Architecture

```text
Camera
  ↓
MediaPipe Pose
  ↓
Person 1 → Landmarks → Classifier → Label 1
Person 2 → Landmarks → Classifier → Label 2
Person 3 → Landmarks → Classifier → Label 3
```

### Challenges

Multi-person membutuhkan tambahan logic untuk:

- mengasosiasikan landmark dengan orang tertentu;
- menggambar skeleton tiap orang;
- menyimpan target pose per orang;
- menghitung accuracy per orang;
- menentukan hasil sesi bila terdapat beberapa orang.

### Product Decision

**Multi-person tidak termasuk MVP.**

Arsitektur MVP harus tetap mendukung kemungkinan pengembangan ke multi-person, tetapi implementasi awal fokus pada satu orang agar scope sesuai resource 3 orang dan deadline 24 November 2026.

---

# 20. Performance Requirements

Belum ada target performa resmi dari dosen.

### Engineering Requirements

- Computer vision tidak boleh bergantung pada koneksi internet.
- UI Jetpack Compose tetap responsif ketika inference aktif.
- CameraX frame analysis, MediaPipe, graph construction, dan ONNX Runtime tidak boleh berjalan pada main/UI thread.
- Pipeline CameraX harus membatasi antrean frame agar analisis tidak memproses frame yang terlalu tertinggal; baseline menggunakan `STRATEGY_KEEP_ONLY_LATEST`.
- Resource `ImageProxy`, analyzer, camera provider/use case, Pose Landmarker, ONNX session, dan executor harus dikelola sesuai lifecycle serta dilepas dengan aman.
- MediaPipe dan ONNX Runtime tidak boleh menyebabkan UI freeze atau ANR.
- Temporal smoothing tidak boleh menambah latency yang mengganggu latihan.
- Graph construction, adjacency matrix, node ordering, label ordering, dan normalisasi fitur pada Android harus konsisten dengan pipeline training.
- Ekspor ONNX untuk GCN harus lolos pengujian parity dengan output PyTorch.
- Android `minSdk` tidak boleh lebih rendah dari 24 pada baseline karena persyaratan MediaPipe Tasks Android; kompatibilitas perangkat lain harus diuji. [3]
- Kamera hanya aktif saat dibutuhkan dan harus dihentikan setelah sesi/layar kamera berakhir.

### Optional Development Metrics

Dicatat selama development:

- inference latency;
- effective processed FPS;
- model size;
- cold start time;
- memory usage;
- end-to-end camera-to-prediction latency.

Metrics tersebut menjadi data engineering, bukan requirement nilai akademik yang telah ditentukan dosen.

# 21. Mobile Technology Recommendation

### Recommended

> **Android Native (Kotlin + Jetpack Compose) + CameraX + MediaPipe Tasks Vision + ONNX Runtime Android**

### Reasoning

- Android merupakan satu-satunya target platform MVP, sehingga framework lintas platform tidak menjadi kebutuhan produk.
- Kotlin dan Jetpack Compose menyediakan implementasi native untuk UI, lifecycle, permission, dan integrasi Android. Jetpack Compose direkomendasikan Android Developers untuk aplikasi baru; arsitektur Android juga merekomendasikan pemisahan UI/data layer, ViewModel, Flow, dan repository. [8][10]
- CameraX menyediakan `Preview` dan `ImageAnalysis` untuk kamera serta pemrosesan frame. Strategi `STRATEGY_KEEP_ONLY_LATEST` dapat digunakan untuk menghindari antrean frame yang tertinggal. [9]
- MediaPipe Pose Landmarker tersedia sebagai MediaPipe Tasks Vision Android SDK dan menyediakan 33 landmark dengan mode `LIVE_STREAM` untuk kamera. [2][3]
- GCN memanfaatkan struktur hubungan antarlandmark tubuh yang secara alami dapat direpresentasikan sebagai graf skeleton. [4]
- PyTorch menyediakan jalur ekspor model ke ONNX; keberhasilan ekspor tetap perlu diuji pada implementasi GCN aktual. [5]
- ONNX Runtime menyediakan package Android (`onnxruntime-android`) dengan API Java yang dapat dipanggil dari Kotlin. Kompatibilitas operator model GCN dan hasil inference harus divalidasi pada perangkat Android. [6][7]
- Room dan WorkManager mendukung penyimpanan lokal serta sinkronisasi tertunda untuk kebutuhan offline-first. [11][12]

### Mobile Decision

> **Android Native menggunakan Kotlin dan Jetpack Compose.**

CameraX, MediaPipe Tasks Vision, dan ONNX Runtime Android menjadi komponen native pada satu aplikasi Android. Arsitektur MVP tidak menggunakan framework lintas platform atau cross-platform bridge.

# 22. Security & Privacy

### Camera Data

- Aplikasi meminta izin `CAMERA` melalui runtime permission Android ketika pengguna memulai fitur latihan.
- Frame kamera diproses di memori perangkat dan tidak dikirim ke backend untuk inference.
- Video tidak direkam atau disimpan oleh MVP.
- Landmark hasil inference tidak disimpan kecuali benar-benar diperlukan dan disetujui untuk eksperimen.
- Kamera dan analyzer dihentikan saat sesi berakhir atau pengguna meninggalkan layar workout.

### Authentication

- Password disimpan sebagai hash di backend, tidak plaintext.
- API menggunakan HTTPS pada deployment.
- Token/session tidak boleh disimpan sebagai plaintext di file konfigurasi atau log. Implementasi penyimpanan kredensial harus menggunakan mekanisme penyimpanan aman Android yang sesuai, dengan perlindungan berbasis Android Keystore bila diperlukan.

### User Data

Data profile hanya disimpan untuk kebutuhan aplikasi dan tidak digunakan sebagai input model computer vision pada MVP. Data sesi lokal disimpan secara terbatas dan disinkronkan ke akun pengguna yang sesuai.

---

# 23. Non-Functional Requirements

### Usability

- Flow latihan mengikuti urutan utama yang telah ditetapkan.
- UI mudah dipahami pengguna umum.
- Informasi selama pose tidak terlalu padat.

### Performance

- Inference real-time/on-device.
- UI Jetpack Compose tidak freeze selama camera analysis.
- CameraX frame analysis, MediaPipe, dan ONNX Runtime berjalan di luar main thread.
- Camera pipeline menghindari backlog frame dan mengelola lifecycle resource dengan benar.
- Latency, processed FPS, memory usage, serta end-to-end camera-to-prediction latency dicatat pada perangkat uji.

### Reliability

- Prediksi dengan probabilitas rendah tidak digunakan sebagai pose yang valid.
- Camera failure harus memiliki state yang jelas.
- Session result tidak hilang ketika koneksi internet terputus.

### Maintainability

- Model version harus dapat dicatat.
- Mapping 82 pose tidak hard-coded di banyak tempat.
- Difficulty configuration disimpan sebagai data.

---

# 24. Acceptance Criteria — Core MVP

## Authentication

- [ ] User dapat register.
- [ ] User dapat login.
- [ ] User dapat logout.

## Navigation

- [ ] Beranda tersedia.
- [ ] Profile tersedia.
- [ ] Tombol Olahraga tersedia.
- [ ] Achievement dengan progress bar tersedia.
- [ ] Edukasi Kesehatan tersedia.

## Workout Flow

- [ ] User memilih kategori kesulitan.
- [ ] Gacha memilih pose dari pool level tersebut.
- [ ] Preview pose muncul.
- [ ] User memilih durasi.
- [ ] Yoga session dimulai.
- [ ] Timer berjalan.
- [ ] Timer selesai mengakhiri sesi.

## Android Native Platform

- [ ] Aplikasi dibangun sebagai Android Native menggunakan Kotlin.
- [ ] UI utama menggunakan Jetpack Compose.
- [ ] Navigasi antar-screen berjalan sesuai information architecture.
- [ ] Runtime camera permission diminta ketika fitur latihan digunakan.
- [ ] APK debug/internal build dapat dipasang dan dijalankan pada perangkat uji.

## Computer Vision

- [ ] CameraX Preview menampilkan kamera real-time.
- [ ] CameraX ImageAnalysis menyediakan frame untuk pipeline computer vision.
- [ ] Frame analysis tidak memblokir main/UI thread.
- [ ] Frame backlog dikendalikan dan setiap ImageProxy/resource ditutup dengan aman.
- [ ] Full-body detection berjalan.
- [ ] Skeleton overlay Compose sejajar dengan preview kamera setelah rotasi dan mirroring diperhitungkan.
- [ ] MediaPipe Pose Landmarker Android menghasilkan 33 landmarks.
- [ ] GCN classifier ONNX dijalankan melalui ONNX Runtime Android dan menghasilkan skor/probabilitas kelas pose.
- [ ] System mendukung 82 target class setelah model final selesai.
- [ ] Preprocessing, graph schema, node order, adjacency matrix, dan label order konsisten dengan pipeline Python.
- [ ] Output ONNX Runtime Android telah dibandingkan dengan output PyTorch pada input graf yang sama.
- [ ] Prediksi dengan probabilitas di bawah threshold tidak digunakan sebagai pose yang valid.
- [ ] Pose target dapat dibandingkan dengan detected label.
- [ ] Inference tidak membutuhkan internet.

## Voice Instruction

- [ ] Instruksi audio tersedia untuk pose yang digunakan.
- [ ] Audio diputar satu kali ketika pose dimulai.
- [ ] Tidak ada voice correction berulang.

## Result

- [ ] Total Yoga Accuracy ditampilkan.
- [ ] Session result tersimpan.
- [ ] History dapat ditampilkan.
- [ ] Achievement progress diperbarui.

---

# 25. Development Risks

| Risk | Impact | Mitigation |
|---|---|---|
| 82 kelas terlalu sulit dibedakan | High | Audit dataset, node feature normalization, GCN tuning + probability threshold |
| Domain shift Yoga-82 → kamera nyata | High | Camera validation + real-world test set |
| MediaPipe landmark tidak stabil | High | Full-body framing, landmark confidence filtering, preprocessing, temporal smoothing jika diperlukan |
| GCN belum optimal untuk 82 kelas | High | Validation, hyperparameter tuning, analisis fitur graf dan per-class performance |
| Probability threshold terlalu ketat/longgar | Medium | Threshold tuning pada validation set dan analisis precision/recall |
| On-device inference terlalu berat | Medium | Batasi feature complexity, optimasi frame processing, ukur latency dan memory |
| Integrasi CameraX dan MediaPipe Tasks Vision tidak stabil pada perangkat target | High | Buat proof of concept Android Native lebih awal; uji permission, format frame, timestamp, lifecycle, dan LIVE_STREAM pada perangkat fisik |
| CameraX frame backlog atau resource tidak dilepas | High | Gunakan STRATEGY_KEEP_ONLY_LATEST, analyzer executor khusus, dan pastikan ImageProxy ditutup pada semua jalur eksekusi |
| Ekspor GCN ke ONNX atau integrasi ONNX Runtime Android bermasalah | High | Gunakan operasi tensor standar, ekspor prototype lebih awal, periksa operator yang didukung, dan uji output parity pada perangkat Android |
| Ukuran model dan konsumsi memori terlalu besar | Medium | Ukur ukuran asset, cold start, peak memory, dan inference latency; evaluasi optimasi hanya setelah baseline berjalan |
| Perbedaan konfigurasi toolchain Android/Gradle/Kotlin | Medium | Kunci versi Android Gradle Plugin, Gradle, Kotlin, Compose BOM, dan dependency dalam dokumentasi setup proyek |
| Graph topology/node order/feature extraction tidak konsisten antara Python dan mobile | High | Dokumentasikan graph schema dan buat test graph yang sama untuk kedua environment |
| Scope membesar | High | Multi-person dan advanced correction ditunda |
| Sulit menentukan difficulty level | Medium | Buat taxonomy internal berdasarkan complexity dan validasi dengan referensi/ahli |
| Audio instruction terlalu panjang | Low | Batasi instruction dan uji usability |

# 26. Development Priorities

## Priority 1 — Computer Vision Core

```text
Yoga-82
 ↓
MediaPipe Landmark Extraction
 ↓
Feature Engineering
 ↓
GCN
 ↓
Evaluation
 ↓
Probability Threshold
 ↓
ONNX Export
```

## Priority 2 — Android Native Camera & On-Device ML Prototype

```text
Kotlin + Jetpack Compose (minimal app shell)
 ↓
CameraX Preview + ImageAnalysis
 ↓
MediaPipe Tasks Vision (Pose Landmarker)
 ↓
33 Landmarks + Skeleton Overlay
 ↓
Graph / Feature Preprocessing
 ↓
ONNX Runtime Android
 ↓
GCN Inference
 ↓
Prediction Parity + Device Performance Test
```

Proof of concept pada perangkat Android fisik harus diselesaikan sebelum seluruh fitur UI dan workout flow diintegrasikan. UI Compose tetap dapat dikembangkan paralel menggunakan mock data selama pipeline kamera dan model divalidasi.

## Priority 3 — Workout UX

```text
Difficulty
 ↓
Gacha
 ↓
Preview
 ↓
Duration
 ↓
Timer
 ↓
Result
```

## Priority 4 — Backend

```text
Go API
 ↓
PostgreSQL
 ↓
Authentication
 ↓
History
 ↓
Achievements
 ↓
Mobile Integration
```

## Priority 5 — Supporting Features

```text
Profile
Achievement UI
Health Education
Offline Session Persistence
```

# 27. Suggested Team Distribution

Dengan resource **3 orang**, pembagian awal:

| Person | Primary Responsibility |
|---|---|
| Afif | Android Native Kotlin, Jetpack Compose UI, navigation, CameraX integration, MediaPipe/ONNX Runtime Android integration, workout/session flow, mobile-backend integration |
| Hilwa | Computer Vision, Yoga-82 preprocessing, MediaPipe landmark extraction for dataset, feature engineering, GCN training, model evaluation, ONNX export, graph/model contract documentation |
| Risha | Backend Go, PostgreSQL, REST API, authentication, profile, history, achievement, API integration support |

Pembagian bukan pembatas mutlak. Hilwa dan Afif harus memiliki titik integrasi yang jelas sejak model dan graph feature mulai diuji pada perangkat.

# 28. Milestones sampai 24 November 2026

### Phase 1 — Dataset & GCN Prototype

- audit Yoga-82;
- menentukan split data;
- ekstraksi landmark;
- feature engineering;
- training GCN;
- evaluation;
- threshold experiment;
- export model ke ONNX.

### Phase 2 — Android Native Camera & ML Prototype

- setup project Android Native di Android Studio;
- konfigurasi Kotlin, Gradle, Jetpack Compose, dan Navigation;
- tentukan `minSdk` baseline 24 dan verifikasi kompatibilitas dependency;
- integrasi CameraX Preview dan ImageAnalysis;
- integrasi MediaPipe Tasks Vision Android (Pose Landmarker);
- validasi 33 landmark pada kamera fisik;
- implementasi skeleton overlay dan transformasi koordinat;
- implementasi graph/feature preprocessing di Kotlin;
- integrasi ONNX Runtime Android;
- validasi parity output PyTorch vs ONNX Runtime Android;
- ukur latency, processed FPS, memory, dan stabilitas.

### Phase 3 — Native UI & Workout Product Flow

- difficulty selection;
- gacha;
- preview;
- duration;
- instruction audio;
- timer;
- pose matching;
- result.

### Phase 4 — Backend & Gamification

- Go REST API;
- PostgreSQL;
- login/register;
- history;
- achievement;
- profile.

### Phase 5 — Integration & Testing

- full end-to-end flow;
- offline computer vision test;
- camera test;
- model test pada perangkat;
- performance test;
- usability test;
- bug fixing;
- final documentation.

# 29. Open Technical Decisions

Keputusan berikut sudah memiliki baseline, tetapi beberapa detail teknis tetap perlu dikunci pada tahap technical setup:

| Decision | Recommendation | Status |
|---|---|---|
| Mobile platform | Android Native | Lock |
| Primary language | Kotlin | Lock |
| UI framework | Jetpack Compose | Lock |
| Navigation | Navigation 3 for Compose | Recommended; confirm version compatibility at project setup |
| Android architecture | ViewModel + StateFlow + Coroutines + Repository pattern | Lock |
| Minimum Android SDK | API 24 baseline due to MediaPipe Tasks Android requirement | Lock baseline; verify all dependencies |
| Build system | Android Studio + Gradle Kotlin DSL | Lock |
| Camera | CameraX Preview + ImageAnalysis | Lock |
| Frame backpressure | `STRATEGY_KEEP_ONLY_LATEST` baseline | Recommended; validate on device |
| Pose estimation | MediaPipe Tasks Vision Android / Pose Landmarker | Lock |
| MediaPipe running mode | LIVE_STREAM for real-time camera | Recommended; validate frame/result flow |
| GNN architecture | GCN baseline with fixed skeleton graph | Proposed baseline; validate with prototype |
| Classification strategy | 1 flat classifier → 82 pose classes | Lock |
| Graph input | 33 landmark nodes, fixed MediaPipe skeleton edges, coordinate + visibility features | Proposed baseline; freeze after data audit |
| Training | Python / PyTorch | Lock baseline |
| Model export | ONNX via `torch.onnx.export` | Proposed; verify GCN export parity |
| On-device runtime | ONNX Runtime Android (`onnxruntime-android`) | Lock; verify operators and device performance |
| Execution provider | CPU baseline; evaluate NNAPI/XNNPACK only after measurement | Recommended / Verify |
| Model storage | Bundled local ONNX and MediaPipe `.task` assets | Lock for MVP |
| Local database | Room | Recommended for local session/cache and pending sync |
| Preferences | DataStore | Recommended |
| Background synchronization | WorkManager for queued data sync, not live inference | Recommended |
| Probability handling | Softmax threshold filtering, no `Unknown` class | Baseline; threshold via validation set |
| Temporal smoothing | Enabled if device testing shows benefit | Recommended |
| Backend | Go REST API | Lock |
| Database | PostgreSQL | Lock |
| PostgreSQL driver | pgx/v5 | Recommended |
| Computer vision inference | On-device/offline | Lock |
| Backend inference | Not used | Lock |
| Multi-person | Future | Out of MVP |
| Difficulty levels | Beginner / Intermediate / Advanced | Baseline |
| Achievement system | Badge + progress | Lock |

# 30. Final Product Definition

> **YogaMate adalah aplikasi Android Native untuk latihan yoga mandiri yang mengikuti alur: Login/Register → Beranda → Tombol Olahraga → Kategori Kesulitan → Gacha Gerakan → Preview Gerakan → Pilih Durasi → Melakukan Yoga → Timer Selesai → Total Yoga Accuracy. Aplikasi dibangun menggunakan Kotlin dan Jetpack Compose. CameraX digunakan untuk menampilkan preview dan menyediakan frame kamera melalui ImageAnalysis. MediaPipe Tasks Vision Android dengan Pose Landmarker digunakan untuk memperoleh 33 landmark tubuh secara real-time. Landmark direpresentasikan sebagai graf skeleton dengan node feature yang konsisten, lalu diproses oleh satu model Graph Convolutional Network (GCN), sebagai baseline GNN, yang dilatih menggunakan gambar Yoga-82 untuk mengenali pose. Model PyTorch direncanakan dikonversi ke ONNX dan dijalankan secara on-device menggunakan ONNX Runtime Android melalui API Kotlin/Java; keberhasilan ekspor, dukungan operator, kesesuaian output, dan performa perangkat harus diverifikasi melalui prototype. Prediksi dengan probabilitas di bawah threshold tidak digunakan sebagai pose yang valid dan tidak ditampilkan sebagai kelas `Unknown`. Temporal smoothing dapat digunakan untuk menstabilkan hasil prediksi. Camera processing, MediaPipe, graph preprocessing, dan ONNX inference berjalan di luar main thread serta tidak mengirim frame kamera ke backend. Backend Go REST API + PostgreSQL digunakan untuk authentication dan sinkronisasi data aplikasi. Room digunakan untuk data lokal/sesi tertunda dan WorkManager dapat menjalankan sinkronisasi tertunda ketika koneksi tersedia. Instruksi suara diputar satu kali saat pose dimulai, tanpa voice correction berulang. Gamifikasi menggunakan achievement dan progress bar, sedangkan Total Yoga Accuracy digunakan sebagai hasil evaluasi sesi, bukan sebagai sistem XP.**

# References

**[1] Yoga-82 — Official Dataset Page**  
https://sites.google.com/view/yoga-82/home

**[2] Google AI Edge — Pose Landmarker**  
https://developers.google.com/edge/mediapipe/solutions/vision/pose_landmarker

**[3] Google AI Edge — Pose Landmarker for Android / MediaPipe Tasks Android Setup**  
https://developers.google.com/edge/mediapipe/solutions/vision/pose_landmarker/android  
https://developers.google.com/edge/mediapipe/solutions/setup_android

**[4] Kipf, T. N., & Welling, M. (2017). Semi-Supervised Classification with Graph Convolutional Networks. International Conference on Learning Representations (ICLR).**  
https://arxiv.org/abs/1609.02907

**[5] PyTorch — torch.onnx: Export a PyTorch model to ONNX**  
https://docs.pytorch.org/docs/stable/onnx.html

**[6] ONNX Runtime — Install for Android (Java/Kotlin)**  
https://onnxruntime.ai/docs/install/

**[7] ONNX Runtime — Deploy on Mobile / Android Runtime**  
https://onnxruntime.ai/docs/tutorials/mobile/  
https://onnxruntime.ai/docs/build/android.html

**[8] Android Developers — Recommendations for Android Architecture**  
https://developer.android.com/topic/architecture/recommendations

**[9] Android Developers — CameraX Image Analysis**  
https://developer.android.com/media/camera/camerax/analyze

**[10] Android Developers — Jetpack Compose Setup**  
https://developer.android.com/develop/ui/compose/setup

**[11] Android Developers — Save Data in a Local Database Using Room**  
https://developer.android.com/training/data-storage/room

**[12] Android Developers — WorkManager / Persistent Work Scheduling**  
https://developer.android.com/develop/background-work/background-tasks/persistent

**[13] pgx — PostgreSQL driver and toolkit for Go**  
https://github.com/jackc/pgx
