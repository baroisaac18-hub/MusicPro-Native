package com.musicpro.player.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musicpro.player.data.Song
import com.musicpro.player.theme.MusicProColors
import com.musicpro.player.ui.home.SongRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    songs: List<Song>,
    onPlaySong: (Song) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredSongs = remember(query, songs) {
        if (query.isBlank()) emptyList()
        else songs.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.album.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search songs, artists, albums...", color = MusicProColors.TextTertiary)
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, null, tint = MusicProColors.TextSecondary)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Filled.Close, "Clear", tint = MusicProColors.TextSecondary)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MusicProColors.TextPrimary,
                unfocusedTextColor = MusicProColors.TextPrimary,
                focusedBorderColor = MusicProColors.Primary,
                unfocusedBorderColor = MusicProColors.SurfaceVariantLight,
                cursorColor = MusicProColors.Primary
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isNotBlank() && filteredSongs.isNotEmpty()) {
            Text(
                "${filteredSongs.size} result${if (filteredSongs.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelLarge,
                color = MusicProColors.TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyColumn {
            items(filteredSongs) { song ->
                SongRow(song = song, onClick = { onPlaySong(song) })
            }
        }

        if (query.isBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Search, null,
                        tint = MusicProColors.TextTertiary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Search your music library",
                        color = MusicProColors.TextTertiary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else if (filteredSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No results for \"$query\"",
                    color = MusicProColors.TextTertiary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
