package com.viscit.yogamate.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

// State aplikasi UI-first: onboarding/login + riwayat disimpan di SharedPreferences.
// Belum ada backend/auth/DB di codebase — integrasi nyata adalah pekerjaan terpisah (plan.md).
object AppSession {
    private lateinit var prefs: SharedPreferences

    var onboardingDone by mutableStateOf(false)
        private set
    var loggedIn by mutableStateOf(false)
        private set
    var userName by mutableStateOf("")
        private set
    var history by mutableStateOf<List<HistoryItem>>(emptyList())
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences("yogamate", Context.MODE_PRIVATE)
        onboardingDone = prefs.getBoolean("onboarding_done", false)
        loggedIn = prefs.getBoolean("logged_in", false)
        userName = prefs.getString("user_name", "") ?: ""
        history = decode(prefs.getString("history", "[]") ?: "[]")
    }

    fun completeOnboarding() {
        onboardingDone = true
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }

    fun login(name: String) {
        loggedIn = true
        userName = name
        prefs.edit().putBoolean("logged_in", true).putString("user_name", name).apply()
    }

    fun logout() {
        loggedIn = false
        prefs.edit().putBoolean("logged_in", false).apply()
    }

    fun addHistory(item: HistoryItem) {
        history = listOf(item) + history
        prefs.edit().putString("history", encode(history)).apply()
    }

    private fun encode(items: List<HistoryItem>): String {
        val arr = JSONArray()
        items.forEach {
            arr.put(JSONObject().apply {
                put("id", it.id); put("poseName", it.poseName); put("level", it.level.name)
                put("dayOfMonth", it.dayOfMonth); put("month", it.month); put("year", it.year)
                put("timeLabel", it.timeLabel); put("dayLabel", it.dayLabel)
                put("minutes", it.minutes); put("accuracy", it.accuracy)
            })
        }
        return arr.toString()
    }

    private fun decode(json: String): List<HistoryItem> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            HistoryItem(
                id = o.getLong("id"),
                poseName = o.getString("poseName"),
                level = Level.valueOf(o.getString("level")),
                dayOfMonth = o.getInt("dayOfMonth"),
                month = o.getInt("month"),
                year = o.getInt("year"),
                timeLabel = o.getString("timeLabel"),
                dayLabel = o.getString("dayLabel"),
                minutes = o.getInt("minutes"),
                accuracy = o.getInt("accuracy"),
            )
        }
    } catch (_: Exception) {
        emptyList()
    }
}

// State sesi yoga yang sedang berjalan (setup step 1-4 → session → complete), in-memory saja.
object YogaSessionState {
    var step by mutableStateOf(1)            // 1..4 (Mulai Yoga 1..4)
    var level by mutableStateOf(Level.PEMULA)
    var pose by mutableStateOf(Poses.POSE_POHON)
    var durationMin by mutableStateOf(1)
    var resultAccuracy by mutableStateOf(0)  // disimulasikan saat sesi selesai

    fun reset() {
        step = 1
        level = Level.PEMULA
        pose = Poses.POSE_POHON
        durationMin = 1
        resultAccuracy = 0
    }
}
