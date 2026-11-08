package com.viscit.yogamate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.viscit.yogamate.data.AppSession
import com.viscit.yogamate.ui.navigation.YogaMateApp
import com.viscit.yogamate.ui.theme.YogamateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppSession.init(this)
        setContent {
            YogamateTheme {
                YogaMateApp()
            }
        }
    }
}
