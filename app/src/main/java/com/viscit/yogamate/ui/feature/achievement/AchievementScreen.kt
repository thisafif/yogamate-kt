package com.viscit.yogamate.ui.feature.achievement

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.AchievementIcon
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.data.Achievements
import com.viscit.yogamate.ui.components.AppHeader
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft

// ACH-01 Pencapaian (Figma 102:2295). Ikon profil kanan atas, bukan tab.
@Composable
fun AchievementScreen(onProfile: () -> Unit, onOpenHistory: () -> Unit) {
    val achievements = Achievements.compute(AppSession.history)
    val earned = achievements.count { it.achieved }

    Column(Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(8.dp))
        AppHeader(onProfile)
        Spacer(Modifier.height(16.dp))
        Text("Pencapaianmu", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Text("Rayakan progressmu yukk!", style = MaterialTheme.typography.bodyLarge, color = Muted)
        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("Lencana diraih", style = MaterialTheme.typography.titleMedium, color = Ink)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$earned", style = MaterialTheme.typography.displayMedium, color = Ink)
                    Text(" /${achievements.size}", style = MaterialTheme.typography.headlineMedium, color = Muted)
                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp).background(Lime, CircleShape)) {
                Image(
                    painterResource(R.drawable.ic_nav_pencapaian),
                    "Lencana",
                    colorFilter = ColorFilter.tint(Ink),
                    modifier = Modifier.size(28.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onOpenHistory).padding(vertical = 4.dp),
        ) {
            Text("Lihat riwayat latihan", style = MaterialTheme.typography.titleMedium, color = Ink)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Ink, modifier = Modifier.size(18.dp).padding(start = 4.dp))
        }
        Spacer(Modifier.height(24.dp))

        Text("Perjalanan lencanamu", style = MaterialTheme.typography.titleLarge, color = Ink)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            achievements.forEach { a ->
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp).background(if (a.achieved) Lime else LavenderLight, CircleShape)) {
                            AchievementGlyph(a.icon)
                        }
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(a.title, style = MaterialTheme.typography.titleMedium, color = Ink)
                            Text(a.subtitle, style = MaterialTheme.typography.bodyMedium, color = Muted)
                        }
                        Text(
                            if (a.achieved) "✓✓ Diraih" else "Dalam proses",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (a.achieved) Ink else Muted,
                            textAlign = TextAlign.End,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.fillMaxWidth().padding(start = 68.dp).height(5.dp).clip(RoundedCornerShape(3.dp)).background(OutlineSoft)) {
                        Box(
                            Modifier
                                .fillMaxWidth((a.progress.toFloat() / a.target).coerceIn(0f, 1f))
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Ink),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${a.progress.coerceAtMost(a.target)} / ${a.target} ${a.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Muted,
                        modifier = Modifier.padding(start = 68.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AchievementGlyph(icon: AchievementIcon) {
    when (icon) {
        AchievementIcon.SPROUT -> Icon(Icons.Default.Eco, null, tint = Ink, modifier = Modifier.size(26.dp))
        AchievementIcon.FLOWER -> Icon(Icons.Default.LocalFlorist, null, tint = Ink, modifier = Modifier.size(26.dp))
        AchievementIcon.FIRE -> Icon(Icons.Default.LocalFireDepartment, null, tint = Ink, modifier = Modifier.size(26.dp))
        AchievementIcon.SPARKLE -> Icon(Icons.Default.AutoAwesome, null, tint = Ink, modifier = Modifier.size(26.dp))
        AchievementIcon.HEART -> Icon(Icons.Default.FavoriteBorder, null, tint = Ink, modifier = Modifier.size(26.dp))
    }
}
