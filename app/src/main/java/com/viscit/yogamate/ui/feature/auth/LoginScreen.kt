package com.viscit.yogamate.ui.feature.auth

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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmTextField
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted

// AUTH-03 Login (Figma 98:752). UI-only: belum ada backend — kredensial tidak diverifikasi server.
@Composable
fun LoginScreen(onSuccess: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(128.dp).background(LavenderLight, CircleShape))
                Image(painterResource(R.drawable.mascot_01_halo), null, Modifier.size(104.dp))
            }
            Spacer(Modifier.size(16.dp))
            Text("Halo! Selamat datang di\nYogaMate!", style = MaterialTheme.typography.bodyLarge, color = Muted)
        }
        Spacer(Modifier.height(24.dp))
        Text("Senang kamu kembali.", style = MaterialTheme.typography.headlineLarge, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text("Masuk untuk melanjutkan perjalanan yogamu.", style = MaterialTheme.typography.bodyLarge, color = Muted)
        Spacer(Modifier.height(24.dp))

        YmTextField("Email", email, { email = it }, "nama@email.com")
        Spacer(Modifier.height(16.dp))
        YmTextField("Kata sandi", password, { password = it }, "Minimal 8 karakter", isPassword = true, supportingText = "Gunakan setidaknya 8 karakter.")
        Spacer(Modifier.height(24.dp))

        error?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        YmPrimaryButton("Masuk", {
            when {
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> error = "Format email belum benar."
                password.length < 8 -> error = "Gunakan setidaknya 8 karakter."
                else -> { AppSession.login(email.substringBefore("@")); onSuccess() }
            }
        }, trailingIcon = { ArrowForwardIcon() })
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Belum punya akun? ", style = MaterialTheme.typography.bodyMedium, color = Muted)
            Text("Buat akun", style = MaterialTheme.typography.labelMedium, color = Ink, modifier = Modifier.clickable(onClick = onRegister))
        }
        Spacer(Modifier.height(24.dp))
    }
}
