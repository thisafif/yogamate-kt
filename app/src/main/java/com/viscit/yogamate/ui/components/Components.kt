package com.viscit.yogamate.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.viscit.yogamate.R
import com.viscit.yogamate.data.HistoryItem
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.LavenderLight
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted
import com.viscit.yogamate.ui.theme.OutlineSoft

// Tombol utama sesuai desain: pill #28086B, label DM Sans 600 16, ikon opsional di kanan.
@Composable
fun YmPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    container: Color = Ink,
    contentColor: Color = Color(0xFFFFFCF7),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) container else container.copy(alpha = 0.4f),
        contentColor = contentColor,
        modifier = modifier.fillMaxWidth().height(56.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            leadingIcon?.invoke()
            Spacer(Modifier.width(if (leadingIcon != null) 10.dp else 0.dp))
            Text(text, style = MaterialTheme.typography.labelLarge)
            trailingIcon?.invoke()
        }
    }
}

// Tombol sekunder: outline tipis, teks ungu (dipakai "Masuk ke akun").
@Composable
fun YmSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, OutlineSofter),
        contentColor = Ink,
        modifier = modifier.fillMaxWidth().height(56.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
            trailingIcon?.invoke()
        }
    }
}

private val OutlineSofter = Color(0xFFEAE3EF)

// Panah kanan untuk tombol utama (sesuai desain).
@Composable
fun ArrowForwardIcon(tint: Color = Color(0xFFFFFCF7)) {
    Spacer(Modifier.width(10.dp))
    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = tint, modifier = Modifier.size(20.dp))
}

// Header aplikasi: logo maskot+wordmark (aset asli Figma) + ikon profil kanan atas.
// Profil bukan tab — hanya dibuka dari sini (ketentuan pengguna).
@Composable
fun AppHeader(onProfile: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(R.drawable.logo_yogamate),
            contentDescription = "yogamate",
            modifier = Modifier.height(56.dp),
        )
        Spacer(Modifier.weight(1f))
        Surface(
            onClick = onProfile,
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, OutlineSofter),
            modifier = Modifier.size(52.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profil",
                    tint = Ink,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

// Baris judul seksi + aksi kanan ("Progres >", "Lihat semua >").
@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Ink)
        Spacer(Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onAction).padding(4.dp),
            ) {
                Text(actionLabel, style = MaterialTheme.typography.bodyMedium, color = Ink)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Ink, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// Top bar alur yoga: tombol back bulat, judul tengah, tombol X.
@Composable
fun YmTopBar(
    title: String,
    onBack: (() -> Unit)?,
    onClose: (() -> Unit)?,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        if (onBack != null) {
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(1.dp, OutlineSofter),
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Ink, modifier = Modifier.size(22.dp))
                }
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = Ink,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        if (onClose != null) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Tutup", tint = Ink, modifier = Modifier.size(24.dp))
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
    }
}

// Segmen progres 4 langkah setup yoga.
@Composable
fun ProgressSegments(activeCount: Int, total: Int = 4) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        repeat(total) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (i < activeCount) Ink else OutlineSoft)
            )
        }
    }
}

// Input teks: kartu putih radius 16 outline tipis, label di atas.
@Composable
fun YmTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    supportingText: String? = null,
) {
    var showPassword by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = Ink)
        Spacer(Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink),
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { inner ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, OutlineSoft, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) Text(hint, style = MaterialTheme.typography.bodyLarge, color = Muted.copy(alpha = 0.6f))
                        inner()
                    }
                    if (isPassword) {
                        IconButton(onClick = { showPassword = !showPassword }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = Ink, modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            },
        )
        if (supportingText != null) {
            Spacer(Modifier.height(6.dp))
            Text(supportingText, style = MaterialTheme.typography.bodyMedium, color = Muted)
        }
    }
}

// Kartu riwayat latihan (Beranda "Latihan terakhir" & daftar Aktivitas) → buka Detail Riwayat.
@Composable
fun HistoryCard(item: HistoryItem, onClick: () -> Unit, showDate: Boolean = true) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineSoft),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = LavenderLight, modifier = Modifier.size(64.dp)) {
                Image(painterResource(R.drawable.ilustrasi_pose), null, Modifier.padding(6.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.poseName, style = MaterialTheme.typography.titleMedium, color = Ink)
                Text("${item.level.label}  ·  ${item.minutes} menit", style = MaterialTheme.typography.bodyMedium, color = Muted)
                if (showDate) Text("${item.dayLabel}, ${item.timeLabel}", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = RoundedCornerShape(50), color = Lime) {
                    Text(
                        "${item.accuracy}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = Ink,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }
                Text("Akurasi", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        }
    }
}
