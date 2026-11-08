package com.viscit.yogamate.ui.navigation

import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.ui.feature.activity.ActivityScreen
import com.viscit.yogamate.ui.feature.achievement.AchievementScreen
import com.viscit.yogamate.ui.feature.auth.ChooseAuthScreen
import com.viscit.yogamate.ui.feature.auth.LoginScreen
import com.viscit.yogamate.ui.feature.auth.RegisterScreen
import com.viscit.yogamate.ui.feature.home.HomeScreen
import com.viscit.yogamate.ui.feature.onboarding.OnboardingScreen
import com.viscit.yogamate.ui.feature.profile.ProfileScreen
import com.viscit.yogamate.ui.feature.yoga.CompleteScreen
import com.viscit.yogamate.ui.feature.yoga.SessionScreen
import com.viscit.yogamate.ui.feature.yoga.SetupScreen
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted

// Route registry — satu tempat agar tidak ada string route tersebar.
object Routes {
    const val ONBOARDING = "onboarding/intro"
    const val AUTH_METHOD = "auth/method"
    const val AUTH_REGISTER = "auth/register"
    const val AUTH_LOGIN = "auth/login"
    const val HOME = "home"
    const val ACTIVITY = "activity"        // screen Figma "Riwayat", label tab: Aktivitas
    const val ACHIEVEMENT = "achievement"
    const val PROFILE = "profile"
    const val YOGA_SETUP = "yoga/setup"
    const val YOGA_SESSION = "yoga/session"
    const val YOGA_COMPLETE = "yoga/complete"

    val TABS = listOf(HOME, ACTIVITY, ACHIEVEMENT)
}

private data class TabSpec(val route: String, val label: String, val iconRes: Int, val iconActiveRes: Int)

private val TABS = listOf(
    TabSpec(Routes.HOME, "Beranda", com.viscit.yogamate.R.drawable.ic_nav_beranda, com.viscit.yogamate.R.drawable.ic_nav_beranda),
    TabSpec(Routes.ACTIVITY, "Aktivitas", com.viscit.yogamate.R.drawable.ic_nav_aktivitas, com.viscit.yogamate.R.drawable.ic_nav_aktivitas),
    TabSpec(Routes.ACHIEVEMENT, "Pencapaian", com.viscit.yogamate.R.drawable.ic_nav_pencapaian, com.viscit.yogamate.R.drawable.ic_nav_pencapaian),
)

// Bottom navigation tepat 3 tab (Beranda, Aktivitas, Pencapaian) — tanpa Profil (ketentuan pengguna).
// Pill hijau = tab aktif; ikon nav memakai aset PNG asli dari Figma dengan tint warna.
@Composable
fun YmBottomBar(nav: NavHostController) {
    val current by nav.currentBackStackEntryAsState()
    val route = current?.destination?.route
    Surface(color = Color(0xFFFFFCF7)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            TABS.forEach { tab ->
                val selected = route == tab.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).clickable {
                        // pilih ulang tab aktif tidak menumpuk back stack
                        if (!selected) {
                            // Keluar dari Profil dulu (tanpa menyimpan state-nya) agar
                            // Profil tidak ikut ter-save/restore dalam back stack tab.
                            if (route == Routes.PROFILE) nav.popBackStack()
                            nav.navigate(tab.route) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(56.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Lime else Color.Transparent),
                    ) {
                        Image(
                            painterResource(tab.iconRes),
                            contentDescription = tab.label,
                            colorFilter = ColorFilter.tint(if (selected) Ink else Muted),
                            modifier = Modifier.height(24.dp),
                        )
                    }
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) Ink else Muted,
                    )
                }
            }
        }
    }
}

