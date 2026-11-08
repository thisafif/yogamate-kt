package com.viscit.yogamate.ui.feature.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted

private data class OnbPage(val mascot: Int, val caption: String, val heading: String, val body: String, val button: String)

// Onboarding 1–3 (Figma 98:32, 98:122, 98:411): satu screen, 3 halaman state internal.
private val PAGES = listOf(
    OnbPage(R.drawable.mascot_01_halo, "Halo! Aku Mate, teman yoga barumu.",
        "Gerak kecil.\nRasa lebih baik.",
        "Awal awal gak perlu sempurna juga gapapa kok! Kita sama sama belajar iniih~", "Lanjutkan"),
    OnbPage(R.drawable.mascot_04_semangat, "Aku temani, satu langkah dalam satu waktu.",
        "Cobain dulu satu pose.",
        "Kamu bisa tingkat kesulitan, temukan pose, dan atur durasi yang nyaman buat kamu. Sebentar juga gapapa banget kok!", "Lanjutkan"),
    OnbPage(R.drawable.mascot_07_prestasi, "Siap bertumbuh bersama?",
        "Pelan–pelan,\nbisa yuk bisaa!",
        "Ada riwayat latihan dan rayakan kemajuan kecilmu.", "Mulai perjalanan"),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit, onLogin: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val p = PAGES[page]
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.weight(0.6f))
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(320.dp)
                    .background(LavenderLight, CircleShape),
            )
            Image(painterResource(p.mascot), null, Modifier.size(220.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(p.caption, style = MaterialTheme.typography.labelMedium, color = Muted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(3) { i ->
                if (i == page) {
                    Box(Modifier.size(width = 44.dp, height = 10.dp).background(Ink, RoundedCornerShape(50)))
                } else {
                    Box(Modifier.size(12.dp).background(Lavender, CircleShape))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(Modifier.fillMaxWidth()) {
            Text(p.heading, style = MaterialTheme.typography.displayLarge, color = Ink)
            Spacer(Modifier.height(16.dp))
            Text(p.body, style = MaterialTheme.typography.bodyMedium, color = Muted)
            Spacer(Modifier.height(24.dp))
            YmPrimaryButton(p.button, onClick = { if (page < 2) page++ else onFinish() })
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Sudah punya akun? ", style = MaterialTheme.typography.bodyMedium, color = Muted)
                Text(
                    "Masuk",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink,
                    modifier = Modifier.clickable(onClick = onLogin),
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
