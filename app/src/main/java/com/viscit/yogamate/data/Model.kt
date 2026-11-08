package com.viscit.yogamate.data

import java.util.Calendar
import java.util.Locale

// Model data minimal untuk UI; sumber data riwayat: penyimpanan lokal (lihat AppSession).
// Belum ada kontrak API/ML di codebase — akurasi sesi disimulasikan (lihat plan.md).

enum class Level(val label: String) {
    PEMULA("Pemula"), MENENGAH("Menengah"), LANJUTAN("Lanjutan")
}

data class Pose(
    val name: String,
    val sanskrit: String,
    val level: Level,
    val description: String,
    val steps: List<String>,
)

data class HistoryItem(
    val id: Long,
    val poseName: String,
    val level: Level,
    val dayOfMonth: Int,
    val month: Int, // 1..12
    val year: Int,
    val timeLabel: String, // "07.30"
    val dayLabel: String,  // "Hari ini" / "5 Okt"
    val minutes: Int,
    val accuracy: Int,     // persen
) {
    val levelMinutesLabel: String get() = "${level.label}  ·  $minutes menit"
}

data class Achievement(
    val title: String,
    val subtitle: String,
    val icon: AchievementIcon,
    val progress: Int,
    val target: Int,
    val unit: String,
) {
    val achieved: Boolean get() = progress >= target
}

enum class AchievementIcon { SPROUT, FLOWER, FIRE, SPARKLE, HEART }

object Poses {
    // Pose Pohon lengkap sesuai Figma (112:4086). Pose lain konten minimal (Need Confirmation di plan.md).
    val POSE_POHON = Pose(
        name = "Pose Pohon",
        sanskrit = "Vrksasana",
        level = Level.PEMULA,
        description = "Temukan keseimbanganmu, satu napas dalam satu waktu.",
        steps = listOf(
            "Berdiri tegak, pindahkan berat badan ke kaki kiri.",
            "Letakkan telapak kaki kanan di betis atau paha kiri, bukan pada lutut.",
            "Satukan telapak tangan di depan dada. Bernapas perlahan.",
        ),
    )
    val ALL = listOf(
        POSE_POHON,
        Pose("Pose Gunung", "Tadasana", Level.PEMULA, "Berdiri tegak dengan tenang dan stabil.", listOf("Berdiri dengan tumit menyatu.", "Relaksasikan bahu, tarik napas.", "Tahan dengan nyaman.")),
        Pose("Pose Anak", "Balasana", Level.PEMULA, "Jeda untuk kembali tenang.", listOf("Duduk di atas tumit.", "Condongkan badan ke depan.", "Tarik napas perlahan.")),
        Pose("Pose Kucing", "Marjaryasana", Level.PEMULA, "Lenturkan punggung dengan lembut.", listOf("Posisi merangkak.", "Lengkungkan punggung ke atas dan bawah.", "Ikuti ritme napas.")),
    )
    fun random(): Pose = ALL.random()
}

object Achievements {
    fun compute(history: List<HistoryItem>): List<Achievement> {
        val sessions = history.size
        val poses = history.map { it.poseName }.distinct().size
        val minutes = history.sumOf { it.minutes }
        return listOf(
            Achievement("Langkah Pertama", "Selesaikan latihan pertamamu", AchievementIcon.SPROUT, sessions, 1, "sesi"),
            Achievement("Mulai Mengalir", "Selesaikan 3 sesi latihan", AchievementIcon.FLOWER, sessions, 3, "sesi"),
            Achievement("Yoga Jadi Kebiasaan", "Selesaikan 7 sesi latihan", AchievementIcon.FIRE, sessions, 7, "sesi"),
            Achievement("Penjelajah Pose", "Coba 10 pose yang berbeda", AchievementIcon.SPARKLE, poses, 10, "pose"),
            Achievement("Waktu untuk Diri", "Berlatih selama 60 menit", AchievementIcon.HEART, minutes, 60, "menit"),
        )
    }
}

object TimeUtil {
    fun todayParts(): Triple<Int, Int, Int> {
        val c = Calendar.getInstance()
        return Triple(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
    }

    fun monthName(month: Int): String = when (month) {
        1 -> "Januari"; 2 -> "Februari"; 3 -> "Maret"; 4 -> "April"; 5 -> "Mei"; 6 -> "Juni"
        7 -> "Juli"; 8 -> "Agustus"; 9 -> "September"; 10 -> "Oktober"; 11 -> "November"; else -> "Desember"
    }

    fun daysInMonth(month: Int, year: Int): Int {
        val c = Calendar.getInstance()
        c.set(year, month - 1, 1)
        return c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun firstDayOfWeek(month: Int, year: Int): Int {
        val c = Calendar.getInstance()
        c.set(year, month - 1, 1)
        // Senin=1..Minggu=7 sesuai desain (Sen..Min)
        return ((c.get(Calendar.DAY_OF_WEEK) + 5) % 7) + 1
    }

    fun currentTimeLabel(): String {
        val c = Calendar.getInstance()
        return String.format(Locale.US, "%02d.%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
    }

    fun dateLabel(day: Int, month: Int, year: Int, today: Triple<Int, Int, Int>): String =
        if (day == today.first && month == today.second && year == today.third) "Hari ini"
        else "$day ${monthName(month).take(3)}"
}
