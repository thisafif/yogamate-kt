package com.viscit.yogamate.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Downloads pose media once and keeps it in the app cache for later sessions. */
object PoseMediaCache {
    suspend fun downloadAll(context: Context, poses: List<Pose>, onProgress: (Int, Int) -> Unit = { _, _ -> }): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val media = poses.flatMap { pose -> listOf(pose.linkImg, pose.audio) }
                    .filter { it.startsWith("https://") || it.startsWith("http://") }
                    .distinct()
                val dir = File(context.cacheDir, "pose-media").apply { mkdirs() }
                media.forEachIndexed { index, uri ->
                    val target = File(dir, uri.substringAfterLast('/').substringBefore('?').ifBlank { "media-$index" })
                    if (!target.exists() || target.length() == 0L) {
                        val connection = (URL(uri).openConnection() as HttpURLConnection).apply {
                            connectTimeout = 15_000
                            readTimeout = 30_000
                        }
                        try {
                            connection.inputStream.use { input -> target.outputStream().use(input::copyTo) }
                        } finally { connection.disconnect() }
                    }
                    onProgress(index + 1, media.size)
                }
            }
        }

    fun cachedFile(context: Context, uri: String): File? {
        if (!uri.startsWith("https://") && !uri.startsWith("http://")) return null
        return File(File(context.cacheDir, "pose-media"), uri.substringAfterLast('/').substringBefore('?'))
            .takeIf { it.exists() && it.length() > 0L }
    }
}
