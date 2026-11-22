package com.viscit.yogamate.data

import android.content.Context
import java.util.Calendar
import java.util.Locale

enum class Level(val label: String) {
    PEMULA("Pemula"), MENENGAH("Menengah"), LANJUTAN("Lanjutan")
}

data class Pose(
    val name: String,
    val sanskrit: String,
    val level: Level,
    val description: String,
    val steps: List<String>,
    // Mapping label/class yang dipakai output classifier GCN; -1 untuk pose lama non-model.
    val labelId: Int = -1,
    val classIndex: Int = -1,
    val classLabel: String = "",
    val slug: String = "",
    val linkImg: String = "",
    val audio: String = "",
    val modelClassId: Int = classIndex,
)

data class DetectionPose(val classId: Int, val name: String, val level: Level)

data class HistoryItem(
    val id: Long,
    val poseName: String,
    val level: Level,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val timeLabel: String,
    val dayLabel: String,
    val minutes: Int,
    val accuracy: Int,
) {
    val levelMinutesLabel: String get() = "${level.label}  ·  $minutes menit"
}

data class Achievement(
    val code: String,
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
    // Satu-satunya sumber katalog: 15 pose dari CSV seed. Tidak ada fallback hardcode.
    lateinit var ALL: List<Pose>
        private set
    lateinit var DETECTION: List<DetectionPose>
        private set
    lateinit var GACHA: List<Pose>
        private set

    /** Dipanggil dari AppSession.init, sebelum UI dibuat. Gagal keras bila seed tidak valid. */
    fun loadFrom(context: Context) {
        ALL = PoseSeedLoader.load(context)
        DETECTION = DetectionPoseLoader.load(context)
        val gachaClassIds = mapOf(
            "cobra-pose" to 10, "sitting-pose" to 57, "cat-cow-pose" to 7,
            "seated-forward-bend-pose" to 52, "reverse-warrior-pose" to 72,
            "extended-revolved-side-angle-pose" to 21, "extended-revolved-triangle-pose" to 22,
            "standing-big-toe-hold-pose" to 60, "lords-dance-pose" to 39, "boat-pose" to 2,
            "plow-pose" to 45, "crane-crow-pose" to 14, "supported-headstand-pose" to 63,
            "split-pose" to 58, "wheel-pose" to 69,
        )
        GACHA = ALL.map { pose ->
            pose.copy(modelClassId = gachaClassIds[pose.slug] ?: -1)
        }
        check(GACHA.all { it.modelClassId >= 0 }) { "Ada pose Gacha yang tidak terpetakan ke model 82 kelas" }
        check(GACHA.all { pose -> DETECTION.any { it.classId == pose.modelClassId } })
        check(GACHA.map { it.modelClassId }.toSet().size == GACHA.size) { "Mapping class ID Gacha duplikat" }
    }

    fun random(level: Level? = null): Pose {
        val pool = if (level == null) GACHA else GACHA.filter { it.level == level }.ifEmpty { GACHA }
        return pool.random()
    }
}

object Achievements {
    fun compute(history: List<HistoryItem>): List<Achievement> {
        val sessions = history.size
        val poses = history.map { it.poseName }.distinct().size
        val minutes = history.sumOf { it.minutes }
        return listOf(
            Achievement("first_session", "Langkah Pertama", "Selesaikan latihan pertamamu", AchievementIcon.SPROUT, sessions, 1, "sesi"),
            Achievement("three_sessions", "Mulai Mengalir", "Selesaikan 3 sesi latihan", AchievementIcon.FLOWER, sessions, 3, "sesi"),
            Achievement("seven_sessions", "Yoga Jadi Kebiasaan", "Selesaikan 7 sesi latihan", AchievementIcon.FIRE, sessions, 7, "sesi"),
            Achievement("ten_poses", "Penjelajah Pose", "Coba 10 pose yang berbeda", AchievementIcon.SPARKLE, poses, 10, "pose"),
            Achievement("sixty_minutes", "Waktu untuk Diri", "Berlatih selama 60 menit", AchievementIcon.HEART, minutes, 60, "menit"),
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
