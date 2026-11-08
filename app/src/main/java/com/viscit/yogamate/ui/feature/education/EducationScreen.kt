package com.viscit.yogamate.ui.feature.education

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.ui.components.YmTopBar
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft

// Konten statis Edukasi Kesehatan (PRD section 11) — sumber umum, bukan diagnosis personal.
private data class EducationTopic(val icon: ImageVector, val title: String, val body: List<String>)

private val TOPICS = listOf(
    EducationTopic(
        Icons.Default.SelfImprovement,
        "Dasar Yoga",
        listOf(
            "Yoga merupakan latihan yang menggabungkan gerakan tubuh, pengaturan napas, dan konsentrasi tanpa mengharuskan pemula memiliki kelenturan tubuh tertentu.",
            "Berlatih Yoga selama 5–10 menit sehari dapat meeningkatkan kelenturan, kualitas pernapasan, dan konsentrasi."
        ),
    ),
    EducationTopic(
        Icons.Default.Lightbulb,
        "Tips Latihan",
        listOf(
            "Pastikan tersedia ruang gerak yang cukup agar kamu dapat merentangkan tangan dan kaki tanpa membentur benda di sekitarnya.",
            "Pakai pakaian yang nyaman agar tidak membatasi gerakan tubuh kamu"
        ),
    ),
    EducationTopic(
        Icons.Default.LocalFireDepartment,
        "Pemanasan",
        listOf(
            "Pemanasan selama 3 sampe 5 menit membantu mempersiapkan otot dan sendi sebelum memulai rangkaian pose yoga.",
            "Gerakkan leher, bahu, pergelangan tangan, pinggul, dan pergelangan kaki secara perlahan menggunakan gerakan sederhana.",
        ),
    ),
    EducationTopic(
        Icons.Default.HealthAndSafety,
        "Catatan Keamanan",
        listOf(
            "Hentikan latihan apabila mengalami nyeri tajam, pusing, sesak napas, atau ketidaknyamanan yang tidak biasa.",
            "YogaMate merupakan alat bantu latihan mandiri, bukan alat diagnosis medis maupun pengganti arahan instruktur yoga profesional."
        ),
    ),
)

// Modul Edukasi Kesehatan (PRD IA #5): satu layar, 4 kartu konten statis.
@Composable
fun EducationScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        YmTopBar("Edukasi kesehatan", onBack = onBack, onClose = null)
        Spacer(Modifier.height(16.dp))
        Text("Kenali sebelum Yoga!", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Text(
            "Pahami dasar dasar yoga agar latihanmu aman dan menyenangkan.",
            style = MaterialTheme.typography.bodyLarge, color = Muted,
        )
        Spacer(Modifier.height(24.dp))
        TOPICS.forEach { topic ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp).background(LavenderLight, CircleShape)) {
                            Icon(topic.icon, null, tint = Ink, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(topic.title, style = MaterialTheme.typography.titleLarge, color = Ink)
                    }
                    Spacer(Modifier.height(14.dp))
                    topic.body.forEachIndexed { i, paragraph ->
                        if (i > 0) Spacer(Modifier.height(10.dp))
                        Text(paragraph, style = MaterialTheme.typography.bodyMedium, color = Muted)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Spacer(Modifier.height(8.dp))
    }
}
