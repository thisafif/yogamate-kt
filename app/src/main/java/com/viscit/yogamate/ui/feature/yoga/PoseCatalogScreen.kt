package com.viscit.yogamate.ui.feature.yoga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.data.Poses
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Muted

@Composable
fun PoseCatalogScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") }
            Text("Katalog pose yoga", style = MaterialTheme.typography.headlineSmall, color = Ink)
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Poses.DETECTION, key = { it.classId }) { pose ->
                val guide = Poses.GACHA.firstOrNull { it.modelClassId == pose.classId }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(pose.name, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text("${pose.level.label} · kelas ${pose.classId}", style = MaterialTheme.typography.bodySmall, color = Muted)
                        if (guide == null) {
                            Text("Konten belum tersedia", style = MaterialTheme.typography.labelSmall, color = Muted)
                        } else {
                            Text(guide.description, style = MaterialTheme.typography.bodyMedium, color = Ink)
                            guide.steps.forEachIndexed { index, step ->
                                Text("${index + 1}. $step", style = MaterialTheme.typography.bodySmall, color = Muted, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
