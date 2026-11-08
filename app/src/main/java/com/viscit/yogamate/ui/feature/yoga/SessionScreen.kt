package com.viscit.yogamate.ui.feature.yoga

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmTopBar
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted
import kotlinx.coroutines.delay

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

    Column(Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(8.dp))
        YmTopBar("Waktu untuk dirimu", onBack = onBack, onClose = onClose)
        Spacer(Modifier.height(16.dp))
        Text(YogaSessionState.pose.name, style = MaterialTheme.typography.headlineLarge, color = Ink, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(50), color = Lime, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                "PASTIKAN SELURUH TUBUH TERLIHAT KAMERA",
                style = MaterialTheme.typography.labelSmall, color = Ink,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Surface(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (hasPermission) {
                CameraPreview(Modifier.fillMaxSize())
            } else {
                // Izin kamera ditolak — ketuk untuk minta izin lagi
                Box(
                    Modifier.fillMaxSize().background(Color(0xFF1A1A1A)).clickable { requestPermission.launch(Manifest.permission.CAMERA) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Izin kamera ditolak.\nKetuk untuk mengizinkan lagi.", style = MaterialTheme.typography.bodyMedium, color = Color.White, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        val progress = if (totalSec == 0) 0f else 1f - remaining.toFloat() / totalSec
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Lavender)) {
            Box(Modifier.fillMaxWidth(progress).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Ink))
        }
        Spacer(Modifier.height(16.dp))
        val m = remaining / 60
        val s = remaining % 60
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("%02d".format(m), style = MaterialTheme.typography.displayLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5f), color = Ink)
            Text(":", style = MaterialTheme.typography.displayLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5f), color = Lavender)
            Text("%02d".format(s), style = MaterialTheme.typography.displayLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5f), color = Ink)
        }
        Spacer(Modifier.height(24.dp))
        YmPrimaryButton(
            if (paused) "Lanjutkan latihan" else "Jeda sebentar",
            { paused = !paused },
            leadingIcon = {
                androidx.compose.material3.Icon(
                    if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    null, tint = Color.White, modifier = Modifier.height(20.dp),
                )
            },
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Akhiri tanpa menyimpan",
            style = MaterialTheme.typography.labelMedium, color = Muted,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().clickable { onClose() },
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CameraPreview(modifier: Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    var provider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    LaunchedEffect(Unit) {
        val p = ProcessCameraProvider.awaitInstance(previewView.context)
        provider = p
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        try {
            p.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview)
        } catch (e: IllegalArgumentException) {
            // Kamera depan tidak ada (mis. emulator) — pakai kamera belakang
            p.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
        }
    }
    DisposableEffect(Unit) { onDispose { provider?.unbindAll() } }
    AndroidView(factory = { previewView }, modifier = modifier)
}
