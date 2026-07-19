package com.musicpro.player.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.musicpro.player.data.Song
import com.musicpro.player.theme.MusicProColors

@Composable
fun HomeScreen(
    songs: List<Song>,
    onPlaySong: (Song) -> Unit,
    onPlayAll: () -> Unit
) {
    val recentSongs = songs.takeLast(20).reversed()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Welcome Header
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Music",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MusicProColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${songs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MusicProColors.TextSecondary
                        )
                    }
                }
            }
        }

        // Play All Button
        if (songs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(MusicProColors.Primary, MusicProColors.PrimaryDark)
                            )
                        )
                        .clickable { onPlayAll() }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.PlayArrow, "Play",
                        tint = MusicProColors.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Play All",
                        color = MusicProColors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Quick Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.MusicNote,
                    label = "Songs",
                    value = "${songs.size}"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Album,
                    label = "Albums",
                    value = "${songs.map { it.album }.toSet().size}"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Person,
                    label = "Artists",
                    value = "${songs.map { it.artist }.toSet().size}"
                )
            }
        }

        // Recently Added
        if (recentSongs.isNotEmpty()) {
            item {
                Text(
                    "Recently Added",
                    style = MaterialTheme.typography.titleMedium,
                    color = MusicProColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(recentSongs.take(10)) { song ->
                SongRow(song = song, onClick = { onPlaySong(song) }, showIndex = false)
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = modifier,
        color = MusicProColors.SurfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = MusicProColors.Primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = MusicProColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(label, color = MusicProColors.TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
fun SongRow(song: Song, onClick: () -> Unit, showIndex: Boolean = false, index: Int = 0) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.albumArtUri ?: android.net.Uri.EMPTY)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album Art",
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.title,
                    color = MusicProColors.TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.artist,
                    color = MusicProColors.TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                formatDuration(song.duration),
                color = MusicProColors.TextTertiary,
                fontSize = 12.sp
            )
        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private typealias ImageVector = androidx.compose.ui.graphics.vector.ImageVector
private typealias Color = androidx.compose.ui.graphics.Color
