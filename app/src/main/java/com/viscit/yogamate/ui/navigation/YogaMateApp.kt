package com.viscit.yogamate.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleOut
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.data.YogaSessionState
import com.viscit.yogamate.ui.feature.activity.ActivityScreen
import com.viscit.yogamate.ui.feature.achievement.AchievementScreen
import com.viscit.yogamate.ui.feature.education.EducationScreen
import com.viscit.yogamate.ui.feature.auth.ChooseAuthScreen
import com.viscit.yogamate.ui.feature.auth.LoginScreen
import com.viscit.yogamate.ui.feature.auth.RegisterScreen
import com.viscit.yogamate.ui.feature.home.HomeScreen
import com.viscit.yogamate.ui.feature.onboarding.OnboardingScreen
import com.viscit.yogamate.ui.feature.profile.EditProfileScreen
import com.viscit.yogamate.ui.feature.profile.ProfileScreen
import com.viscit.yogamate.ui.feature.yoga.CompleteScreen
import com.viscit.yogamate.ui.feature.yoga.SessionScreen
import com.viscit.yogamate.ui.feature.yoga.SetupScreen
import com.viscit.yogamate.ui.feature.yoga.PoseCatalogScreen
import com.viscit.yogamate.ui.theme.Ink
import com.viscit.yogamate.ui.theme.Lime
import com.viscit.yogamate.ui.theme.Muted

object Routes {
    const val ONBOARDING = "onboarding/intro"
    const val AUTH_METHOD = "auth/method"
    const val AUTH_REGISTER = "auth/register"
    const val AUTH_LOGIN = "auth/login"
    const val HOME = "home"
    const val ACTIVITY = "activity"
    const val ACHIEVEMENT = "achievement"
    const val PROFILE = "profile"
    const val PROFILE_EDIT = "profile/edit"
    const val EDUCATION = "education"
    const val YOGA_SETUP = "yoga/setup"
    const val YOGA_SESSION = "yoga/session"
    const val YOGA_COMPLETE = "yoga/complete"
    const val POSE_CATALOG = "yoga/catalog"

    val TABS = listOf(HOME, ACTIVITY, ACHIEVEMENT)
}

private data class TabSpec(val route: String, val label: String, val iconRes: Int, val iconActiveRes: Int)

