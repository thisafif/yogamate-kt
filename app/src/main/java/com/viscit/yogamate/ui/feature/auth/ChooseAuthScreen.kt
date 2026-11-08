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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.ui.components.ArrowForwardIcon
import com.viscit.yogamate.ui.components.YmPrimaryButton
import com.viscit.yogamate.ui.components.YmSecondaryButton
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Muted

private val MutedAuth get() = Muted

// AUTH-01 (Figma 98:502): pilih register atau login.
@Composable
fun ChooseAuthScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.weight(0.8f))
        Box(contentAlignment = Alignment.Center) {
            Box(Modifier.size(260.dp).background(LavenderLight, CircleShape))
            Image(painterResource(R.drawable.mascot_01_halo), null, Modifier.size(180.dp))
        }
        Spacer(Modifier.height(24.dp))
        Row {
            Text("yog", style = MaterialTheme.typography.displayMedium, color = Ink)
            Text("amate", style = MaterialTheme.typography.displayMedium, color = Ink.copy(alpha = 0.75f))
        }
        Spacer(Modifier.height(8.dp))
        Text("Halo! Senang melihatmu di sini.", style = MaterialTheme.typography.bodyLarge, color = MutedAuth)
        Spacer(Modifier.weight(1f))
        Column(Modifier.fillMaxWidth()) {
            YmPrimaryButton("Buat akun", onRegister, trailingIcon = { Spacer(Modifier.width(10.dp)); ArrowForwardIcon() })
            Spacer(Modifier.height(12.dp))
            YmSecondaryButton("Masuk ke akun", onLogin)
        }
        Spacer(Modifier.height(32.dp))
    }
}
