package com.viscit.yogamate.ui.feature.yoga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.data.YogaSessionState
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmTopBar
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted

// YOGA-06 Selesai Yoga (Figma 117:4593).
@Composable
fun CompleteScreen(onBack: () -> Unit, onSeeHistory: () -> Unit, onBackHome: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth()) {
            YmTopBar("Latihan selesai", onBack = onBack, onClose = onBack)
        }
        Spacer(Modifier.weight(0.4f))
        Box {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp).background(Lime, CircleShape)) {
                Icon(
                    painterIconTrophy(),
                    null, tint = Ink, modifier = Modifier.size(72.dp),
                )
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).align(Alignment.BottomStart).background(Lavender, CircleShape)) {
                Icon(Icons.Default.Check, null, tint = Ink, modifier = Modifier.size(22.dp))
            }
            Icon(Icons.Default.AutoAwesome, null, tint = Ink, modifier = Modifier.align(Alignment.TopEnd).size(28.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Kamu sudah meluangkan\nwaktu untuk dirimu!",
            style = MaterialTheme.typography.headlineLarge, color = Ink, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text("Terima kasih sudah hadir untuk tubuhmu hari ini.", style = MaterialTheme.typography.bodyLarge, color = Muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Surface(shape = RoundedCornerShape(24.dp), color = Color.Transparent, border = androidx.compose.foundation.BorderStroke(1.dp, com.viscit.yogamate.ui.theme.OutlineSoft), modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(YogaSessionState.pose.name, style = MaterialTheme.typography.headlineMedium, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${YogaSessionState.pose.level.label} · ${YogaSessionState.durationMin} menit · Selesai",
                    style = MaterialTheme.typography.bodyLarge, color = Muted,
                )
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(com.viscit.yogamate.ui.theme.OutlineSoft))
                Spacer(Modifier.height(16.dp))
                Text("TOTAL YOGA ACCURACY", style = MaterialTheme.typography.labelSmall, color = Muted)
                Spacer(Modifier.height(4.dp))
                Text("${YogaSessionState.resultAccuracy}%", style = MaterialTheme.typography.displayMedium, color = Ink)
            }
        }
        Spacer(Modifier.weight(1f))
        YmPrimaryButton("Lihat riwayat", onSeeHistory, trailingIcon = { ArrowForwardIcon() })
        Spacer(Modifier.height(16.dp))
        Text(
            "Kembali ke beranda",
            style = MaterialTheme.typography.labelMedium, color = Ink,
            modifier = Modifier.clickable(onClick = onBackHome),
        )
        Spacer(Modifier.height(24.dp))
    }
}

// Trofi memakai aset ikon nav asli dari Figma agar bentuk sama.
@Composable
private fun painterIconTrophy() = androidx.compose.ui.res.painterResource(com.viscit.yogamate.R.drawable.ic_nav_pencapaian)
