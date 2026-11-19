# YogaMate — Ringkasan Implementasi & Catatan Teknis

> Ringkasan kondisi proyek per **2026-11-09**. Disusun dari catatan pengerjaan sesi-sesi sebelumnya;
> untuk spesifikasi lengkap lihat `plan.md` dan `YogaMate_PRD_v2.4_Android_Native.md` di root;
> checklist lengkap semua fase PRD vs status ada di `roadmap-implementasi.md`.

## Ikhtisar

Aplikasi Android native **Kotlin + Jetpack Compose (Material 3)**.

| | |
|---|---|
| applicationId | `com.viscit.yogamate` |
| minSdk / target / compile | 26 / 37 / 37 |
| AGP / Kotlin | 9.4.1 / 2.2.10 |
| Compose BOM | 2026.06.00 (ui 1.11.3 — dinaikkan karena peringatan inspeksi Studio pada ui 1.10.4) |
| Dependency kunci | CameraX 1.4.2, MediaPipe tasks-vision 0.10.14, material-icons-extended, Navigation Compose 2.9.5 |

## Status fitur — selesai

- **UI utama**: onboarding, auth lokal (login/register/choose auth), Beranda, Aktivitas, Pencapaian, Profil, Edit Profil, Edukasi Kesehatan (4 kartu; entri dari Beranda + Profil).
- **Alur yoga**: setup 4 langkah (pilih level → gacha pose beranimasi 3 fase ±3,7 dtk dengan getar → kenali pose → slider durasi dengan snap beranimasi) → sesi kamera → layar complete → riwayat.
- **Kamera sesi**: CameraX kamera depan (fallback belakang), full-screen edge-to-edge, semua UI sebagai overlay chip transparan gelap; izin runtime dengan fallback teks saat ditolak.
- **Deteksi pose**: MediaPipe Pose Landmarker **Full** (model 9 MB di assets, LIVE_STREAM, delegate GPU → fallback CPU), skeleton overlay 33 landmark real-time di atas preview.
- **Akurasi sesi**: masih **disimulasikan 75–95%** — belum ada pembanding pose target.
- **Penyimpanan**: SharedPreferences via `AppSession` (profil, riwayat sebagai JSON). Password tersimpan plaintext — disengaja untuk simulasi, ganti saat backend nyata.

## Keputusan desain/teknis penting (tidak terlihat dari sekadar membaca kode)

- **Design tokens**: `#FFFCF7 / #28086B / #776887 / #C5A1F9 / #F2EAF9 / #DFFFBE / #AF3549`; font Plus Jakarta Sans 700 + DM Sans 400/500/600.
- **Navigasi**: memakai animasi default Navigation Compose (fade + slide). Sebelumnya dipaksa `fadeIn(snap())` / `fadeOut(snap())` untuk "nol animasi"; `EnterTransition.None` ditolak karena layar lama+baru menumpuk ±0,1 dtk di Navigation Compose 2.9.
- **Pola bottom bar**: tab/link yang menumpuk stack membuat tap tab macet. Satu titik temu di handler bottom bar (`YogaMateApp.kt`): `popBackStack(tab.route)` dulu, baru navigate; Profil selalu keluar via `popBackStack()`.
- **Reset state setup**: dilakukan di `DisposableEffect { onDispose }` (bukan di callback tombol) agar tidak kilatan satu frame; guard `continuedToSession` agar lanjut ke sesi tidak ikut ter-reset. Back dari sesi ke setup sengaja tidak reset.
- **Detail Riwayat & Panduan** = modal bottom sheet, bukan route.
- **Topbar alur setup**: `←` mundur satu step (step 1 = keluar), `✕` langsung ke Beranda.

## Konfigurasi build release