private val TABS = listOf(
    TabSpec(Routes.HOME, "Beranda", com.viscit.yogamate.R.drawable.ic_nav_beranda, com.viscit.yogamate.R.drawable.ic_nav_beranda),
    TabSpec(Routes.ACTIVITY, "Aktivitas", com.viscit.yogamate.R.drawable.ic_nav_aktivitas, com.viscit.yogamate.R.drawable.ic_nav_aktivitas),
    TabSpec(Routes.ACHIEVEMENT, "Pencapaian", com.viscit.yogamate.R.drawable.ic_nav_pencapaian, com.viscit.yogamate.R.drawable.ic_nav_pencapaian),
)

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
                            // Tab tujuan sudah ada di back stack (mis. dibuka lewat
                            // "Progres"/"Lihat semua") → cukup kembali ke sana; pola
                            // navigate+restoreState di atasnya macet untuk kasus ini.
                            if (!nav.popBackStack(tab.route, inclusive = false)) {
                                nav.navigate(tab.route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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

/**
 * Setiap tujuan dibungkus background opaque. Tanpa ini, saat predictive back
 * dua komposable hidup bersamaan dan konten layar lama terlihat menembus layar baru.
 */
private fun NavGraphBuilder.screen(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route) { entry ->
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            content(entry)
        }
    }
}

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
            // Navigasi maju: langsung, tanpa fade Compose.
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            // Predictive back (Android 13+ / 15 default): layar sebelumnya langsung
            // terlihat, layar saat ini mengecil mengikuti gestur (panduan resmi).
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { scaleOut(targetScale = 0.9f) },
            // Penting: tanpa ini AnimatedContent menganimasikan perubahan ukuran antar
            // layar dan men-compose dua halaman sekaligus (tampak bertabrakan).
            sizeTransform = { null },
        ) {
            screen(Routes.ONBOARDING) { OnboardingScreen(
                onFinish = {
                    AppSession.completeOnboarding()
                    nav.navigate(Routes.AUTH_METHOD) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                },
                onLogin = {
                    AppSession.completeOnboarding()
                    nav.navigate(Routes.AUTH_LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                },
            ) }
            screen(Routes.AUTH_METHOD) { ChooseAuthScreen(
                onRegister = { nav.navigate(Routes.AUTH_REGISTER) },
                onLogin = { nav.navigate(Routes.AUTH_LOGIN) },
            ) }
            screen(Routes.AUTH_REGISTER) { RegisterScreen(
                onSuccess = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.AUTH_METHOD) { inclusive = true } }
                },
                onLogin = { nav.navigate(Routes.AUTH_LOGIN) },
            ) }
            screen(Routes.AUTH_LOGIN) { LoginScreen(
                onSuccess = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.AUTH_METHOD) { inclusive = true } }
                },
                onRegister = { nav.navigate(Routes.AUTH_REGISTER) },
            ) }
            screen(Routes.HOME) { HomeScreen(
                onProfile = { nav.navigate(Routes.PROFILE) },
                onStartYoga = { nav.navigate(Routes.YOGA_SETUP) },
                onOpenHistory = { nav.navigate(Routes.ACTIVITY) },
                onOpenEducation = { nav.navigate(Routes.EDUCATION) },
            ) }
            screen(Routes.ACTIVITY) { ActivityScreen(
                onProfile = { nav.navigate(Routes.PROFILE) },
                onStartYoga = { nav.navigate(Routes.YOGA_SETUP) },
            ) }
            screen(Routes.ACHIEVEMENT) { AchievementScreen(
                onProfile = { nav.navigate(Routes.PROFILE) },
                onOpenHistory = { nav.navigate(Routes.ACTIVITY) },
            ) }
            screen(Routes.PROFILE_EDIT) { EditProfileScreen(
                onBack = { nav.popBackStack() },
            ) }
            screen(Routes.EDUCATION) { EducationScreen(
                onBack = { nav.popBackStack() },
            ) }
            screen(Routes.POSE_CATALOG) { PoseCatalogScreen(onBack = { nav.popBackStack() }) }
            // Link internal Profil keluar lewat popBackStack() dulu (buang state Profil),
            // lalu navigate dengan pola tab — mencegah stack [tab, PROFIL, tab] yang
            // membuat navigasi bottom bar macet/Profil "menguasai" tab.
            screen(Routes.PROFILE) { ProfileScreen(
                onBack = { nav.popBackStack() },
                onEditProfile = { nav.navigate(Routes.PROFILE_EDIT) },
                onOpenEducation = { nav.navigate(Routes.EDUCATION) },
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
                onOpenPoses = { nav.navigate(Routes.POSE_CATALOG) },
            ) }
            // ← pada alur setup mundur satu step (step 1 = keluar); ✕ keluar ke beranda.
            // Reset progress ditangani onDispose di SetupScreen (tanpa kilatan step 1).
            screen(Routes.YOGA_SETUP) { SetupScreen(
                onBack = {
                    if (YogaSessionState.step > 1) YogaSessionState.step -= 1 else nav.popBackStack()
                },
                onClose = { nav.popBackStack() },
                onStartSession = { nav.navigate(Routes.YOGA_SESSION) },
            ) }
            screen(Routes.YOGA_SESSION) { SessionScreen(
                onBack = { nav.popBackStack() },
                onClose = { nav.popBackStack() },
                onComplete = { nav.navigate(Routes.YOGA_COMPLETE) { popUpTo(Routes.YOGA_SETUP) { inclusive = true } } },
            ) }
            screen(Routes.YOGA_COMPLETE) { CompleteScreen(
                onSeeHistory = {
                    YogaSessionState.reset()
                    nav.navigate(Routes.ACTIVITY) { popUpTo(Routes.HOME) }
                },
                onBackHome = {
                    YogaSessionState.reset()
                    nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                },
                onBack = { nav.popBackStack() },
            ) }
        }
        // Bottom bar tampil di 3 tab; di Profil juga tampil tanpa tab aktif.
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
