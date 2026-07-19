package com.musicpro.player.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.musicpro.player.player.AudioPlayer

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val player = AudioPlayer.getInstance(context)
        when (intent.action) {
            "PLAY" -> player.play()
            "PAUSE" -> player.pause()
            "PREVIOUS" -> player.skipToPrevious()
            "NEXT" -> player.skipToNext()
        }
    }
}
