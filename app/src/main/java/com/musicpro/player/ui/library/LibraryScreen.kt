package com.musicpro.player.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musicpro.player.data.Song
import com.musicpro.player.data.SortMode
import com.musicpro.player.theme.MusicProColors
import com.musicpro.player.ui.home.SongRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    songs: List<Song>,
    sortMode: SortMode,
    onSortChange: (SortMode) -> Unit,
    onPlaySong: (Song) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    val sortedSongs = remember(songs, sortMode) {
        when (sortMode) {
            SortMode.TITLE -> songs.sortedBy { it.title.lowercase() }
            SortMode.ARTIST -> songs.sortedBy { it.artist.lowercase() }
            SortMode.ALBUM -> songs.sortedBy { it.album.lowercase() }
            SortMode.DURATION -> songs.sortedByDescending { it.duration }
            SortMode.DATE_ADDED -> songs.sortedByDescending { it.dateAdded }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with sort
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Library",
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

            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(
                        Icons.Filled.SwapVert, "Sort",
                        tint = MusicProColors.TextSecondary
                    )
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (mode) {
                                        SortMode.TITLE -> "Title"
                                        SortMode.ARTIST -> "Artist"
                                        SortMode.ALBUM -> "Album"
                                        SortMode.DURATION -> "Duration"
                                        SortMode.DATE_ADDED -> "Date Added"
                                    },
                                    color = if (mode == sortMode) MusicProColors.Primary else MusicProColors.TextPrimary
                                )
                            },
                            onClick = {
                                onSortChange(mode)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        if (sortedSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No music found on device",
                    color = MusicProColors.TextTertiary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sortedSongs) { song ->
                    SongRow(song = song, onClick = { onPlaySong(song) })
                }
            }
        }
    }
}
