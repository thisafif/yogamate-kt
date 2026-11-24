package com.viscit.yogamate.data.engine

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import java.nio.FloatBuffer

private const val MODEL_ASSET = "gcn_model.onnx"
private const val MODEL_DATA_ASSET = "gcn_model.onnx.data"
private const val INPUT_NAME = "node_features"
private const val LANDMARKS = 33
private const val FEATURES = 5
private const val EPSILON = 1e-6f

data class PosePrediction(val classIndex: Int, val confidence: Float)

/**
 * Classifier GCN 82 kelas. Output index mengikuti class_id pada yoga82_difficulty_labels.csv.
 *
 * Preprocessing wajib mengikuti notebook 07: koordinat direlatifkan terhadap titik
 * tengah pinggul lalu dibagi jarak shoulder-center ke hip-center (sumbu x/y saja).
 */
class PoseClassifier(context: Context) {
    private val env = OrtEnvironment.getEnvironment()
    private val modelFile = java.io.File(context.codeCacheDir, MODEL_ASSET)
    private val dataFile = java.io.File(context.codeCacheDir, MODEL_DATA_ASSET)
    init {
        context.assets.open(MODEL_ASSET).use { input -> modelFile.outputStream().use { output -> input.copyTo(output) } }
        context.assets.open(MODEL_DATA_ASSET).use { input -> dataFile.outputStream().use { output -> input.copyTo(output) } }
    }
    private val session = env.createSession(modelFile.absolutePath)

    /** null bila tubuh tidak valid untuk dinormalisasi (skala terlalu kecil/NaN). */
    fun classify(frame: PoseFrame): PosePrediction? {
        val features = PoseFeatures.build(frame) ?: return null
        val input = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), longArrayOf(1L, LANDMARKS.toLong(), FEATURES.toLong()))
        try {
            val result = session.run(mapOf(INPUT_NAME to input))
            try {
                val raw = result[0].value
                val row = when (raw) {
                    is Array<*> -> raw[0] as? FloatArray
                    is FloatArray -> raw
                    else -> null
                } ?: return null
                var best = 0
                for (i in 1 until row.size) if (row[i] > row[best]) best = i
                if (row.size != 82) return null

                return PosePrediction(best, softmaxConfidence(row))
            } finally {
                result.close()
            }
        } finally {
            input.close()
        }
    }

    fun close() {
        session.close()
    }

}

/** Probabilitas softmax dari logits; stabil terhadap nilai besar. */
internal fun softmaxConfidence(logits: FloatArray): Float {
    var best = 0
    for (i in 1 until logits.size) if (logits[i] > logits[best]) best = i
    var sumExp = 0.0
    for (value in logits) sumExp += kotlin.math.exp((value - logits[best]).toDouble())
    return (1.0 / sumExp).toFloat().coerceIn(0f, 1f)
}

/** Preprocessing yang sama persis dengan notebook 07 (tanpa ketergantungan ONNX). */
internal object PoseFeatures {
    fun build(frame: PoseFrame): FloatArray? {
        val xs = frame.xs
        val ys = frame.ys
        val zs = frame.zs
        if (xs.size != LANDMARKS || ys.size != LANDMARKS || zs.size != LANDMARKS) return null

        val shoulderX = (xs[11] + xs[12]) / 2f
        val shoulderY = (ys[11] + ys[12]) / 2f
        val hipX = (xs[23] + xs[24]) / 2f
        val hipY = (ys[23] + ys[24]) / 2f
        val hipZ = (zs[23] + zs[24]) / 2f

        val dx = shoulderX - hipX
        val dy = shoulderY - hipY
        val scale = kotlin.math.sqrt(dx * dx + dy * dy)
        if (!scale.isFinite() || scale < EPSILON) return null

        val features = FloatArray(LANDMARKS * FEATURES)
        for (i in 0 until LANDMARKS) {
            val nx = (xs[i] - hipX) / scale
            val ny = (ys[i] - hipY) / scale
            val nz = (zs[i] - hipZ) / scale
            if (!nx.isFinite() || !ny.isFinite() || !nz.isFinite()) return null
            val base = i * FEATURES
            features[base] = nx
            features[base + 1] = ny
            features[base + 2] = nz
            features[base + 3] = frame.visibility[i]
            features[base + 4] = frame.presence[i]
        }
        return features
    }
}
