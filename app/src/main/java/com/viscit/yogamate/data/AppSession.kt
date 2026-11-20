package com.viscit.yogamate.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.util.Locale

/** UI state backed by Firebase Auth and per-user Cloud Firestore documents. */
object AppSession {
    private const val TAG = "AppSession"
    private lateinit var prefs: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var profileListener: ListenerRegistration? = null
    private var historyListener: ListenerRegistration? = null
    private var achievementListener: ListenerRegistration? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    var onboardingDone by mutableStateOf(false)
        private set
    var loggedIn by mutableStateOf(false)
        private set
    var userName by mutableStateOf("")
        private set
    var userEmail by mutableStateOf("")
        private set
    var userGoal by mutableStateOf("Lebih fleksibel")
        private set
    var history by mutableStateOf<List<HistoryItem>>(emptyList())
        private set
    var achievements by mutableStateOf(Achievements.compute(emptyList()))
        private set

    fun init(context: Context) {
        Poses.loadFrom(context)
        YogaSessionState.ensurePose()
        prefs = context.applicationContext.getSharedPreferences("yogamate", Context.MODE_PRIVATE)
        onboardingDone = prefs.getBoolean("onboarding_done", false)
        // Start Firebase accounts without prototype profile or history data.
        prefs.edit()
            .remove("user_password")
            .remove("logged_in")
            .remove("user_name")
            .remove("user_email")
            .remove("user_goal")
            .remove("history")
            .apply()
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.let { user ->
            loggedIn = true
            userEmail = user.email.orEmpty()
            userName = user.displayName.orEmpty().ifBlank { user.email?.substringBefore("@") ?: "Yogamate" }
        }
        authListener?.let(auth::removeAuthStateListener)
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            loggedIn = user != null
            if (user == null) {
                detachCloudListeners()
                userName = ""
                userEmail = ""
                userGoal = "Lebih fleksibel"
                history = emptyList()
                achievements = Achievements.compute(emptyList())
                prefs.edit().remove("history").apply()
            } else {
                userEmail = user.email.orEmpty()
                userName = user.displayName.orEmpty().ifBlank { user.email?.substringBefore("@") ?: "Yogamate" }
                attachCloudListeners(user)
            }
        }
        auth.addAuthStateListener(authListener!!)
    }

    private fun userRef(user: FirebaseUser) = db.collection("users").document(user.uid)

    private fun detachCloudListeners() {
        profileListener?.remove(); profileListener = null
        historyListener?.remove(); historyListener = null
        achievementListener?.remove(); achievementListener = null
    }

    private fun attachCloudListeners(user: FirebaseUser) {
        detachCloudListeners()
        val ref = userRef(user)
        profileListener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) { Log.w(TAG, "Profile listener failed", error); return@addSnapshotListener }
            if (snapshot == null || !snapshot.exists()) {
                saveProfileDocument(user, user.displayName.orEmpty(), "Lebih fleksibel")
                return@addSnapshotListener
            }
            userName = snapshot.getString("name").orEmpty().ifBlank { user.displayName.orEmpty() }
                .ifBlank { user.email?.substringBefore("@") ?: "Yogamate" }
            userEmail = user.email.orEmpty()
            userGoal = snapshot.getString("goal") ?: "Lebih fleksibel"
        }
        historyListener = ref.collection("history")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { Log.w(TAG, "History listener failed", error); return@addSnapshotListener }
                history = snapshot?.documents.orEmpty().mapNotNull(::historyFromDocument)
                val calculated = Achievements.compute(history)
                achievements = calculated
                persistAchievements(user, calculated)
            }
        achievementListener = ref.collection("achievements").addSnapshotListener { snapshot, error ->
            if (error != null) { Log.w(TAG, "Achievement listener failed", error); return@addSnapshotListener }
            val saved = snapshot?.documents.orEmpty().associateBy { it.id }
            achievements = Achievements.compute(history).map { calculated ->
                val doc = saved[calculated.code] ?: return@map calculated
                calculated.copy(
                    progress = (doc.getLong("progress") ?: calculated.progress.toLong()).toInt(),
                    target = (doc.getLong("target") ?: calculated.target.toLong()).toInt(),
                )
            }
        }
    }

    private fun persistAchievements(user: FirebaseUser, items: List<Achievement>) {
        val batch = db.batch()
        val collection = userRef(user).collection("achievements")
        items.forEach { item ->
            batch.set(collection.document(item.code), mapOf(
                "code" to item.code,
                "title" to item.title,
                "subtitle" to item.subtitle,
                "icon" to item.icon.name,
                "progress" to item.progress,
                "target" to item.target,
                "unit" to item.unit,
                "achieved" to item.achieved,
                "updatedAt" to System.currentTimeMillis(),
            ), SetOptions.merge())
        }
        batch.commit().addOnFailureListener { Log.w(TAG, "Achievement sync failed", it) }
    }

    private fun historyFromDocument(doc: DocumentSnapshot): HistoryItem? {
        return try {
            HistoryItem(
                id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                poseName = doc.getString("poseName") ?: return null,
                level = doc.getString("level")?.let { runCatching { Level.valueOf(it) }.getOrNull() } ?: Level.PEMULA,
                dayOfMonth = (doc.getLong("dayOfMonth") ?: return null).toInt(),
                month = (doc.getLong("month") ?: return null).toInt(),
                year = (doc.getLong("year") ?: return null).toInt(),
                timeLabel = doc.getString("timeLabel") ?: "",
                dayLabel = doc.getString("dayLabel") ?: "",
                minutes = (doc.getLong("minutes") ?: 0).toInt(),
                accuracy = (doc.getLong("accuracy") ?: 0).toInt(),
            )
        } catch (e: Exception) {
            Log.w(TAG, "Ignoring malformed history item ${doc.id}", e)
            null
        }
    }

    fun completeOnboarding() {
        onboardingDone = true
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }

    fun register(name: String, email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) { onResult("Akun Firebase tidak ditemukan."); return@addOnSuccessListener }
                user.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build())
                    .addOnCompleteListener {
                        userName = name.trim()
                        userEmail = user.email.orEmpty()
                        userRef(user).set(
                            mapOf("name" to name.trim(), "email" to user.email, "goal" to "Lebih fleksibel", "createdAt" to System.currentTimeMillis()),
                            SetOptions.merge(),
                        ).addOnFailureListener { Log.w(TAG, "Could not initialize user profile", it) }
                        onResult(null)
                    }
            }
            .addOnFailureListener { onResult(authMessage(it)) }
    }

    fun login(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(authMessage(it)) }
    }

    fun logout() { auth.signOut() }

    fun updateProfile(name: String, goal: String, email: String, newPassword: String? = null, onResult: (String?) -> Unit = {}) {
        val user = auth.currentUser
        if (user == null) { onResult("Sesi login berakhir. Silakan masuk kembali."); return }
        val normalizedEmail = email.trim()
        fun saveDocument() {
            userRef(user).set(mapOf("name" to name.trim(), "email" to (user.email ?: normalizedEmail), "goal" to goal), SetOptions.merge())
                .addOnSuccessListener { userName = name.trim(); userEmail = user.email.orEmpty(); userGoal = goal; onResult(null) }
                .addOnFailureListener { onResult("Profil tidak berhasil disimpan. Periksa koneksi internet.") }
        }
        fun updatePasswordIfNeeded() {
            if (newPassword.isNullOrBlank()) { saveDocument(); return }
            user.updatePassword(newPassword)
                .addOnSuccessListener { saveDocument() }
                .addOnFailureListener { onResult(authMessage(it)) }
        }
        fun updateEmailIfNeeded() {
            if (normalizedEmail.equals(user.email, ignoreCase = true)) { updatePasswordIfNeeded(); return }
            user.updateEmail(normalizedEmail)
                .addOnSuccessListener { updatePasswordIfNeeded() }
                .addOnFailureListener { onResult(authMessage(it)) }
        }
        if (user.displayName != name.trim()) {
            user.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build())
                .addOnSuccessListener { updateEmailIfNeeded() }
                .addOnFailureListener { onResult(authMessage(it)) }
        } else updateEmailIfNeeded()
    }

    fun addHistory(item: HistoryItem) {
        history = listOf(item) + history
        val user = auth.currentUser ?: return
        val data = mapOf(
            "id" to item.id, "poseName" to item.poseName, "level" to item.level.name,
            "dayOfMonth" to item.dayOfMonth, "month" to item.month, "year" to item.year,
            "timeLabel" to item.timeLabel, "dayLabel" to item.dayLabel,
            "minutes" to item.minutes, "accuracy" to item.accuracy,
            "createdAt" to item.id,
        )
        userRef(user).collection("history").document(item.id.toString()).set(data)
            .addOnSuccessListener { persistAchievements(user, Achievements.compute(history)) }
            .addOnFailureListener { Log.w(TAG, "History sync failed", it) }
    }

    private fun saveProfileDocument(user: FirebaseUser, name: String, goal: String) {
        userRef(user).set(mapOf("name" to name, "email" to user.email, "goal" to goal), SetOptions.merge())
            .addOnFailureListener { Log.w(TAG, "Profile initialization failed", it) }
    }

    private fun authMessage(error: Exception): String {
        val code = (error as? FirebaseAuthException)?.errorCode.orEmpty().uppercase(Locale.ROOT)
        Log.e(TAG, "Firebase Authentication failed: ${code.ifBlank { "UNKNOWN" }}", error)
        return when {
            code.contains("EMAIL_ALREADY_IN_USE") || error.message?.contains("email address is already in use", true) == true ->
                "Email sudah terdaftar. Silakan masuk."
            code.contains("OPERATION_NOT_ALLOWED") ->
                "Login Email/Password belum aktif di Firebase Console. Aktifkan di Authentication → Sign-in method."
            code.contains("CONFIGURATION_NOT_FOUND") ->
                "Konfigurasi Firebase Authentication belum lengkap. Periksa project dan provider Email/Password."
            code.contains("INVALID_EMAIL") -> "Format email belum benar."
            code.contains("WEAK_PASSWORD") -> "Kata sandi terlalu lemah. Gunakan kata sandi yang lebih kuat."
            code.contains("INVALID_CREDENTIAL") || code.contains("WRONG_PASSWORD") || code.contains("USER_NOT_FOUND") ->
                "Email atau kata sandi salah."
            code.contains("NETWORK_REQUEST_FAILED") || error.message?.contains("network", true) == true ->
                "Tidak dapat terhubung ke Firebase. Periksa internet emulator/perangkat."
            code.contains("TOO_MANY_REQUESTS") -> "Terlalu banyak percobaan. Tunggu sebentar lalu coba lagi."
            code.contains("REQUIRES_RECENT_LOGIN") || error.message?.contains("recent login", true) == true ->
                "Demi keamanan, keluar lalu masuk kembali sebelum mengubah email atau kata sandi."
            else -> "Autentikasi gagal (${code.ifBlank { "UNKNOWN" }}). Lihat Logcat untuk detail."
        }
    }

}

object YogaSessionState {
    var step by mutableStateOf(1)
    var level by mutableStateOf(Level.PEMULA)

    // State tetap Compose-observable, tapi tidak ada pose dummy sebelum seed dimuat.
    private var currentPose by mutableStateOf<Pose?>(null)
    var pose: Pose
        get() = currentPose ?: error("Pose belum diinisialisasi dari seed")
        set(value) { currentPose = value }

    var durationMin by mutableStateOf(1)
    var resultAccuracy by mutableStateOf(0)

    fun ensurePose() {
        if (currentPose == null) currentPose = Poses.ALL.first()
    }

    fun reset() {
        step = 1
        level = Level.PEMULA
        pose = Poses.ALL.first()
        durationMin = 1
        resultAccuracy = 0
    }
}