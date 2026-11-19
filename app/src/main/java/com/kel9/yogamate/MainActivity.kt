package com.kel9.yogamate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kel9.yogamate.data.AppSession
import com.kel9.yogamate.ui.navigation.YogaMateApp
import com.kel9.yogamate.ui.theme.YogamateTheme

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
