# AGENTS.md — YogaMate

Single-module Android app: Kotlin + Jetpack Compose (Material 3), package `com.viscit.yogamate`.
Design/spec and past-session notes are in Indonesian: `plan.md`, `docs/ringkasan-implementasi.md` (decisions + device gotchas), `docs/roadmap-implementasi.md`, `docs/YogaMate_PRD_v2.4_Android_Native.md`.

## Build & verify
- Gradle wrapper 9.6.0 (use `gradlew.bat` on Windows). Every dependency/plugin is declared in `gradle/libs.versions.toml` and referenced via `alias(libs.*)` — don't hardcode versions in build scripts.
- `org.gradle.configuration-cache=true`: if a build-script edit seems to have no effect, re-run with `--no-configuration-cache`.
- Verify with `./gradlew assembleDebug` (known green). Output is `app/build/intermediates/apk/debug/app-debug.apk`, marked test-only → install with `adb install -r -t`.
- Release uses R8 (`optimization { enable = true }`) + `isShrinkResources = true`. `app/proguard-rules.pro` is **mandatory** — without it MediaPipe/protobuf crash at runtime. No signing config: sign manually with `zipalign` + `apksigner` (build-tools 36.0.0, `~/.android/debug.keystore` pass `android`) to upgrade-install without losing data. Keep `app/build/outputs/mapping/release/mapping.txt` per release.

## Architecture / entrypoints
- `MainActivity` calls `AppSession.init(this)` before `setContent`. Order matters: the NavHost start destination is derived from `AppSession.onboardingDone` / `loggedIn`.
- One NavHost in `ui/navigation/YogaMateApp.kt`; all route strings live in `object Routes` — add destinations there, not inline. Bottom bar appears on the 3 tabs and `profile`; Detail Riwayat & Panduan are modal bottom sheets, not routes.
- State is local-only: `AppSession` (SharedPreferences "yogamate", history as JSON) plus in-memory `YogaSessionState`. No backend/DB — auth is UI-only and the plaintext password is deliberate; don't "fix" it without a backend task. Session accuracy is simulated.
- `data/engine/PoseEngine.kt` wraps MediaPipe Pose Landmarker (Full) using `assets/pose_landmarker_full.task`, GPU→CPU fallback, and fails gracefully (no crash) on x86 emulators.

## Gotchas — do not "simplify" these
- `YmBottomBar` switches tabs via `popBackStack(tab.route)` before navigating, and links out of `Profile` call `popBackStack()` first. Plain `navigate()` makes tab taps stick / leaves Profile owning the tabs.
- Back-stack state is read in the leaf `BottomBarIfVisible`, not in `YogaMateApp` — reading it higher up recomposes the parent and rebuilds the NavHost graph on every navigation.
- Setup-screen reset runs in `DisposableEffect { onDispose }` with a `continuedToSession` guard, not in button callbacks, to avoid a one-frame flash.
- MediaPipe/protobuf keep rules are in `app/proguard-rules.pro`; `app/src/main/keepRules/rules.keep` is still the empty template.

## Testing
- Only the generated `ExampleUnitTest` / `ExampleInstrumentedTest` exist — no real coverage; `assembleDebug` is the practical verification step.
- Device-test notes (Xiaomi wireless ADB, tap/swipe and ANR quirks) are in `docs/ringkasan-implementasi.md`. Use `android_ui_resolve` for tap coordinates and start swipes at x ≥ 200.
