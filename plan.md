# YogaMate — Plan Implementasi UI (final, setelah audit + inspeksi Figma)

## 1. Ringkasan
Implementasi UI YogaMate (Kotlin + Jetpack Compose) sesuai `plan_yogamate_android_revised.md`. Bottom navigation **3 tab** (Beranda, Aktivitas, Pencapaian); **Profil non-tab** dibuka dari ikon kanan atas. Sumber visual: Figma file `fQ1MjzqWso0FTKj8CgLbJ9` (diperiksa via Figma REST API — MCP Figma tidak terekspos sebagai tool di sesi ini, API key dari konfigurasi host dipakai langsung; metode dan data identik).

## 2. Hasil audit codebase (Tahap 0) — Verified
- Proyek template Compose baru: package `com.viscit.yogamate`, hanya `MainActivity.kt` + `ui/theme/{Color,Theme,Type}.kt` (default template, belum dipakai berarti).
- Gradle: AGP 9.4.1, Kotlin 2.2.10, Compose BOM 2026.02.01, minSdk 26, targetSdk/compileSdk 37, Java 11.
- Dependency tersedia: compose-ui, material3, activity-compose, core-ktx, lifecycle-runtime-ktx. **Belum ada navigation** → ditambah `androidx.navigation:navigation-compose` (kebutuhan teknis nyata) dan `material-icons-extended` (sudah dicakup BOM, untuk ikon Material kecil).
- Belum ada: API/auth/database/kamera/ML — tidak dikarang (batasan #5–#6 plan).
- Tidak ada pola arsitektur lama yang wajib diikuti → struktur `ui/feature/...` dari plan dipakai.

## 3. Hasil inspeksi Figma (18 screen) — Verified via screenshot + node data
Token warna (frame "COLOR PALETTE GUARDRAILS" 29:2430 + node data):
| Token | Hex | Pemakaian |
|---|---|---|
| Krem Hangat | `#FFFCF7` | latar belakang |
| Ungu Tua | `#28086B` | teks, tombol, ikon |
| Muted | `#776887` | teks sekunder |
| Lavendel | `#C5A1F9` | aksen, kartu gacha |
| Lavendel muda | `#F2EAF9` | kartu/chip/avatar |
| Hijau Limau | `#DFFFBE` | pill nav aktif, chip sukses |
| Outline | `#EEE8F5`/`#EAE3EF` | border kartu & input |
| Logout | `#AF3549` | tombol Logout (Profil) |

Typography: **Plus Jakarta Sans 700** (heading), **DM Sans 400/500/600** (body, tombol, label) — diambil dari node data (fontFamily+size+weight+lineHeight), font TTF diunduh ke `res/font`.

Frame khusus:
- **Mascot (30:6333)**: 7 pose maskot → diekspor PNG (instruksi #9) + logo header, mascot hero, ilustrasi pose, placeholder kamera, 3 ikon bottom nav. Semua ke `res/drawable-nodpi`.
- **Detail Riwayat (108:3430)**: ternyata **modal bottom sheet** "Cerita latihanmu" (di atas layar blur) → diimplementasi sebagai `ModalBottomSheet`, dibuka dari kartu riwayat (Beranda & Aktivitas), bukan route terpisah.
- **Beranda**: dua varian frame (102:1444 tanpa label "Bersama si Mate", 132:5111 dengan label). Plan menunjuk 102:1444 → varian itu yang dipakai.
- Bottom nav terlihat pada screenshot Profil (tanpa tab aktif) → bottom bar **tetap tampil** di Profil sesuai §6.4.5 plan.
- Frame "Melakukan Yoga" memakai foto placeholder kuda (120:4752) → diekspor sebagai placeholder kamera.

## 4. Peta navigasi — sesuai plan §6 (route = usulan plan, label UI tetap sesuai desain)
- Start: `!onboardingDone → onboarding` → `auth/method` → (register/login) → main. `onboardingDone` & `login` disimpan lokal (SharedPreferences) — **auth lokal UI-only, backend tidak ada** (Need Confirmation untuk integrasi nyata).
- Tab: `home`, `activity` (desain "Riwayat"), `achievement` — satu NavHost, bottom bar custom (pill hijau aktif).
- `profile`: destination non-tab, dibuka dari ikon kanan atas 3 tab; Back kembali ke tab asal (push biasa di back stack).
- `yoga/setup` (step 1–4 state internal, back/X per step) → `yoga/session` → `yoga/complete` → "Lihat riwayat"→tab Aktivitas, "Kembali ke beranda"→tab Beranda (popUpTo main, clear auth/yoga stack).
- Detail Riwayat & Panduan Singkat: modal bottom sheet (bukan route). Back/scroll menutup sheet dulu (perilaku bawaan ModalBottomSheet).
- Session state yoga (level, pose, durasi, hasil) lewat object in-memory `YogaSessionState`; bukan argumen route.

## 5. Data & Need Confirmation
- Riwayat: disimpan lokal (SharedPreferences JSON) setelah sesi selesai; seed kosong sesuai desain. Bulan kalender & statistik dihitung dari riwayat lokal.
- Pencapaian: 5 lencana dihitung dari riwayat (1/3/7 sesi, 10 pose, 60 menit) — angka target sesuai desain.
- Akurasi sesi: **disimulasikan** (belum ada ML/pose detection di codebase) — dicatat sebagai pekerjaan terpisah.
- Gacha gerakan: hanya "Pose Pohon" yang terspesifikasi penuh di Figma; pose lain memakai ilustrasi/langkah minimal — perlu konten resmi.
- Ikon kecil level (sprout/flower/bolt) memakai Material Icons mendekati; ikon navigasi & maskot memakai aset PNG asli dari Figma.

## 6. Struktur file
```
ui/theme/{Color,Type,Theme}.kt
ui/components/Components.kt (YmButton, YmTextField, AppHeader, SectionHeader, HistoryCard, StatChip, dsb)
ui/navigation/YogaMateApp.kt (NavHost + MainScaffold + bottom bar)
ui/feature/onboarding/OnboardingScreen.kt
ui/feature/auth/AuthScreens.kt
ui/feature/home/HomeScreen.kt
ui/feature/activity/ActivityScreen.kt
ui/feature/achievement/AchievementScreen.kt
ui/feature/profile/ProfileScreen.kt (+ Panduan sheet)
ui/feature/yoga/{SetupScreen,SessionScreen,CompleteScreen,DetailSheet}.kt
data/{Model.kt,AppSession.kt}
```

## 7. Status
- [x] Audit codebase
- [x] Inspeksi 18 screen via Figma API (screenshot + node data)
- [x] Aset maskot → PNG (instruksi #9)
- [x] Implementasi seluruh tahap (fondasi → auth → tabs → profil → yoga → riwayat)
- [x] Verifikasi kompilasi: `./gradlew assembleDebug` BUILD SUCCESSFUL
- [ ] Verifikasi visual perangkat/emulator terhadap screenshot Figma (butuh device — tidak dijalankan di sesi ini)

### Pemetaan 18 screen/state → kode
| Figma | Implementasi |
|---|---|
| ONB 1–3 (98:32/122/411) | `OnboardingScreen` — 1 screen, 3 halaman state internal |
| AUTH-01..03 (98:502/594/752) | `ChooseAuthScreen`, `RegisterScreen`, `LoginScreen` |
| HOME-01 (102:1444) | `HomeScreen` |
| YOGA-01..04 (112:3824–4213) | `SetupScreen` step 1–4 (satu route `yoga/setup`) |
| YOGA-05 (120:4712) | `SessionScreen` (placeholder kamera + timer + jeda) |
| YOGA-06 (117:4593) | `CompleteScreen` |
| HISTORY-01 (102:1763) | `ActivityScreen` (tab "Aktivitas", desain "Riwayat") |
| HISTORY-02 (108:3430) | `DetailSheet` — modal bottom sheet dari Beranda/Aktivitas |
| ACH-01 (102:2295) | `AchievementScreen` |
| PROFILE-01 (104:2837) | `ProfileScreen` |
| PROFILE-02 (108:3362) | `GuideSheet` — modal bottom sheet dari Profil |
