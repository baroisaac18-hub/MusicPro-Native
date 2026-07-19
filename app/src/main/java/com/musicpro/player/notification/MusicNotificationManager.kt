package com.musicpro.player.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.musicpro.player.MainActivity
import com.musicpro.player.data.Song

class MusicNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "music_pro_playback"
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var mediaSession: MediaSessionCompat? = null

    fun createMediaSession(callback: MediaSessionCompat.Callback) {
        mediaSession = MediaSessionCompat(context, "MusicPro").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setCallback(callback)
            isActive = true
        }
    }

    fun updatePlaybackState(state: Int, position: Long) {
        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, position, 1f)
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_SEEK_TO
                )
                .build()
        )
    }

    fun buildNotification(song: Song, isPlaying: Boolean): Notification {
        createChannel()

        val contentIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                createPendingIntent("PAUSE")
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                createPendingIntent("PLAY")
            )
        }

        val albumArt = loadAlbumArt(song)
        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(albumArt)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(song.album)
            .setContentIntent(contentIntent)
            .setOngoing(isPlaying)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(style)
            .addAction(android.R.drawable.ic_media_previous, "Previous", createPendingIntent("PREVIOUS"))
            .addAction(playPauseAction)
            .addAction(android.R.drawable.ic_media_next, "Next", createPendingIntent("NEXT"))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    fun showNotification(song: Song, isPlaying: Boolean) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(song, isPlaying))
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun release() {
        cancelNotification()
        mediaSession?.release()
        mediaSession = null
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun loadAlbumArt(song: Song): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, song.uri)
            val art = retriever.embeddedPicture
            retriever.release()
            art?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        } catch (e: Exception) {
            null
        }
    }
}
