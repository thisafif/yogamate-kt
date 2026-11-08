package com.viscit.yogamate.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.viscit.yogamate.R

val DmSans = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
    Font(R.font.dm_sans_semibold, FontWeight.SemiBold),
)
val JakartaBold = FontFamily(Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold))

// Nilai fontSize/lineHeight dibaca dari node data Figma (Plus Jakarta Sans 700 heading, DM Sans body)
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = JakartaBold, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 45.sp), // heading onboarding
    displayMedium = TextStyle(fontFamily = JakartaBold, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp), // "Pose Pohon" setup 3
    headlineLarge = TextStyle(fontFamily = JakartaBold, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp), // heading tab utama
    headlineMedium = TextStyle(fontFamily = JakartaBold, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp), // heading kartu/sheet
    titleLarge = TextStyle(fontFamily = JakartaBold, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.75.sp),
    bodySmall = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp), // label tombol
    labelMedium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)
