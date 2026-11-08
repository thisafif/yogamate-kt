package com.viscit.yogamate.ui.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmTextField
import com.viscit.yogamate.ui.components.YmTopBar
import com.viscit.yogamate.ui.theme.Ink
import androidx.compose.ui.graphics.Color.Companion.White
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft

// Opsi tujuan latihan untuk Edit Profil (belum ada node khusus di Figma — mengikuti pola kartu pilihan StepLevel).
private val GOAL_OPTIONS = listOf("Lebih fleksibel", "Mengurangi stres", "Membangun kebiasaan", "Menambah tenaga")

// Edit Profil: non-tab, dibuka dari tombol "Edit profil" di Profil.
@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf(AppSession.userName) }
    var goal by remember { mutableStateOf(AppSession.userGoal) }
    var email by remember { mutableStateOf(AppSession.userEmail) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(8.dp))
        YmTopBar("Edit profil", onBack = onBack, onClose = null)
        Spacer(Modifier.height(24.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp).background(LavenderLight, CircleShape)) {
            Text(
                AppSession.userName.trim().ifBlank { "Y" }.take(1).uppercase(),
                style = MaterialTheme.typography.displayMedium, color = Ink,
            )
        }
        Spacer(Modifier.height(24.dp))

        Text("NAMA", style = MaterialTheme.typography.labelSmall, color = Muted)
        Spacer(Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(20.dp), color = White, border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft)) {
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink),
                cursorBrush = SolidColor(Ink),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            )
        }
        Spacer(Modifier.height(24.dp))

        YmTextField("Email", email, { email = it }, "nama@email.com")
        Spacer(Modifier.height(24.dp))

        Text("TUJUAN LATIHANKU", style = MaterialTheme.typography.labelSmall, color = Muted)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            GOAL_OPTIONS.forEach { option ->
                val selected = goal == option
                Surface(
                    onClick = { goal = option },
                    shape = RoundedCornerShape(20.dp),
                    color = if (selected) LavenderLight else White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Ink else OutlineSoft),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                        Text(option, style = MaterialTheme.typography.titleMedium, color = Ink, modifier = Modifier.weight(1f))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (selected) Ink else Color.Transparent)
                                .border(1.dp, if (selected) Ink else OutlineSoft, CircleShape),
                        ) {
                            if (selected) Icon(Icons.Default.Check, null, tint = White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // Kata sandi opsional: hanya diubah jika kolom baru diisi (auth lokal simulasi — tidak ada verifikasi sandi lama).
        Text("KATA SANDI BARU", style = MaterialTheme.typography.labelSmall, color = Muted)
        Spacer(Modifier.height(8.dp))
        YmTextField("Kata sandi baru", newPassword, { newPassword = it }, "Minimal 8 karakter", isPassword = true)
        Spacer(Modifier.height(16.dp))
        YmTextField("Konfirmasi kata sandi baru", confirmPassword, { confirmPassword = it }, "Ulangi kata sandi", isPassword = true)
        Spacer(Modifier.height(24.dp))

        error?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
//        Text("Perubahan tersimpan di perangkatmu.", style = MaterialTheme.typography.bodySmall, color = Muted)
//        Spacer(Modifier.height(16.dp))
        YmPrimaryButton(
            "Simpan perubahan",
            {
                when {
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                        error = "Format email belum benar."
                    newPassword.isNotEmpty() && newPassword.length < 8 ->
                        error = "Kata sandi baru minimal 8 karakter."
                    newPassword.isNotEmpty() && confirmPassword != newPassword ->
                        error = "Konfirmasi kata sandi belum sama."
                    else -> {
                        AppSession.updateProfile(name.trim(), goal, email.trim(), newPassword.ifBlank { null })
                        onBack()
                    }
                }
            },
            enabled = name.trim().isNotBlank(),
        )
        Spacer(Modifier.height(24.dp))
    }
}
