package com.viscit.yogamate.ui.feature.yoga

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.viscit.yogamate.R
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.data.HistoryItem
import com.viscit.yogamate.data.TimeUtil
import com.viscit.yogamate.data.YogaSessionState
import com.viscit.yogamate.data.engine.POSE_CONNECTIONS
import com.viscit.yogamate.data.engine.PoseEngine
import com.viscit.yogamate.data.engine.PoseFrame
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.theme.Lime
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import kotlin.math.max

private const val TAG = "SessionScreen"

// YOGA-05 Melakukan Yoga (Figma 120:4712). Preview kamera depan via CameraX;
// akurasi tetap disimulasikan (ML pose detection = pekerjaan terpisah).
@Composable
fun SessionScreen(onBack: () -> Unit, onClose: () -> Unit, onComplete: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }
    LaunchedEffect(Unit) { if (!hasPermission) requestPermission.launch(Manifest.permission.CAMERA) }

    val totalSec = YogaSessionState.durationMin * 60
    var remaining by remember { mutableIntStateOf(totalSec) }
    var paused by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(paused, finished) {
        while (!paused && !finished && remaining > 0) {
            delay(1000)
            remaining--
        }
        if (remaining == 0 && !finished) {
            finished = true
            // Akurasi disimulasikan (belum ada ML pose detection)
            YogaSessionState.resultAccuracy = (75..95).random()
            AppSession.addHistory(
                HistoryItem(
                    id = System.currentTimeMillis(),
                    poseName = YogaSessionState.pose.name,
                    level = YogaSessionState.pose.level,
                    dayOfMonth = TimeUtil.todayParts().first,
                    month = TimeUtil.todayParts().second,
                    year = TimeUtil.todayParts().third,
                    timeLabel = TimeUtil.currentTimeLabel(),
                    dayLabel = TimeUtil.dateLabel(
                        TimeUtil.todayParts().first, TimeUtil.todayParts().second, TimeUtil.todayParts().third,
                        TimeUtil.todayParts(),
                    ),
                    minutes = YogaSessionState.durationMin,
                    accuracy = YogaSessionState.resultAccuracy,
                ),
            )
            onComplete()
        }
    }

    // State kamera/ML di-hoist agar chip panduan bisa dirender di alur overlay (tidak menimpa chip lain).
    val cameraState = remember { mutableStateOf(CameraState.LOADING) }
    val poseDetected = remember { mutableStateOf<Boolean?>(null) }
    val mlAvailable = remember { mutableStateOf(true) }

    // Kamera full-screen; semua UI jadi overlay chip transparan gelap agar kamera paling dominan.
    Box(Modifier.fillMaxSize()) {
        if (hasPermission) {
            CameraPreview(Modifier.fillMaxSize(), cameraState, poseDetected, mlAvailable)
        } else {
            // Izin kamera ditolak — ketuk untuk minta izin lagi
            Box(
                Modifier.fillMaxSize().background(Color(0xFF1A1A1A)).clickable { requestPermission.launch(Manifest.permission.CAMERA) },
                contentAlignment = Alignment.Center,
            ) {
                Text("Izin kamera ditolak.\nKetuk untuk mengizinkan lagi.", style = MaterialTheme.typography.bodyMedium, color = Color.White, textAlign = TextAlign.Center)
            }
        }
        Column(Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OverlayCircleButton(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", onBack)
                Spacer(Modifier.weight(1f))
                OverlayChip { Text(YogaSessionState.pose.name, style = MaterialTheme.typography.titleMedium, color = Color.White) }
                Spacer(Modifier.weight(1f))
                OverlayCircleButton(Icons.Default.Close, "Tutup", onClose)
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                OverlayChip {
                    Text(
                        "PASTIKAN SELURUH TUBUH TERLIHAT KAMERA",
                        style = MaterialTheme.typography.labelSmall, color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    )
                }
            }
            // Panduan kondisi: kamera menyiapkan / pose tidak terdeteksi / error.
            val hint = when {
                cameraState.value == CameraState.ERROR -> "Kamera bermasalah. Coba tutup dan buka lagi."
                cameraState.value == CameraState.LOADING -> "Menyiapkan kamera…"
                !mlAvailable.value -> "Deteksi pose tidak didukung pada perangkat ini."
                poseDetected.value == null -> "Mendeteksi pose…"
                poseDetected.value == false -> "Pose tidak terdeteksi — pastikan seluruh tubuh terlihat di kamera."
                else -> null
            }
            hint?.let {
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(50), color = Color.Black.copy(alpha = 0.55f), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = Color.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            val remainingNow = remaining
            val progress = if (totalSec == 0) 0f else 1f - remainingNow.toFloat() / totalSec
            Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.3f))) {
                Box(Modifier.fillMaxWidth(progress).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Lime))
            }
            Spacer(Modifier.height(12.dp))
            val m = remainingNow / 60
            val s = remainingNow % 60
            val timerStyle = MaterialTheme.typography.displayLarge
                .copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.2f, shadow = Shadow(Color.Black.copy(alpha = 0.6f), blurRadius = 16f))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("%02d".format(m), style = timerStyle, color = Color.White)
                Text(":", style = timerStyle, color = Color.White.copy(alpha = 0.7f))
                Text("%02d".format(s), style = timerStyle, color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            YmPrimaryButton(
                if (paused) "Lanjutkan latihan" else "Jeda sebentar",
                { paused = !paused },
                container = Color.Black.copy(alpha = 0.55f),
                contentColor = Color.White,
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        null, tint = Color.White, modifier = Modifier.height(20.dp),
                    )
                },
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Akhiri tanpa menyimpan",
                style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().clickable { onClose() },
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Chip gelap transparan di atas kamera.
@Composable
private fun OverlayChip(content: @Composable () -> Unit) {
    Surface(shape = RoundedCornerShape(50), color = Color.Black.copy(alpha = 0.55f)) {
        Box(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) { content() }
    }
}

