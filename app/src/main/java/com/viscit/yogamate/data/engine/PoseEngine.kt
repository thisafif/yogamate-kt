package com.viscit.yogamate.data.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker

/**
 * Satu frame hasil deteksi Pose Landmarker: 33 landmark ternormalisasi (0..1) yang sudah
 * dirotasi ke orientasi tegak, plus dimensi gambar tegak — overlay di UI memetakannya ke
 * layar dengan transformasi FILL_CENTER (+ mirror untuk kamera depan).
 */
class PoseFrame(
    val xs: FloatArray,
    val ys: FloatArray,
    val visibility: FloatArray,
    val imageWidth: Int,
    val imageHeight: Int,
) {
    /** Tubuh dianggap terdeteksi bila minimal satu landmark cukup yakin. */
    fun hasBody(minVisibility: Float = VISIBILITY_THRESHOLD): Boolean = visibility.any { it >= minVisibility }

    companion object {
        const val VISIBILITY_THRESHOLD = 0.5f
        const val LANDMARK_COUNT = 33
    }
}

/**
 * Topologi skeletal MediaPipe Pose (indeks landmark 0..33) — garis hanya digambar
 * bila kedua ujungnya valid.
 */
val POSE_CONNECTIONS: List<Pair<Int, Int>> = listOf(
    0 to 1, 1 to 2, 2 to 3, 3 to 7,          // kepala/wajah
    0 to 4, 4 to 5, 5 to 6, 7 to 8,          // mata-telinga
    9 to 10,                                  // mulut
    11 to 12,                                 // bahu
    11 to 13, 13 to 15, 15 to 17, 15 to 19, 15 to 21, 17 to 19, // lengan kiri
    12 to 14, 14 to 16, 16 to 18, 16 to 20, 16 to 22, 18 to 20, // lengan kanan
    11 to 23, 12 to 24, 23 to 24,             // torso
    23 to 25, 25 to 27, 27 to 29, 29 to 31, 27 to 31, // kaki kiri
    24 to 26, 26 to 28, 28 to 30, 30 to 32, 28 to 32, // kaki kanan
)

/**
 * Pembungkus MediaPipe Pose Landmarker (model Full, LIVE_STREAM) untuk YogaMate.
 * Inference berjalan di thread internal MediaPipe (bukan main thread); hasil dikirim ke
 * main thread lewat callback [onFrame] — null berarti tidak ada tubuh terdeteksi.
 * Terpisah dari UI dan dari klasifikasi GCN (yang menyusul, section 9.10+).
 */
class PoseEngine(private val onFrame: (PoseFrame?) -> Unit) {

    private var landmarker: PoseLandmarker? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    /** Inisialisasi detector. False bila tidak tersedia (mis. ABI tanpa lib native). */
    fun start(context: Context): Boolean {
        if (landmarker != null) return true
        // GPU bila tersedia, fallback CPU.
        landmarker = try {
            create(
                context,
                BaseOptions.builder().setModelAssetPath(MODEL_ASSET).setDelegate(Delegate.GPU).build(),
            )
        } catch (t: Throwable) {
            Log.w(TAG, "GPU delegate gagal, fallback ke CPU", t)
            try {
                create(
                    context,
                    BaseOptions.builder().setModelAssetPath(MODEL_ASSET).setDelegate(Delegate.CPU).build(),
                )
            } catch (t2: Throwable) {
                // Bisa terjadi di ABI tanpa lib native (mis. emulator x86_64) —
                // jangan crash; UI menampilkan status error.
                Log.e(TAG, "MediaPipe tidak tersedia", t2)
                null
            }
        }
        return landmarker != null
    }

    private fun create(context: Context, base: BaseOptions): PoseLandmarker {
        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(base)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumPoses(1)
            .setMinPoseDetectionConfidence(0.5f)
            .setMinPosePresenceConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setResultListener { result, input ->
                val frame = result.landmarks().firstOrNull()?.takeIf { it.size == PoseFrame.LANDMARK_COUNT }?.let { lm ->
                    PoseFrame(
                        xs = FloatArray(lm.size) { lm[it].x() },
                        ys = FloatArray(lm.size) { lm[it].y() },
                        visibility = FloatArray(lm.size) { lm[it].visibility().orElse(1f) },
                        imageWidth = input.width,
                        imageHeight = input.height,
                    )
                }
                mainHandler.post { onFrame(frame) }
            }
            .setErrorListener { error ->
                Log.e(TAG, "MediaPipe error", error)
                mainHandler.post { onFrame(null) }
            }
            .build()
        return PoseLandmarker.createFromOptions(context, options)
    }

    /**
     * Kirim satu frame bitmap (sudah tegak) untuk deteksi asinkron. Aman dipanggil dari
     * thread analyzer; frame dilewatkan bila engine belum siap atau antrean penuh.
     */
    fun detect(bitmap: Bitmap, timestampMs: Long) {
        val l = landmarker ?: return
        try {
            l.detectAsync(BitmapImageBuilder(bitmap).build(), timestampMs)
        } catch (e: IllegalStateException) {
            // Antrean LIVE_STREAM penuh — buang frame ini, ambil frame berikutnya.
            Log.d(TAG, "Frame dilewati: ${e.message}")
        }
    }

    /** Lepaskan resource MediaPipe. Panggil saat layar sesi ditutup. */
    fun close() {
        landmarker?.close()
        landmarker = null
    }

    /**
     * Konversi ImageProxy (RGBA_8888, orientasi sensor) menjadi Bitmap tegak sesuai
     * rotasi display — koordinat landmark lalu langsung sejajar dengan preview.
     */
    fun toUprightBitmap(proxy: ImageProxy): Bitmap? {
        val src = Bitmap.createBitmap(proxy.width, proxy.height, Bitmap.Config.ARGB_8888)
        proxy.planes[0].buffer.rewind()
        src.copyPixelsFromBuffer(proxy.planes[0].buffer)
        val rotation = proxy.imageInfo.rotationDegrees
        if (rotation == 0) return src
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(src, 0, 0, proxy.width, proxy.height, matrix, false)
    }

    companion object {
        private const val TAG = "PoseEngine"
        private const val MODEL_ASSET = "pose_landmarker_full.task"
    }
}
