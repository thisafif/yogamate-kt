package com.viscit.yogamate.ui.feature.yoga

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.HistoryItem
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted

// HISTORY-02 Detail Riwayat (Figma 108:3430): modal bottom sheet "Cerita latihanmu".
@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
fun DetailSheet(item: HistoryItem, onDismiss: () -> Unit, onStartYoga: () -> Unit = onDismiss) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFFFFCF7)) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Cerita latihanmu", style = MaterialTheme.typography.headlineMedium, color = Ink, modifier = Modifier.weight(1f))
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).background(LavenderLight, CircleShape).clickable(onClick = onDismiss)) {
                    Text("✕", style = MaterialTheme.typography.titleMedium, color = Ink)
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(24.dp), color = LavenderLight, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painterResource(R.drawable.ilustrasi_pose),
                    "Ilustrasi ${item.poseName}",
                    modifier = Modifier.fillMaxWidth().height(220.dp).padding(24.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(item.poseName, style = MaterialTheme.typography.headlineMedium, color = Ink)
            Text("${item.dayLabel}, ${item.timeLabel} · ${item.level.label}", style = MaterialTheme.typography.bodyLarge, color = Muted)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(16.dp), color = LavenderLight, modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Schedule, null, tint = Ink, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("${item.minutes} menit", style = MaterialTheme.typography.headlineMedium.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize), color = Ink)
                        Text("Durasi latihan", style = MaterialTheme.typography.bodySmall, color = Muted)
                    }
                }
                Surface(shape = RoundedCornerShape(16.dp), color = Lime, modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Check, null, tint = Ink, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("${item.accuracy}%", style = MaterialTheme.typography.headlineMedium.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize), color = Ink)
                        Text("Total Yoga Accuracy", style = MaterialTheme.typography.bodySmall, color = Muted)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Ini data contoh untuk menggambarkan hasil pengenalan pose. Akurasi bukan penilaian medis atau jaminan ketepatan gerakan.",
                style = MaterialTheme.typography.bodyLarge, color = Muted, textAlign = TextAlign.Start,
            )
            Spacer(Modifier.height(20.dp))
            YmPrimaryButton("Latihan lagi", onStartYoga, trailingIcon = { ArrowForwardIconWhite() })
        }
    }
}

@Composable
internal fun ArrowForwardIconWhite() {
    Spacer(Modifier.width(8.dp))
    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(20.dp))
}
