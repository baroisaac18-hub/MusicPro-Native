package com.musicpro.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.musicpro.player.theme.MusicProTheme
import com.musicpro.player.ui.MusicProScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicProTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicProScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
