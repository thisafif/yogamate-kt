package com.viscit.yogamate.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.data.HistoryItem
import com.viscit.yogamate.ui.components.AppHeader
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.HistoryCard
import com.viscit.yogamate.ui.components.SectionHeader
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.feature.yoga.DetailSheet
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderSoft
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft
import java.util.Calendar

// HOME-01 Beranda (Figma 102:1444). Ikon profil kanan atas membuka Profil (bukan tab).
@Composable
fun HomeScreen(onProfile: () -> Unit, onStartYoga: () -> Unit, onOpenHistory: () -> Unit) {
    var detail by remember { mutableStateOf<HistoryItem?>(null) }
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    // Urutan label hari sesuai desain: S S R K J S M (Senin..Minggu)
    val dayLabels = listOf("S", "S", "R", "K", "J", "S", "M")
    val startOffset = (today + 6) % 7 // posisi hari ini dalam minggu Senin-pertama
    val week = (0..6).map { i ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, i - startOffset)
        cal
    }

    Column(Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(8.dp))
        AppHeader(onProfile)
        Spacer(Modifier.height(16.dp))
        Text("Halo, ${AppSession.userName.ifBlank { "{nama}" }}.", style = MaterialTheme.typography.bodyLarge, color = Muted)
        Text("Waktunya kembali\nke dirimu.", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Spacer(Modifier.height(16.dp))

        // Kartu hero
        Surface(shape = RoundedCornerShape(24.dp), color = LavenderSoft, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text("Satu pose.\nSatu jeda\nuntukmu.", style = MaterialTheme.typography.headlineMedium, color = Ink)
                        Spacer(Modifier.height(12.dp))
                        Text("Pelan pelan, yuk mulai\nrutin yoga!", style = MaterialTheme.typography.bodyMedium, color = Muted)
                    }
                    Box(contentAlignment = Alignment.Center) {
                        Box(Modifier.size(140.dp).background(com.viscit.yogamate.ui.theme.Lavender.copy(alpha = 0.35f), CircleShape))
                        androidx.compose.foundation.Image(painterResource(R.drawable.mascot_hero), null, Modifier.height(140.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                YmPrimaryButton("Mulai yoga", onStartYoga, trailingIcon = { ArrowForwardIcon() })
            }
        }
        Spacer(Modifier.height(24.dp))

        SectionHeader("Ritmemu minggu ini", "Progres", onOpenHistory)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("0", style = MaterialTheme.typography.displayMedium, color = Ink)
            Text(" /7 ", style = MaterialTheme.typography.headlineMedium, color = Muted)
            Text("hari berlatih", style = MaterialTheme.typography.bodyLarge, color = Muted)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            week.forEachIndexed { i, cal ->
                val isToday = i == startOffset
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(dayLabels[i], style = MaterialTheme.typography.labelSmall, color = Muted)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(if (isToday) Lime else androidx.compose.ui.graphics.Color.White)
                            .border(1.dp, if (isToday) Ink else OutlineSoft, CircleShape),
                    ) {
                        Text("${cal.get(Calendar.DAY_OF_MONTH)}", style = MaterialTheme.typography.labelMedium, color = Ink)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        SectionHeader("Latihan terakhir", "Lihat semua", onOpenHistory)
        Spacer(Modifier.height(12.dp))
        val last = AppSession.history.firstOrNull()
        if (last != null) {
            HistoryCard(last, onClick = { detail = last })
        } else {
            // Belum ada riwayat — desain memakai contoh data; tampilkan kartu contoh "Pose Pohon" (108:3430)
            HistoryCard(
                HistoryItem(0, "Pose Pohon", com.viscit.yogamate.data.Level.PEMULA, 0, 0, 0, "07.30", "Hari ini", 3, 92),
                onClick = { detail = HistoryItem(0, "Pose Pohon", com.viscit.yogamate.data.Level.PEMULA, 0, 0, 0, "07.30", "Hari ini", 3, 92) },
            )
        }
        Spacer(Modifier.height(24.dp))
    }

    detail?.let { DetailSheet(it, onDismiss = { detail = null }) }
}
