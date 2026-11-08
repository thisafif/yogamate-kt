package com.viscit.yogamate.ui.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmTextField
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lavender
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted

// AUTH-02 Register (Figma 98:594). UI-only: validasi lokal, belum ada backend.
@Composable
fun RegisterScreen(onSuccess: () -> Unit, onLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val hasMinLength = password.length >= 8
    val hasDigit = password.any { it.isDigit() }
    val hasCase = password.any { it.isUpperCase() } && password.any { it.isLowerCase() }
    val score = listOf(hasMinLength, hasDigit, hasCase).count { it }
    val strengthLabel = when {
        password.isEmpty() -> "Belum diisi"
        score == 3 -> "Kuat"
        score == 2 -> "Sedang"
        else -> "Lemah"
    }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(96.dp).background(LavenderLight, CircleShape))
                Image(painterResource(R.drawable.mascot_01_halo), null, Modifier.size(80.dp))
            }
            Spacer(Modifier.size(16.dp))
            Text("Senang bisa kenalan\ndenganmu!", style = MaterialTheme.typography.bodyLarge, color = Muted)
        }
        Spacer(Modifier.height(24.dp))
        Text("Mulai Kebiasaan Yogamu.", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text("Buat akun untuk menyimpan langkah kecilmu.", style = MaterialTheme.typography.bodyLarge, color = Muted)
        Spacer(Modifier.height(24.dp))

        YmTextField("Nama panggilan", name, { name = it }, "Nama yang ingin Mate panggil")
        Spacer(Modifier.height(16.dp))
        YmTextField("Email", email, { email = it }, "nama@email.com")
        Spacer(Modifier.height(16.dp))
        YmTextField("Kata sandi", password, { password = it }, "Minimal 8 karakter", isPassword = true)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Kekuatan kata sandi", style = MaterialTheme.typography.titleMedium, color = Ink)
            Spacer(Modifier.weight(1f))
            Text(strengthLabel, style = MaterialTheme.typography.bodyMedium, color = Ink, textAlign = TextAlign.End)
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(8.dp).background(LavenderLight, RoundedCornerShape(4.dp))) {
            Box(
                Modifier
                    .fillMaxWidth(score / 3f)
                    .height(8.dp)
                    .background(if (score == 3) Ink else Lavender, RoundedCornerShape(4.dp)),
            )
        }
        Spacer(Modifier.height(12.dp))
        listOf(
            "Minimal 8 karakter" to hasMinLength,
            "Setidaknya satu angka" to hasDigit,
            "Huruf besar dan huruf kecil" to hasCase,
        ).forEach { (label, ok) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(20.dp).background(if (ok) Ink else LavenderLight, CircleShape),
                ) {
                    if (ok) Text("✓", style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White)
                }
                Spacer(Modifier.size(10.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = Muted)
            }
        }
        Spacer(Modifier.height(16.dp))

        YmTextField("Konfirmasi kata sandi", confirm, { confirm = it }, "Ulangi kata sandi")
        Spacer(Modifier.height(24.dp))

        error?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        YmPrimaryButton("Buat akun", {
            when {
                name.isBlank() -> error = "Nama panggilan belum diisi."
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> error = "Format email belum benar."
                score < 3 -> error = "Kata sandi belum memenuhi semua syarat."
                confirm != password -> error = "Konfirmasi kata sandi belum sama."
                else -> { AppSession.login(name.trim(), email, password); onSuccess() }
            }
        }, trailingIcon = { ArrowForwardIcon() })
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Sudah punya akun? ", style = MaterialTheme.typography.bodyMedium, color = Muted)
            Text("Masuk", style = MaterialTheme.typography.labelMedium, color = Ink, modifier = Modifier.clickable(onClick = onLogin))
        }
        Spacer(Modifier.height(24.dp))
    }
}