@Composable
private fun OverlayCircleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDesc: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(44.dp)
            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
            .clickable(onClick = onClick),
    ) {
        Icon(icon, contentDesc, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

// State kamera untuk UI: menyiapkan kamera, siap, atau error.
private enum class CameraState { LOADING, READY, ERROR }

@Composable
private fun CameraPreview(
    modifier: Modifier,
    cameraState: MutableState<CameraState>,
    poseDetected: MutableState<Boolean?>,
    mlAvailable: MutableState<Boolean>,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    var provider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var mirrored by remember { mutableStateOf(true) }

    // Hasil deteksi tiap frame dibaca HANYA di dalam Canvas (invalidasi draw-phase saja,
    // tanpa recomposition); status terdeteksi/tidak dipisah agar UI tidak recompose tiap frame.
    val poseFrame = remember { mutableStateOf<PoseFrame?>(null) }
    val poseEngine = remember {
        PoseEngine { frame ->
            poseFrame.value = frame
            val detected = frame?.hasBody() == true
            if (detected != poseDetected.value) poseDetected.value = detected
        }
    }
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        try {
            val p = ProcessCameraProvider.awaitInstance(previewView.context)
            provider = p
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            // Analisis frame: hanya frame terbaru (tanpa penumpukan), RGBA untuk MediaPipe.
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { a ->
                    a.setAnalyzer(analyzerExecutor) { proxy ->
                        try {
                            poseEngine.toUprightBitmap(proxy)?.let {
                                // timestamp kamera (nanos) → ms, monotonik untuk LIVE_STREAM
                                poseEngine.detect(it, proxy.imageInfo.timestamp / 1_000_000L)
                            }
                        } catch (t: Throwable) {
                            Log.e(TAG, "Gagal memproses frame", t)
                        } finally {
                            proxy.close()
                        }
                    }
                }
            try {
                p.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis)
                mirrored = true
            } catch (e: IllegalArgumentException) {
                // Kamera depan tidak ada (mis. emulator) — pakai kamera belakang
                p.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                mirrored = false
            }
            cameraState.value = CameraState.READY
            mlAvailable.value = poseEngine.start(context)
        } catch (e: Exception) {
            Log.e(TAG, "Kamera gagal diinisialisasi", e)
            cameraState.value = CameraState.ERROR
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            provider?.unbindAll()
            analyzerExecutor.shutdown()
            poseEngine.close()
        }
    }

    Box(modifier) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        // Overlay skeleton: gambar ulang hanya saat frame deteksi baru datang.
        Canvas(Modifier.fillMaxSize()) {
            val frame = poseFrame.value ?: return@Canvas
            val scale = max(size.width / frame.imageWidth, size.height / frame.imageHeight)
            val dx = (size.width - frame.imageWidth * scale) / 2f
            val dy = (size.height - frame.imageHeight * scale) / 2f
            fun point(i: Int): Offset {
                val x = dx + frame.xs[i] * frame.imageWidth * scale
                val y = dy + frame.ys[i] * frame.imageHeight * scale
                return Offset(if (mirrored) size.width - x else x, y)
            }
            POSE_CONNECTIONS.forEach { (a, b) ->
                if (frame.visibility[a] >= PoseFrame.VISIBILITY_THRESHOLD && frame.visibility[b] >= PoseFrame.VISIBILITY_THRESHOLD) {
                    drawLine(
                        Color.White.copy(alpha = 0.9f), point(a), point(b),
                        strokeWidth = 5f, cap = StrokeCap.Round,
                    )
                }
            }
            frame.visibility.forEachIndexed { i, v ->
                if (v >= PoseFrame.VISIBILITY_THRESHOLD) {
                    drawCircle(Lime, radius = 7f, center = point(i))
                }
            }
        }
    }
}
