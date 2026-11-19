package com.viscit.yogamate.data.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PoseClassifierTest {

    @Test
    fun fitur_mengikuti_normalisasi_notebook_07() {
        val xs = FloatArray(33)
        val ys = FloatArray(33)
        val zs = FloatArray(33)
        val vis = FloatArray(33) { 1f }
        val presence = FloatArray(33) { 0.5f }

        // shoulder center = (0.6, 0.1), hip center = (0.6, 0.5), scale = 0.4
        xs[11] = 0.5f; ys[11] = 0.1f
        xs[12] = 0.7f; ys[12] = 0.1f
        xs[23] = 0.5f; ys[23] = 0.5f
        xs[24] = 0.7f; ys[24] = 0.5f
        // landmark 0 → (0, 1, 0.5) setelah normalisasi
        xs[0] = 0.6f; ys[0] = 0.9f; zs[0] = 0.2f
        vis[0] = 0.8f; presence[0] = 0.25f

        val features = PoseFeatures.build(
            PoseFrame(xs, ys, zs, vis, presence, 640, 480)
        )!!

        assertEquals(165, features.size)
        assertEquals(0f, features[0], 1e-6f)
        assertEquals(1f, features[1], 1e-6f)
        assertEquals(0.5f, features[2], 1e-6f)
        assertEquals(0.8f, features[3], 1e-6f)
        assertEquals(0.25f, features[4], 1e-6f)
    }

    @Test
    fun confidence_adalah_probabilitas_softmax() {
        // logit menang 2 vs 0 → softmax = e^2 / (e^2 + 2) ≈ 0.787
        val confidence = softmaxConfidence(floatArrayOf(0f, 2f, 0f))
        assertEquals(0.7869f, confidence, 0.001f)
        assertEquals(1f, softmaxConfidence(floatArrayOf(0f, 100f, 0f)), 0.001f)
    }

    @Test
    fun skala_tubuh_nol_ditolak() {
        val zeros = FloatArray(33)
        assertNull(
            PoseFeatures.build(
                PoseFrame(zeros, zeros.copyOf(), zeros.copyOf(), zeros.copyOf(), zeros.copyOf(), 640, 480)
            )
        )
    }
}
