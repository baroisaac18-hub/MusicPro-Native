package com.musicpro.player.data

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri? = null,
    val year: Int = 0,
    val trackNumber: Int = 0,
    val size: Long = 0,
    val dateAdded: Long = 0,
    val isFavorite: Boolean = false
)

enum class RepeatMode {
    NONE, ALL, ONE
}

enum class SortMode {
    TITLE, ARTIST, ALBUM, DURATION, DATE_ADDED
}

data class Bookmark(
    val songId: Long,
    val title: String,
    val artist: String,
    val positionMs: Long,
    val createdAt: Long = System.currentTimeMillis()
)