// Start destination dari status onboarding + login yang tersimpan lokal.
@Composable
fun YogaMateApp(nav: NavHostController = rememberNavController()) {
    val start = when {
        !AppSession.onboardingDone -> Routes.ONBOARDING
        !AppSession.loggedIn -> Routes.AUTH_METHOD
        else -> Routes.HOME
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NavHost(
            navController = nav,
            startDestination = start,
            modifier = Modifier.weight(1f),
            // pindah halaman tanpa animasi.
            // snap() (bukan EnterTransition.None) supaya transisi selesai di frame pertama
            // dan layar lama langsung dibuang — None masih menampilkan overlap 1-6 frame
            // (AnimatedContent di Navigation Compose, lihat issuetracker 550924680).
            enterTransition = { fadeIn(snap()) },
            exitTransition = { fadeOut(snap()) },
            popEnterTransition = { fadeIn(snap()) },
            popExitTransition = { fadeOut(snap()) },
        ) {
            composable(Routes.ONBOARDING) { OnboardingScreen(
                onFinish = {
                    AppSession.completeOnboarding()
                    nav.navigate(Routes.AUTH_METHOD) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                },
                onLogin = {
                    AppSession.completeOnboarding()
                    nav.navigate(Routes.AUTH_LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                },
            ) }
            composable(Routes.AUTH_METHOD) { ChooseAuthScreen(
                onRegister = { nav.navigate(Routes.AUTH_REGISTER) },
                onLogin = { nav.navigate(Routes.AUTH_LOGIN) },
            ) }
            composable(Routes.AUTH_REGISTER) { RegisterScreen(
                onSuccess = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.AUTH_METHOD) { inclusive = true } }
                },
                onLogin = { nav.navigate(Routes.AUTH_LOGIN) },
            ) }
            composable(Routes.AUTH_LOGIN) { LoginScreen(
                onSuccess = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.AUTH_METHOD) { inclusive = true } }
                },
                onRegister = { nav.navigate(Routes.AUTH_REGISTER) },
            ) }
            composable(Routes.HOME) { HomeScreen(
                onProfile = { nav.navigate(Routes.PROFILE) },
                onStartYoga = { nav.navigate(Routes.YOGA_SETUP) },
                onOpenHistory = { nav.navigate(Routes.ACTIVITY) },
            ) }
            composable(Routes.ACTIVITY) { ActivityScreen(
                onProfile = { nav.navigate(Routes.PROFILE) },
                onStartYoga = { nav.navigate(Routes.YOGA_SETUP) },
            ) }
            composable(Routes.ACHIEVEMENT) { AchievementScreen(
                onProfile = { nav.navigate(Routes.PROFILE) },
                onOpenHistory = { nav.navigate(Routes.ACTIVITY) },
            ) }
            // Link internal Profil keluar lewat popBackStack() dulu (buang state Profil),
            // lalu navigate dengan pola tab — mencegah stack [tab, PROFIL, tab] yang
            // membuat navigasi bottom bar macet/Profil "menguasai" tab.
            composable(Routes.PROFILE) { ProfileScreen(
                onBack = { nav.popBackStack() },
                onOpenHistory = {
                    nav.popBackStack()
                    nav.navigate(Routes.ACTIVITY) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onOpenAchievement = {
                    nav.popBackStack()
                    nav.navigate(Routes.ACHIEVEMENT) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            ) }
            composable(Routes.YOGA_SETUP) { SetupScreen(
                onBack = { nav.popBackStack() },
                onClose = { nav.popBackStack() },
                onStartSession = { nav.navigate(Routes.YOGA_SESSION) },
            ) }
            composable(Routes.YOGA_SESSION) { SessionScreen(
                onBack = { nav.popBackStack() },
                onClose = { nav.popBackStack() },
                onComplete = { nav.navigate(Routes.YOGA_COMPLETE) { popUpTo(Routes.YOGA_SETUP) { inclusive = true } } },
            ) }
            composable(Routes.YOGA_COMPLETE) { CompleteScreen(
                onSeeHistory = {
                    nav.navigate(Routes.ACTIVITY) { popUpTo(Routes.HOME) }
                },
                onBackHome = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                },
                onBack = { nav.popBackStack() },
            ) }
        }
        // Bottom bar tampil di 3 tab; di Profil juga tampil (sesuai node Figma 104:2837) tanpa tab aktif.
        // State back stack dibaca DI SINI (leaf), bukan di YogaMateApp: kalau dibaca di atas,
        // YogaMateApp ikut recompose tiap navigasi dan builder lambda berubah membuat
        // NavHost me-rebuild graph-nya (remember(route, startDestination, builder)).
        BottomBarIfVisible(nav)
    }
}

@Composable
private fun BottomBarIfVisible(nav: NavHostController) {
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    if (route != null && (route in Routes.TABS || route == Routes.PROFILE)) {
        YmBottomBar(nav)
    }
}
