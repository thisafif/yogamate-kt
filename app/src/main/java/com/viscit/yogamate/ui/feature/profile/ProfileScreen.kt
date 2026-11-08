package com.viscit.yogamate.ui.feature.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.theme.Danger
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted

// PROFILE-01 Profil (Figma 104:2837): non-tab, dibuka dari ikon kanan atas 3 tab utama.
@Composable
fun ProfileScreen(onBack: () -> Unit, onEditProfile: () -> Unit, onOpenEducation: () -> Unit, onOpenHistory: () -> Unit, onOpenAchievement: () -> Unit) {
    var showGuide by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.Image(painterResource(R.drawable.logo_yogamate), "yogamate", Modifier.height(56.dp))
            Spacer(Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .background(Lavender.copy(alpha = 0.4f), CircleShape)
                    .clickable(onClick = onBack),
            ) {
                Icon(Icons.Default.Person, "Kembali", tint = Ink, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Profilmu", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Text("Atur profil kamu disini yaa!", style = MaterialTheme.typography.bodyLarge, color = Muted)
        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp).background(LavenderLight, CircleShape)) {
                Text(
                    AppSession.userName.trim().ifBlank { "Y" }.take(1).uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = Ink,
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(AppSession.userName.ifBlank { "Yogamate" }, style = MaterialTheme.typography.headlineMedium, color = Ink)
        }
        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("Tujuan latihanku", style = MaterialTheme.typography.bodyMedium, color = Muted)
                Text(AppSession.userGoal, style = MaterialTheme.typography.titleMedium, color = Ink)
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = LavenderLight,
                onClick = onEditProfile,
            ) {
                Text("Edit profil", style = MaterialTheme.typography.labelMedium, color = Ink, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            }
        }
        Spacer(Modifier.height(24.dp))

        Text("Ruang pribadimu", style = MaterialTheme.typography.titleLarge, color = Ink)
        Spacer(Modifier.height(8.dp))
        MenuRow(icon = Icons.Default.Schedule, label = "Riwayat latihan", onClick = onOpenHistory)
        MenuRow(painterRes = R.drawable.ic_nav_pencapaian, label = "Pencapaian", onClick = onOpenAchievement)
        MenuRow(icon = Icons.Default.SelfImprovement, label = "Edukasi kesehatan", onClick = onOpenEducation)
        MenuRow(icon = Icons.AutoMirrored.Outlined.MenuBook, label = "Panduan & latihan aman", onClick = { showGuide = true })
        Spacer(Modifier.height(32.dp))

        YmPrimaryButton("Logout", {
            AppSession.logout()
            onBack()
        }, container = Danger, leadingIcon = {
            androidx.compose.material3.Icon(Icons.Default.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
        })
        Spacer(Modifier.height(24.dp))
    }

    // PROFILE-02 Panduan Singkat (108:3362): modal bottom sheet, bukan tab/route.
    if (showGuide) {
        GuideSheet(onDismiss = { showGuide = false })
    }
}

@Composable
private fun MenuRow(icon: androidx.compose.ui.graphics.vector.ImageVector? = null, painterRes: Int? = null, label: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp).background(LavenderLight, CircleShape)) {
            if (icon != null) Icon(icon, null, tint = Ink, modifier = Modifier.size(24.dp))
            if (painterRes != null) androidx.compose.foundation.Image(
                painterResource(painterRes), null,
                modifier = Modifier.size(24.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Ink),
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = Ink, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Ink, modifier = Modifier.size(22.dp))
    }
}

@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
fun GuideSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFFFFCF7)) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp).verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Yoga pertama? Sanss ajah.", style = MaterialTheme.typography.headlineMedium, color = Ink, modifier = Modifier.weight(1f))
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).background(LavenderLight, CircleShape).clickable(onClick = onDismiss)) {
                    Text("✕", style = MaterialTheme.typography.titleMedium, color = Ink)
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(16.dp), color = Lime, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.AutoMirrored.Outlined.MenuBook, null, tint = Ink, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Panduan penting untuk dibaca!", style = MaterialTheme.typography.titleMedium, color = Ink)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Yoga bukan soal menyentuh ujung kaki atau melakukan pose sempurna. Mulailah dari gerakan sederhana, dan beri tubuhmu waktu untuk mengenalnya.",
                style = MaterialTheme.typography.bodyLarge, color = Muted,
            )
            Spacer(Modifier.height(20.dp))
            GuideStep(1, "Siapkan ruangmu", "Pilih permukaan rata, matras yang tidak licin, dan ruang yang cukup untuk merentangkan tangan.")
            GuideStep(2, "Pemanasan dulu", "Gerakkan bahu, pergelangan tangan, dan kaki dengan lembut. Jangan memaksa rentang gerak.")
            GuideStep(3, "Dengarkan tubuhmu", "Bernapas dengan nyaman. Hentikan latihan jika muncul nyeri, pusing, atau rasa tidak nyaman.")
            Spacer(Modifier.height(20.dp))
            Surface(shape = RoundedCornerShape(16.dp), color = LavenderLight, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "YogaMate bukan alat diagnosis atau pengganti instruktur profesional. Jika memiliki kondisi kesehatan tertentu, konsultasikan dengan tenaga kesehatan sebelum berlatih.",
                    style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(16.dp),
                )
            }
            Spacer(Modifier.height(20.dp))
            YmPrimaryButton("Siap mencoba", onDismiss, trailingIcon = {
                androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(20.dp))
            })
        }
    }
}

@Composable
private fun GuideStep(number: Int, title: String, body: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp).background(Lavender, CircleShape)) {
            Text("$number", style = MaterialTheme.typography.titleMedium, color = Ink)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
            Spacer(Modifier.height(4.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Muted)
        }
    }
}
