# YogaMate — Roadmap Implementasi (Checklist PRD vs Status)

> Peta lengkap seluruh pekerjaan PRD v2.4 terhadap kondisi kode per **2026-11-09**.
> Gabungan dari `YogaMate_PRD_v2.4_Android_Native.md` (bab 26–28: priorities, tim, milestones)
> dan `ringkasan-implementasi.md` (status aktual). Target PRD: **24 November 2026** (sisa ±15 hari).
>
> Legenda: ✅ selesai · 🔄 sebagian · ⬜ belum mulai
> Pemeliharaan: perbarui dokumen ini **dan** `ringkasan-implementasi.md` setiap kali status berubah.

## Status ringkas per fase

| Fase PRD (bab 28) | PIC (bab 27) | Status | Catatan |
|---|---|---|---|
| 1. Dataset & GCN Prototype | Hilwa | ⬜ 0% | Belum ada jejak di repo / rekaman sesi |
| 2. Camera & On-Device ML Prototype | Afif | 🔄 ±60% | Kamera + MediaPipe + skeleton jalan; preprocessing GCN & ONNX belum |
| 3. Native UI & Workout Flow | Afif | 🔄 ±75% | Pose matching masih simulasi; voice instruction belum |
| 4. Backend & Gamification | Risha | ⬜ 0% | Semua fitur tersistem versi lokal UI-only |
| 5. Integration & Testing | semua | 🔄 ±30% | Uji kamera HP fisik + audit perf UI lulus; test ML menunggu Fase 1–2 |

Jalur kritis menuju 24 Nov: **Fase 1 (GCN → ONNX) adalah pekerjaan terpanjang** dan belum mulai —
selama export model belum ada, item 🔄 di Fase 2–3 dan Total Yoga Accuracy (PRD 9.15) tidak bisa diselesaikan.

---

## Fase 1 — Dataset & GCN Prototype (Hilwa) — ⬜

- [ ] ⬜ Audit dataset Yoga-82 (PRD 17.1)
- [ ] ⬜ Tentukan split data (PRD 17.3)
- [ ] ⬜ Ekstraksi landmark MediaPipe untuk gambar Yoga-82
- [ ] ⬜ Feature engineering (PRD 9.12: 33 node, edges skeleton MediaPipe, koordinat + visibility)
- [ ] ⬜ Training GCN (PRD 9.11, PyTorch)
- [ ] ⬜ Evaluation (PRD 18.1: classification metrics)
- [ ] ⬜ Threshold experiment (PRD 9.13: filter probabilitas rendah, tanpa kelas Unknown)
- [ ] ⬜ Export model ke ONNX (`torch.onnx.export`)
- [ ] ⬜ Dokumentasikan graph schema / kontrak model (kesepakatan dengan sisi mobile)

## Fase 2 — Android Native Camera & ML Prototype (Afif) — 🔄

- [x] ✅ Setup project Android Native (`com.viscit.yogamate`)
- [x] ✅ Konfigurasi Kotlin, Gradle, Compose, Navigation (AGP 9.4.1, Kotlin 2.2.10, BOM 2026.06.00, Navigation Compose 2.9.5)
- [x] ✅ minSdk baseline (actual 26, ≥ baseline PRD 24)
- [x] ✅ Integrasi CameraX Preview + ImageAnalysis (1.4.2; kamera depan, fallback belakang)
- [x] ✅ Integrasi MediaPipe Tasks Vision Pose Landmarker (0.10.14, model Full 9 MB, LIVE_STREAM, GPU → fallback CPU)
- [x] ✅ Validasi 33 landmark pada kamera fisik (HP Xiaomi, lulus 2026-11-09)
- [x] ✅ Skeleton overlay + transformasi koordinat (overlay real-time di atas preview)
- [ ] ⬜ Graph / feature preprocessing di Kotlin — butuh kontrak dari Fase 1
- [ ] ⬜ Integrasi ONNX Runtime Android — butuh model ONNX dari Fase 1
- [ ] ⬜ Validasi parity PyTorch vs ONNX Runtime Android — butuh dua item di atas
- [ ] ⬜ Ukur latency, FPS inference, memory, stabilitas (audit perf UI sudah; audit **inference ML** belum)

## Fase 3 — Native UI & Workout Product Flow (Afif) — 🔄

- [x] ✅ Difficulty selection (setup step 1)
- [x] ✅ Gacha (animasi 3 fase ±3,7 dtk + getar)
- [x] ✅ Pose preview (setup step 3 "kenali pose")
- [x] ✅ Duration (slider snap beranimasi)
- [ ] ⬜ Voice instruction — TTS diputar sekali saat pose dimulai, tanpa koreksi berulang (PRD 9.16)
- [x] ✅ Timer
- [ ] ⬜ Pose matching — **masih disimulasikan 75–95%**; butuh Fase 1–2 (PRD 9.14)
- [x] ✅ Result (layar complete → riwayat)

