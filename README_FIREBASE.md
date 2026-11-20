# Menyiapkan backend YogaMate dengan Firebase

Aplikasi memakai Firebase Authentication untuk email/kata sandi dan Cloud Firestore untuk profil serta riwayat latihan yang tersinkron per akun. Dengan demikian, akun yang sama dapat masuk dari perangkat lain. Password dikelola Firebase Authentication dan tidak disimpan aplikasi.

Integrasi Firebase untuk login, registrasi, profil, logout, dan riwayat sudah disambungkan di Android. Master pose dan konten edukasi saat ini masih berasal dari data lokal aplikasi; classifier 15 kelas juga belum terhubung ke UI sesi. Aturan Firestore membatasi akses profil/riwayat berdasarkan UID Firebase.

## Langkah setup project Firebase

1. Buka [Firebase Console](https://console.firebase.google.com/) dengan akun Google-mu, lalu pilih **Create a project**.
2. Beri nama project, ikuti wizard, lalu buat project. Analytics boleh dimatikan untuk tahap ini.
3. Di halaman project, tekan **Add app → Android**.
4. Masukkan Android package name persis `com.kel9.yogamate`. Cek nilainya di `app/build.gradle.kts` pada `applicationId`.
5. Unduh file `google-services.json` dari Firebase Console. Letakkan langsung di `app/google-services.json` (jangan ubah nama file). File ini diabaikan Git karena khusus konfigurasi project-mu.
6. Di Firebase Console, buka **Authentication → Get started → Sign-in method**, aktifkan **Email/Password**, lalu simpan.
7. Buka **Firestore Database → Create database**. Pilih lokasi yang dekat dengan pengguna (misalnya lokasi Asia yang tersedia) dan mulai dalam **production mode**.
8. Buka **Firestore Database → Rules**, salin isi file `firestore.rules`, lalu tekan **Publish**. Jangan gunakan aturan `allow read, write: if true`.
9. Di Android Studio tekan **Sync Project with Gradle Files**, lalu jalankan app pada emulator/perangkat dengan internet.

## Tes dua perangkat

1. Di perangkat pertama, daftar dengan email dan kata sandi.
2. Selesaikan satu latihan. Riwayat akan ditulis ke `users/{UID}/history` di Firestore.
3. Di perangkat kedua, install build yang memakai file konfigurasi Firebase dari project yang sama.
4. Masuk memakai email dan kata sandi yang sama. Profil dan riwayat akun dimuat dari Firestore.

Di Firebase Console, data profil muncul di `users/{UID}` dan riwayat di subcollection `history`. Tiap perangkat harus terhubung internet untuk sinkronisasi; UI menampilkan data Firestore setelah listener menerima pembaruan.

## Bila Gradle gagal

- Pastikan file berada tepat di `app/google-services.json`, bukan root repo atau folder `app/src`.
- Pastikan package name aplikasi Firebase sama persis: `com.kel9.yogamate`.
- Pastikan internet aktif saat Gradle mengunduh Firebase SDK.
- Di Firebase Console, pastikan Email/Password aktif dan database Firestore sudah dibuat.
- Jika konfigurasi diunduh sebelum package name diperbaiki, unduh ulang `google-services.json`.

## Data versi UI saat ini

- Authentication email/password: Firebase Auth.
- Profil: `users/{uid}` dengan `name`, `email`, `goal`.
- Riwayat: `users/{uid}/history/{entryId}`.
- Progres achievement dihitung dari riwayat lalu disimpan per akun di `users/{uid}/achievements/{code}`.
- Data empat pose dan konten edukasi masih ditanam di aplikasi. Data tersebut belum dikelola dari Firestore.
- Akurasi hasil latihan di layar saat ini masih simulasi; MediaPipe yang ada mendeteksi landmark, bukan mengklasifikasikan pose ke 15 class index.

Kalau project Firebase dan file konfigurasi sudah kamu buat, build Android dapat diuji dengan project tersebut. Jangan kirim password akun, `google-services.json`, atau credential admin ke chat.
