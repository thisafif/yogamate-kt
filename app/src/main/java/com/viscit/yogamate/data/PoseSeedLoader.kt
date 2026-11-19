package com.viscit.yogamate.data

import android.content.Context
import android.util.Log

private const val SEED_ASSET = "yoga_poses_seed.csv"
private const val TAG = "PoseSeedLoader"

internal object PoseSeedLoader {

    fun load(context: Context): List<Pose> = try {
        context.assets.open(SEED_ASSET).bufferedReader(Charsets.UTF_8).use { parse(it.readText()) }
    } catch (e: Exception) {
        Log.e(TAG, "Gagal memuat katalog pose dari asset", e)
        throw IllegalStateException("Katalog pose dari $SEED_ASSET tidak tersedia", e)
    }

    internal fun parse(csv: String): List<Pose> {
        val rows = parseCsv(csv.removePrefix("\uFEFF"))
        require(rows.isNotEmpty()) { "CSV seed kosong" }
        require(rows.first() == HEADER) { "Header CSV seed tidak sesuai: ${rows.first()}" }

        val poses = rows.drop(1).filter { row -> row.any { it.isNotBlank() } }.map { row ->
            require(row.size == HEADER.size) { "Jumlah kolom tidak sesuai: ${row.size}" }
            val labelId = row[0].toInt()
            val steps = parseSteps(row[9])
            require(steps.size >= 3) { "Langkah kurang dari tiga untuk label $labelId" }
            val level = when (row[6]) {
                "Mudah" -> Level.PEMULA
                "Menengah" -> Level.MENENGAH
                "Sulit" -> Level.LANJUTAN
                else -> error("Level tidak dikenal: ${row[6]}")
            }
            Pose(
                name = row[3],
                sanskrit = row[4],
                level = level,
                description = row[7],
                steps = steps,
                labelId = labelId,
                classIndex = row[1].toInt(),
                classLabel = row[2],
                slug = row[5],
                linkImg = row[8],
                audio = row[10],
            )
        }

        // Urutan dan keunikan mapping adalah kontrak dengan output classifier GCN.
        require(poses.size == 15) { "Seed harus berisi 15 pose, ditemukan ${poses.size}" }
        require(poses.map { it.labelId } == (0..14).toList()) { "label_id tidak berurutan 0..14" }
        require(poses.map { it.classIndex } == (0..14).toList()) { "class_index tidak berurutan 0..14" }
        require(poses.map { it.slug }.toSet().size == 15) { "Slug tidak unik" }
        return poses
    }

    /** Langkah seed selalu array of string sederhana, jadi tidak perlu library JSON. */
    private fun parseSteps(json: String): List<String> {
        val text = json.trim()
        require(text.startsWith("[") && text.endsWith("]")) { "instruksi_langkah bukan array: $json" }
        val items = mutableListOf<String>()
        val current = StringBuilder()
        var inString = false
        var escaped = false
        for (c in text.substring(1, text.length - 1)) {
            when {
                escaped -> {
                    current.append(when (c) { 'n' -> '\n'; 't' -> '\t'; else -> c })
                    escaped = false
                }
                c == '\\' -> escaped = true
                c == '"' -> {
                    if (inString) {
                        items += current.toString()
                        current.clear()
                    }
                    inString = !inString
                }
                inString -> current.append(c)
            }
        }
        require(items.isNotEmpty()) { "instruksi_langkah kosong" }
        return items
    }

    /** Parser CSV minimal: mengerti koma di dalam tanda kutip dan tanda kutip ganda. */
    private fun parseCsv(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val row = mutableListOf<String>()
        val field = StringBuilder()
        var quoted = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                quoted && c == '"' -> {
                    if (i + 1 < text.length && text[i + 1] == '"') {
                        field.append('"')
                        i++
                    } else {
                        quoted = false
                    }
                }
                c == '"' && field.isEmpty() -> quoted = true
                c == ',' && !quoted -> {
                    row += field.toString()
                    field.clear()
                }
                c == '\n' && !quoted -> {
                    row += field.toString().trimEnd('\r')
                    rows += row.toList()
                    row.clear()
                    field.clear()
                }
                else -> field.append(c)
            }
            i++
        }
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row += field.toString().trimEnd('\r')
            rows += row.toList()
        }
        return rows
    }

    private val HEADER = listOf(
        "label_id", "class_index", "class_label", "nama_pose", "nama_alt",
        "slug", "level", "description", "link_img", "instruksi_langkah", "audio",
    )
}
