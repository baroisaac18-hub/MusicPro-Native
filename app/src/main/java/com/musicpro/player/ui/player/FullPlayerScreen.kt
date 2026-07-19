package com.musicpro.player.ui.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color as ComposeColor
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.musicpro.player.data.Song
import com.musicpro.player.data.RepeatMode
import com.musicpro.player.theme.MusicProColors

@Composable
fun FullPlayerScreen(
    songs: List<Song>,
    currentIndex: Int,
    currentSong: Song,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    repeatMode: RepeatMode,
    shuffleEnabled: Boolean,
    favoriteIds: Set<Long>,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onClose: () -> Unit,
    onBookmark: (Pair<Song, Long>) -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit
) {
    val isFavorite = currentSong.id in favoriteIds
    var showBookmarkConfirm by remember { mutableStateOf(false) }

    // Handle drag down to close
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MusicProColors.Background)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, _ -> }
            }
    ) {
        // Background gradient layers
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        MusicProColors.Primary.copy(alpha = 0.25f),
                        MusicProColors.Background,
                        MusicProColors.Surface
                    )
                )
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown, "Close",
                        tint = MusicProColors.TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    "Now Playing",
                    style = MaterialTheme.typography.labelLarge,
                    color = MusicProColors.TextSecondary
                )

                IconButton(onClick = { onToggleFavorite(currentSong.id) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MusicProColors.Favorite else MusicProColors.TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Album Art with glass effect
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .shadow(20.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MusicProColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentSong.albumArtUri ?: android.net.Uri.EMPTY)
                        .crossfade(true)
                        .size(600)
                        .build(),
                    contentDescription = "Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Glass overlay
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(ComposeColor.Transparent, ComposeColor.Transparent, MusicProColors.Surface.copy(alpha = 0.3f))
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Song Title & Artist
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentSong.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MusicProColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentSong.artist,
                    style = MaterialTheme.typography.titleMedium,
                    color = MusicProColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
                if (currentSong.album.isNotBlank()) {
                    Text(
                        text = currentSong.album,
                        style = MaterialTheme.typography.bodySmall,
                        color = MusicProColors.TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seekbar
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                    onValueChange = { onSeek((it * duration).toLong()) },
                    colors = SliderDefaults.colors(
                        thumbColor = MusicProColors.Primary,
                        activeTrackColor = MusicProColors.Primary,
                        inactiveTrackColor = MusicProColors.SurfaceVariantLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatDuration(currentPosition),
                        style = MaterialTheme.typography.labelSmall,
                        color = MusicProColors.TextTertiary
                    )
                    Text(
                        formatDuration(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MusicProColors.TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Play Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        Icons.Filled.Shuffle,
                        "Shuffle",
                        tint = if (shuffleEnabled) MusicProColors.Primary else MusicProColors.TextTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(onClick = onPrevious) {
                    Icon(
                        Icons.Filled.SkipPrevious,
                        "Previous",
                        tint = MusicProColors.TextPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Play/Pause
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MusicProColors.Primary)
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MusicProColors.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next
                IconButton(onClick = onNext) {
                    Icon(
                        Icons.Filled.SkipNext,
                        "Next",
                        tint = MusicProColors.TextPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Repeat
                Box(contentAlignment = Alignment.Center) {
                    IconButton(onClick = onToggleRepeat) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.ONE -> androidx.compose.material.icons.Icons.Filled.RepeatOne
                                else -> Icons.Filled.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != RepeatMode.NONE) MusicProColors.Primary else MusicProColors.TextTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (repeatMode == RepeatMode.ONE) {
                        Text(
                            "1",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MusicProColors.Primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bookmark button
            if (isPlaying) {
                OutlinedButton(
                    onClick = {
                        onBookmark(Pair(currentSong, currentPosition))
                        showBookmarkConfirm = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MusicProColors.Primary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.verticalGradient(listOf(
                            MusicProColors.Primary.copy(alpha = 0.5f),
                            MusicProColors.Primary.copy(alpha = 0.5f)
                        ))
                    ),
                    modifier = Modifier.fillMaxWidth(0.6f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Filled.Bookmark, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark This Moment")
                }

                if (showBookmarkConfirm) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bookmarked!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MusicProColors.Success
                    )
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2000)
                        showBookmarkConfirm = false
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