Pendukung Fase 3 / Priority 5 yang sudah selesai (versi lokal): onboarding, auth lokal
(login/register UI-only), Beranda, Aktivitas, Pencapaian (5 lencana dari riwayat lokal, PRD 10.1–10.2),
Profil + Edit Profil, Edukasi Kesehatan (4 kartu), Detail Riwayat & Panduan (bottom sheet), riwayat lokal.

Item PRD 9.8 yang belum ada di UI kamera: state NO_PERSON / BODY_INCOMPLETE /
POSE_MATCHED / NOT_MATCHED (baru skeleton + timer).

## Fase 4 — Backend & Gamification (Risha) — ⬜

- [ ] ⬜ Go REST API + PostgreSQL (pgx/v5)
- [ ] ⬜ Login/register server — saat ini auth lokal UI-only
- [ ] ⬜ History server-side + sinkronisasi — saat ini SharedPreferences JSON via `AppSession`
- [ ] ⬜ Achievement server-side — saat ini dihitung lokal dari riwayat
- [ ] ⬜ Profil server-side
- [ ] ⬜ Migrasi penyimpanan: Room (sesi/cache pending sync) + DataStore (preferensi), ganti password plaintext
- [ ] ⬜ WorkManager untuk sinkronisasi tertunda

## Fase 5 — Integration & Testing (semua) — 🔄

- [x] ✅ Camera validation di perangkat fisik (PRD 18.2) — lulus
- [x] ✅ Audit performa UI (gfxinfo + 4 fix Compose; idle bersih, GPU 99th 9–11 ms)
- [x] ✅ Build release teruji: R8 + shrinkResources, uji kamera HP lulus
- [ ] ⬜ Model test pada perangkat — menunggu Fase 1–2
- [ ] ⬜ Full end-to-end flow dengan ML nyata (Total Yoga Accuracy, PRD 9.15)
- [ ] ⬜ Offline CV test, usability test, bug fixing, final documentation

---

## Kerja selesai di luar fase PRD

- R8 + shrinkResources (APK 67 → 54 MB) + `proguard-rules.pro` untuk MediaPipe/protobuf (WAJIB, lihat ringkasan).
- Tooling rilis: `zipalign` + `apksigner` build-tools 36.0.0, simpan `mapping.txt` per rilis.
- Audit performa Compose (4 fix `remember`/`graphicsLayer`) + audit lag Aktivitas via gfxinfo.

## Penyimpangan kondisi saat ini vs PRD v2.4

| Aspek | PRD v2.4 | Kondisi sekarang |
|---|---|---|
| minSdk | 24 | 26 (≥ baseline, aman) |
| Navigasi | Navigation 3 (recommended) | Navigation Compose 2.9.5 |
| DB lokal | Room (recommended) | SharedPreferences JSON |
| Preferensi | DataStore (recommended) | SharedPreferences |
| Auth | Server Go | Lokal UI-only; password plaintext (disengaja untuk simulasi) |
| Klasifikasi pose | GCN via ONNX Runtime Android | Disimulasikan 75–95% |

Semua penyimpangan bersifat sementara sampai Fase 1–2 dan Fase 4 selesai; tidak ada yang memblokir.

## Urutan kerja berikutnya

**Jalur paralel (per PIC):**

1. **Hilwa — Fase 1** (jalur kritis deadline): audit Yoga-82 → training GCN → export ONNX → kontrak graph.
2. **Risha — Fase 4**: bisa mulai kapan saja, tidak diblokir; minimal Go API + auth + history.
3. **Afif — lanjutan Fase 2–3**, urut dampak (disepakati di ringkasan):
   1. Ukuran APK: `abiFilters = ["arm64-v8a"]` atau AAB; opsional model `lite` 9 → 5 MB;
   2. Reuse bitmap per frame di `PoseEngine.toUprightBitmap` (+ opsional `ResolutionSelector` 480×360);
   3. Baseline profile (butuh modul benchmark);
   4. Naikkan `core-ktx 1.10.1` & `lifecycle 2.6.1` (jomplang vs BOM);
   5. Integrasi ONNX + preprocessing Kotlin begitu model dari Fase 1 tersedia, lalu camera states UI, akurasi real-time, voice TTS;
   6. Migrasi Room/DataStore saat backend siap.

Catatan lintas jalur: konten Edukasi Kesehatan perlu review medis bila mau dipertanggungjawabkan (bukan bloker MVP).
