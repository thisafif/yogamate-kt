package com.viscit.yogamate.data

import android.content.Context

internal object DetectionPoseLoader {
    fun load(context: Context): List<DetectionPose> {
        val csv = context.assets.open("yoga82_difficulty_labels.csv").bufferedReader(Charsets.UTF_8).use { it.readText() }
        return parse(csv)
    }

    internal fun parse(csv: String): List<DetectionPose> {
        val rows = csv.removePrefix("\uFEFF").lineSequence().map(String::trimEnd).toList()
        require(rows.firstOrNull() == "class_id,pose_name,difficulty") { "Header metadata 82 kelas tidak sesuai" }
        val poses = rows.drop(1).filter(String::isNotBlank).map { row ->
            val values = row.split(',', limit = 3)
            require(values.size == 3)
            DetectionPose(values[0].toInt(), values[1], when(values[2]) {
                "Mudah" -> Level.PEMULA
                "Menengah" -> Level.MENENGAH
                "Sulit" -> Level.LANJUTAN
                else -> error("Level tidak dikenal: ${values[2]}")
            })
        }
        require(poses.size == 82 && poses.map { it.classId } == (0..81).toList())
        require(poses.map { it.name }.toSet().size == 82) { "Nama kelas metadata duplikat" }
        return poses
    }
}
