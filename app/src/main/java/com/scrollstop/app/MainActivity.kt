package com.scrollstop.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.scrollstop.app.ui.navigation.AppNavigation
import com.scrollstop.app.ui.theme.ScrollStopTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScrollStopTheme {
                AppNavigation()
            }
        }
    }
}
