package com.viscit.yogamate.ui.feature.activity

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.data.HistoryItem
import com.viscit.yogamate.data.Level
import com.viscit.yogamate.data.TimeUtil
import com.viscit.yogamate.ui.components.AppHeader
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.HistoryCard
import com.viscit.yogamate.ui.components.SectionHeader
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.feature.yoga.DetailSheet
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft
import java.util.Calendar

// HISTORY-01 Aktivitas / Riwayat (Figma 102:1763). Label tab: Aktivitas; desain: Riwayat.
@Composable
fun ActivityScreen(onProfile: () -> Unit, onStartYoga: () -> Unit) {
    var detail by remember { mutableStateOf<HistoryItem?>(null) }
    val (td, tm, ty) = TimeUtil.todayParts()
    var month by remember { mutableIntStateOf(tm) }
    var year by remember { mutableIntStateOf(ty) }
    var filter by remember { mutableStateOf(Level.PEMULA.label) }
    val filters = listOf("Semua", Level.PEMULA.label, Level.MENENGAH.label, Level.LANJUTAN.label)

    val monthHistory = remember(month, year, AppSession.history) {
        AppSession.history.filter { it.month == month && it.year == year }
    }
    val shown = if (filter == "Semua") monthHistory else monthHistory.filter { it.level.label == filter }

    Column(Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(8.dp))
        AppHeader(onProfile)
        Spacer(Modifier.height(16.dp))
        Text("Rutinitas Harianmu", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Spacer(Modifier.height(16.dp))

        // Ringkasan bulan
        Surface(shape = RoundedCornerShape(20.dp), color = androidx.compose.ui.graphics.Color.Transparent, border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft)) {
            Row(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                StatCell("${monthHistory.size}", "Sesi latihan", Modifier.weight(1f))
                StatCell("${monthHistory.sumOf { it.minutes }}", "Menit bergerak", Modifier.weight(1f))
                StatCell("${monthHistory.map { it.poseName }.distinct().size}", "Pose dicoba", Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(24.dp))

        SectionHeader("Ritme latihanmu")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Bulan sebelumnya", tint = Ink,
                modifier = Modifier.clickable {
                    if (month == 1) { month = 12; year-- } else month--
                })
            Text("${TimeUtil.monthName(month)} $year", style = MaterialTheme.typography.labelMedium, color = Ink, modifier = Modifier.padding(horizontal = 8.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Bulan berikutnya", tint = Ink,
                modifier = Modifier.clickable {
                    if (month == 12) { month = 1; year++ } else month++
                })
        }
        Spacer(Modifier.height(12.dp))

        // Kalender heatmap: intensitas latihan per hari
        val days = TimeUtil.daysInMonth(month, year)
        val firstOffset = TimeUtil.firstDayOfWeek(month, year) - 1
        val weekLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            weekLabels.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(8.dp))
        val maxMin = (monthHistory.maxOfOrNull { it.minutes } ?: 1).coerceAtLeast(1)
        // Menit per hari diakumulasi sekali agar sel heatmap tidak memfilter history per sel.
        val minutesByDay = remember(monthHistory) {
            IntArray(32).also { acc -> monthHistory.forEach { acc[it.dayOfMonth] += it.minutes } }
        }
        val cells = firstOffset + days
        val rows = (cells + 6) / 7
        repeat(rows) { r ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                repeat(7) { c ->
                    val day = r * 7 + c - firstOffset + 1
                    val valid = day in 1..days
                    val mins = if (valid) minutesByDay[day] else 0
                    val bg = when {
                        !valid || mins == 0 -> androidx.compose.ui.graphics.Color.Transparent
                        mins >= maxMin -> Ink
                        mins >= maxMin * 0.5 -> Lavender
                        else -> LavenderLight
                    }
                    val textColor = if (mins >= maxMin) androidx.compose.ui.graphics.Color.White else Ink
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg)
                            .border(1.dp, if (bg == androidx.compose.ui.graphics.Color.Transparent) OutlineSoft else bg, RoundedCornerShape(14.dp)),
                    ) {
                        if (valid) Text("$day", style = MaterialTheme.typography.labelMedium, color = textColor)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Sedikit", style = MaterialTheme.typography.bodySmall, color = Muted)
            listOf(OutlineSoft, LavenderLight, Lavender, Ink).forEach { c ->
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(14.dp).clip(CircleShape).background(c).border(1.dp, OutlineSoft, CircleShape))
            }
            Spacer(Modifier.width(4.dp))
            Text("Banyak", style = MaterialTheme.typography.bodySmall, color = Muted)
        }
        Spacer(Modifier.height(24.dp))

        SectionHeader("Riwayat latihan")
        Spacer(Modifier.height(12.dp))
        // Filter level: chip dalam wadah lavendel, terpilih putih
        Surface(shape = RoundedCornerShape(50), color = LavenderLight) {
            Row(Modifier.padding(4.dp)) {
                filters.forEach { f ->
                    val selected = f == filter
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { filter = f }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(f, style = MaterialTheme.typography.labelMedium, color = if (selected) Ink else Muted)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        if (shown.isEmpty()) {
            // Empty state sesuai desain (kartu lavendel + CTA)
            Surface(shape = RoundedCornerShape(24.dp), color = LavenderLight, modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Icon(Icons.Default.LocalFlorist, null, tint = Ink, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Petualangan baru menantimu.", style = MaterialTheme.typography.titleMedium, color = Ink, textAlign = TextAlign.Center)
                    Text("Belum ada latihan pada bulan ini.", style = MaterialTheme.typography.bodyMedium, color = Muted, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    YmPrimaryButton("Coba latihan", onStartYoga, trailingIcon = { ArrowForwardIcon() })
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                shown.sortedByDescending { it.dayOfMonth }.forEach { HistoryCard(it, onClick = { detail = it }) }
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    detail?.let { DetailSheet(it, onDismiss = { detail = null }) }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Ink)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Muted)
    }
}
