package com.musicpro.player.player

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.musicpro.player.data.RepeatMode
import com.musicpro.player.data.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = AudioPlayer.getInstance(this).exoPlayer
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        super.onDestroy()
    }
}

class AudioPlayer private constructor(private val context: Context) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )
    }

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()

    var playlist: List<Song> = emptyList()
        private set
    private var currentIndex: Int = -1

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                _isPlaying.value = state == Player.STATE_READY && exoPlayer.playWhenReady
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    val idx = playlist.indexOfFirst {
                        it.uri.toString() == mediaItem?.localConfiguration?.uri?.toString()
                    }
                    if (idx >= 0) {
                        currentIndex = idx
                        _currentSong.value = playlist[idx]
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                _isPlaying.value = playWhenReady
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _isPlaying.value = false
            }
        })
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        playlist = songs
        currentIndex = startIndex.coerceIn(0, songs.lastIndex)

        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        songs.forEach { song ->
            val mediaItem = MediaItem.Builder()
                .setUri(song.uri)
                .setMediaId(song.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .build()
                )
                .build()
            exoPlayer.addMediaItem(mediaItem)
        }

        exoPlayer.seekTo(currentIndex, 0)
        exoPlayer.prepare()
        _currentSong.value = songs[currentIndex]
        _duration.value = songs[currentIndex].duration
    }

    fun play() {
        exoPlayer.playWhenReady = true
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun togglePlayPause() {
        if (exoPlayer.playWhenReady) pause() else play()
    }

    fun playSong(song: Song) {
        val idx = playlist.indexOfFirst { it.id == song.id }
        if (idx >= 0) {
            currentIndex = idx
            exoPlayer.seekTo(idx, 0)
            exoPlayer.playWhenReady = true
            _currentSong.value = song
            _duration.value = song.duration
        }
    }

    fun playSongAt(index: Int) {
        if (index in playlist.indices) {
            currentIndex = index
            exoPlayer.seekTo(index, 0)
            exoPlayer.playWhenReady = true
            _currentSong.value = playlist[index]
            _duration.value = playlist[index].duration
        }
    }

    fun skipToNext() {
        if (playlist.isEmpty()) return
        val nextIndex = when (_repeatMode.value) {
            RepeatMode.ONE -> currentIndex
            else -> {
                if (_shuffleEnabled.value) {
                    (playlist.indices).random()
                } else {
                    if (currentIndex < playlist.lastIndex) currentIndex + 1 else 0
                }
            }
        }
        if (_repeatMode.value == RepeatMode.NONE && !_shuffleEnabled.value && currentIndex >= playlist.lastIndex) {
            pause()
            exoPlayer.seekTo(currentIndex, 0)
            return
        }
        currentIndex = nextIndex
        exoPlayer.seekTo(nextIndex, 0)
        exoPlayer.playWhenReady = true
        _currentSong.value = playlist[nextIndex]
        _duration.value = playlist[nextIndex].duration
    }

    fun skipToPrevious() {
        if (playlist.isEmpty()) return
        val prevIndex = if (_shuffleEnabled.value) {
            (playlist.indices).random()
        } else {
            if (exoPlayer.currentPosition > 3000) currentIndex
            else if (currentIndex > 0) currentIndex - 1 else playlist.lastIndex
        }
        currentIndex = prevIndex
        exoPlayer.seekTo(prevIndex, 0)
        exoPlayer.playWhenReady = true
        _currentSong.value = playlist[prevIndex]
        _duration.value = playlist[prevIndex].duration
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun toggleRepeat(): RepeatMode {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        return _repeatMode.value
    }

    fun toggleShuffle(): Boolean {
        _shuffleEnabled.value = !_shuffleEnabled.value
        return _shuffleEnabled.value
    }

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
    }

    fun setShuffle(enabled: Boolean) {
        _shuffleEnabled.value = enabled
    }

    fun getCurrentIndex(): Int = currentIndex

    fun getCurrentPositionMs(): Long = exoPlayer.currentPosition

    fun release() {
        exoPlayer.release()
    }

    fun seekForward(ms: Long = 10000) {
        val newPos = (exoPlayer.currentPosition + ms).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(newPos)
    }

    fun seekBackward(ms: Long = 10000) {
        val newPos = (exoPlayer.currentPosition - ms).coerceAtLeast(0)
        exoPlayer.seekTo(newPos)
    }

    companion object {
        @Volatile
        private var INSTANCE: AudioPlayer? = null

        fun getInstance(context: Context): AudioPlayer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioPlayer(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