- **R8 + shrinkResources AKTIF** (sejak 2026-11-09): APK **67 MB → 54 MB**. DSL AGP 9: `optimization { enable = true }`, resource shrink masih `isShrinkResources = true` di level build type (blok `optimization` tidak punya properti itu).
- **`app/proguard-rules.pro` wajib** — tanpa itu MediaPipe gagal di runtime (dua kegagalan berbeda: field reflection protobuf `Field ... not found`, lalu stack-walk `no caller found on the stack`). Isinya: keep `com.google.protobuf.**` + `com.google.mediapipe.**`, `-dontoptimize`, `-dontwarn` (protobuf + 2 kelas proto yang memang tidak ada di AAR — referensi menggantung di jalur profiler yang tak dipakai).
- **`mapping.txt`** di `app/build/outputs/mapping/release/` = kamus retrace crash log ter-obfuscate; simpan per rilis.
- **Signing**: tidak ada signing config → `assembleRelease` menghasilkan APK unsigned → `zipalign` + `apksigner` (build-tools **36.0.0**; 35.0.0 tidak terinstall) dengan debug keystore `~/.android/debug.keystore` (pass `android`) → bisa `adb install -r` tanpa hilang data.
- **Quirk APK debug (AGP 9.4.1)**: output final di `app/build/intermediates/apk/debug/app-debug.apk`, bertanda test-only → WAJIB `adb install -r -t`.

## Hasil audit performa (2026-11-08)

- **4 fix Compose** (sudah diterapkan): animasi gacha via `graphicsLayer` (bukan `Modifier.rotate` state); `remember` untuk derivasi riwayat di Beranda & Aktivitas; heatmap Aktivitas O(1) lookup; `remaining` dibaca sekali di SessionScreen.
- **Lag pindah tab**: bukan bug layar — tiap pindah tab ada tepat 1 frame 26–69 ms (komposisi layar tujuan; di layar 120 Hz terasa mikro-hitch). Idle bersih, GPU sehat (99th 9–11 ms). Frame ~500 ms sekali setelah install = dexopt/JIT, hilang di cold start berikutnya.

## Gotchas pengujian device (HP Xiaomi, wireless ADB)

- Koordinat tap **wajib** dari `android_ui_resolve` (tree UI bisa landscape-swapped; koordinat dari screenshot ter-scale sering meleset).
- Swipe UI automation yang mulai dekat tepi kiri (x < ~150 px) bisa terpicu gesture BACK sistem — mulai swipe dari x ≥ ~200.
- ADB wireless sering drop di tengah sesi → fallback emulator-5554. Emulator kadang ANR "System UI isn't responding" (bukan dari app) → pilih Wait.
- Setelah `am force-stop` + `am start`, tunggu 2–3 dtk dan cek `pidof` — kadang intent diterima instance lama (cold start gagal).

## Roadmap tersisa (urut dampak)

> Versi lengkap (semua fase PRD + jalur Hilwa/Risha): `roadmap-implementasi.md`.

1. **Ukuran APK ±30 MB lagi**: `abiFilters = ["arm64-v8a"]` atau AAB (native `.so` MediaPipe untuk 4 ABI), opsional model `lite` 9→5 MB (tradeoff akurasi).
2. **Reuse bitmap per frame** di `PoseEngine.toUprightBitmap` (kini alokasi 2 bitmap/frame ≈ 2,5 MB/dtk GC churn) + opsional `ResolutionSelector` 480×360.
3. **Baseline profile** alur app (butuh modul benchmark; profile library Compose/Material sudah otomatis).
4. Naikkan `core-ktx 1.10.1` & `lifecycle 2.6.1` (jomplang vs BOM).
5. **Pipeline ML lanjutan (gap PRD)**: camera states UI (NO_PERSON/BODY_INCOMPLETE/POSE_MATCHED/NOT_MATCHED), akurasi real-time vs pose target, voice instruction TTS.
6. **Backend/DB nyata**: auth server, Room untuk riwayat; ganti penyimpanan password plaintext.
7. Konten Edukasi Kesehatan perlu review medis bila mau dipertanggungjawabkan.
