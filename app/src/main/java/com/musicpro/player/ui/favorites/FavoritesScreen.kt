package com.musicpro.player.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musicpro.player.data.Song
import com.musicpro.player.theme.MusicProColors
import com.musicpro.player.ui.home.SongRow

@Composable
fun FavoritesScreen(
    songs: List<Song>,
    favoriteIds: Set<Long>,
    onToggleFavorite: (Long) -> Unit,
    onPlaySong: (Song) -> Unit
) {
    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.FavoriteBorder, null,
                    tint = MusicProColors.TextTertiary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No favorites yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MusicProColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Tap the heart icon while playing to save favorites",
                    style = MaterialTheme.typography.bodySmall,
                    color = MusicProColors.TextTertiary
                )
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Favorite, null,
                tint = MusicProColors.Favorite,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    "Favorites",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MusicProColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${songs.size} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MusicProColors.TextSecondary
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(songs) { song ->
                SongRow(song = song, onClick = { onPlaySong(song) })
            }
        }
    }
}
