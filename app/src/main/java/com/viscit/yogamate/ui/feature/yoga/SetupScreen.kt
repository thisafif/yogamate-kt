package com.viscit.yogamate.ui.feature.yoga

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.Level
import com.viscit.yogamate.data.Poses
import com.viscit.yogamate.data.YogaSessionState
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.ProgressSegments
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmTopBar
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.LavenderSoft
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft

// YOGA-01..04 Mulai Yoga (Figma 112:3824/3943/4086/4213): satu route, 4 step internal (usulan plan §6.3).
@Composable
fun SetupScreen(onBack: () -> Unit, onClose: () -> Unit, onStartSession: () -> Unit) {
    val step = YogaSessionState.step
    Column(
        Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        YmTopBar("Mulai perjalananmu", onBack = onBack, onClose = onClose)
        Spacer(Modifier.height(16.dp))
        ProgressSegments(step)
        Spacer(Modifier.height(24.dp))
        when (step) {
            1 -> StepLevel()
            2 -> StepGacha()
            3 -> StepPose()
            else -> StepFour(onStartSession)
        }
        Spacer(Modifier.weight(1f))
        when (step) {
            1 -> YmPrimaryButton("Lanjutkan", { YogaSessionState.step = 2 }, trailingIcon = { ArrowForwardIcon() })
            2 -> YmPrimaryButton("Temukan poseku", {
                YogaSessionState.pose = Poses.random()
                YogaSessionState.step = 3
            }, leadingIcon = { Icon(Icons.Default.Shuffle, null, tint = Color.White, modifier = Modifier.size(20.dp)) })
            3 -> YmPrimaryButton("Pilih durasi", { YogaSessionState.step = 4 }, trailingIcon = { ArrowForwardIcon() })
            else -> YmPrimaryButton("Mulai latihan", onStartSession, leadingIcon = { Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp)) })
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StepLevel() {
    Text("01 · PILIH LEVEL", style = MaterialTheme.typography.labelSmall, color = Muted)
    Spacer(Modifier.height(8.dp))
    Text("Mulai dari\nyang nyaman.", style = MaterialTheme.typography.displayLarge, color = Ink)
    Spacer(Modifier.height(8.dp))
    Text("Pilih level yang sesuai dengan pengalamanmu.", style = MaterialTheme.typography.bodyLarge, color = Muted)
    Spacer(Modifier.height(24.dp))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf(
            Triple(Level.PEMULA, Icons.Default.Eco, "MULAI DI SINI"),
            Triple(Level.MENENGAH, Icons.Default.LocalFlorist, null),
            Triple(Level.LANJUTAN, Icons.Default.Bolt, null),
        ).forEach { (level, icon, chip) ->
            val selected = YogaSessionState.level == level
            Surface(
                onClick = { YogaSessionState.level = level },
                shape = RoundedCornerShape(20.dp),
                color = if (selected) LavenderLight else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Ink else OutlineSoft),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Icon(icon, null, tint = Ink, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.size(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(level.label, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text(levelSubtitle(level), style = MaterialTheme.typography.bodyMedium, color = Muted)
                        if (chip != null) {
                            Spacer(Modifier.height(8.dp))
                            Surface(shape = RoundedCornerShape(50), color = Lime) {
                                Text(chip, style = MaterialTheme.typography.labelSmall, color = Ink, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (selected) Ink else Color.Transparent)
                            .border(1.dp, if (selected) Ink else OutlineSoft, CircleShape),
                    ) {
                        if (selected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

private fun levelSubtitle(level: Level): String = when (level) {
    Level.PEMULA -> "Kenalan dengan yoga, pelan–pelan."
    Level.MENENGAH -> "Siap melangkah sedikit lebih jauh?"
    Level.LANJUTAN -> "Eksplorasi kekuatan dan keseimbangan."
}

@Composable
private fun StepGacha() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("02 · GACHA GERAKAN", style = MaterialTheme.typography.labelSmall, color = Muted)
        Spacer(Modifier.height(8.dp))
        Text("Ada kejutan untukmu.", style = MaterialTheme.typography.displayLarge, color = Ink, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Biarkan kami memilih pose dari level ${YogaSessionState.level.label.lowercase()}.\nKenalan dengan gerakan baru, yuk!",
            style = MaterialTheme.typography.bodyLarge, color = Muted, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = Lavender,
                        modifier = Modifier.size(260.dp).rotate(-6f),
                    ) {}
                    Image(painterResource(R.drawable.mascot_04_semangat), null, Modifier.size(200.dp).align(Alignment.Center))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.TopEnd)
                            .background(Lime, CircleShape),
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Ink, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(50), color = Ink, modifier = Modifier.rotate(-6f)) {
                    Text(
                        "Ruang untuk eksplorasi",
                        style = MaterialTheme.typography.labelMedium, color = Color.White,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Surface(shape = RoundedCornerShape(50), color = Color.Transparent, border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Icon(Icons.Default.Eco, null, tint = Ink, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Level ${YogaSessionState.level.label}", style = MaterialTheme.typography.labelMedium, color = Ink)
            }
        }
    }
}

@Composable
private fun StepPose() {
    val pose = YogaSessionState.pose
    Text("03 · KENALI POSEMU", style = MaterialTheme.typography.labelSmall, color = Muted)
    Spacer(Modifier.height(12.dp))
    Box {
        Surface(shape = RoundedCornerShape(24.dp), color = LavenderSoft, modifier = Modifier.fillMaxWidth()) {
            Image(
                painterResource(R.drawable.ilustrasi_pose),
                "Ilustrasi ${pose.name}",
                modifier = Modifier.fillMaxWidth().height(220.dp).padding(20.dp),
            )
        }
        Surface(shape = RoundedCornerShape(50), color = Color.White, modifier = Modifier.align(Alignment.TopStart).padding(12.dp)) {
            Text(pose.level.label, style = MaterialTheme.typography.labelMedium, color = Ink, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(Lime, CircleShape),
        ) {
            Icon(Icons.Default.AutoAwesome, null, tint = Ink, modifier = Modifier.size(22.dp))
        }
    }
    Spacer(Modifier.height(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(pose.name, style = MaterialTheme.typography.displayMedium, color = Ink)
            Text(pose.sanskrit, style = MaterialTheme.typography.bodyMedium, color = Muted)
        }
        Surface(
            onClick = { YogaSessionState.pose = Poses.random() },
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft),
            modifier = Modifier.size(48.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Refresh, "Ganti pose", tint = Ink, modifier = Modifier.size(22.dp))
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    Text(pose.description, style = MaterialTheme.typography.bodyLarge, color = Muted)
    Spacer(Modifier.height(16.dp))
    Text("Begini caranya", style = MaterialTheme.typography.titleLarge, color = Ink)
    Spacer(Modifier.height(12.dp))
    pose.steps.forEachIndexed { i, s ->
        Row(modifier = Modifier.padding(bottom = 12.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp).background(Lime, CircleShape)) {
                Text("${i + 1}", style = MaterialTheme.typography.labelMedium, color = Ink)
            }
            Spacer(Modifier.size(12.dp))
            Text(s, style = MaterialTheme.typography.bodyMedium, color = Muted, modifier = Modifier.weight(1f))
        }
    }
}

// Step 4: durasi + persiapan ruang + tombol mulai (dari SetupScreen when).

@Composable
private fun StepFour(onStartSession: () -> Unit) {
    Text("04 · ATUR WAKTUMU", style = MaterialTheme.typography.labelSmall, color = Muted)
    Spacer(Modifier.height(8.dp))
    Text("Sebentar juga\nberarti.", style = MaterialTheme.typography.displayLarge, color = Ink)
    Spacer(Modifier.height(8.dp))
    Text("Dengarkan tubuhmu, pilih durasi yang nyaman.", style = MaterialTheme.typography.bodyLarge, color = Muted)
    Spacer(Modifier.height(24.dp))
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
        Text("Durasi latihan", style = MaterialTheme.typography.titleMedium, color = Ink, modifier = Modifier.weight(1f))
        Text("${YogaSessionState.durationMin}", style = MaterialTheme.typography.displayMedium, color = Ink)
        Text(" menit", style = MaterialTheme.typography.bodyLarge, color = Muted)
    }
    Spacer(Modifier.height(8.dp))
    val options = listOf(1, 2, 3, 5)
    var sliderValue by remember { mutableFloatStateOf(options.indexOf(YogaSessionState.durationMin).coerceAtLeast(0).toFloat()) }
    Slider(
        value = sliderValue,
        onValueChange = {
            sliderValue = it
            YogaSessionState.durationMin = options[it.toInt().coerceIn(0, options.lastIndex)]
        },
        valueRange = 0f..(options.size - 1).toFloat(),
        steps = options.size - 2,
        colors = SliderDefaults.colors(
            thumbColor = Ink,
            activeTrackColor = Ink,
            inactiveTrackColor = LavenderLight,
        ),
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEach { o ->
            Text(
                "$o menit",
                style = if (o == YogaSessionState.durationMin) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                color = if (o == YogaSessionState.durationMin) Ink else Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("Geser untuk memilih durasi. Ambil waktu yang nyaman untukmu.", style = MaterialTheme.typography.bodyMedium, color = Muted)
    Spacer(Modifier.height(24.dp))
    Surface(shape = RoundedCornerShape(20.dp), color = Lime, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, null, tint = Ink, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(10.dp))
                Text("Siapkan ruang yang nyaman", style = MaterialTheme.typography.titleMedium, color = Ink)
            }
            Spacer(Modifier.height(12.dp))
            listOf(
                "Gunakan matras di permukaan yang rata.",
                "Sisakan ruang gerak di sekitarmu.",
                "Berhenti jika terasa sakit atau pusing.",
            ).forEach {
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Check, null, tint = Ink, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = Ink)
                }
            }
        }
    }
}
