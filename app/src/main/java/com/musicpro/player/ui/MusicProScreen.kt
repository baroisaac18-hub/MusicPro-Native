package com.musicpro.player.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.musicpro.player.data.Song
import com.musicpro.player.data.SortMode
import com.musicpro.player.data.RepeatMode
import com.musicpro.player.player.AudioPlayer
import com.musicpro.player.theme.MusicProColors
import kotlinx.coroutines.launch

@Composable
fun MusicProScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember { AudioPlayer.getInstance(context) }
    val scope = rememberCoroutineScope()

    val currentSong by player.currentSong.collectAsState()
    val isPlaying by player.isPlaying.collectAsState()
    val currentPosition by player.currentPosition.collectAsState()
    val duration by player.duration.collectAsState()
    val repeatMode by player.repeatMode.collectAsState()
    val shuffleEnabled by player.shuffleEnabled.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showFullPlayer by remember { mutableStateOf(false) }
    var allSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var favoriteIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var bookmarks by remember { mutableStateOf<List<Pair<Song, Long>>>(emptyList()) }
    var sortMode by remember { mutableStateOf(SortMode.TITLE) }

    // Load music on first composition
    LaunchedEffect(Unit) {
        val repo = com.musicpro.player.data.MusicRepository(context)
        val songs = repo.loadDeviceMusic()
        allSongs = songs
        if (player.playlist.isEmpty() && songs.isNotEmpty()) {
            player.setPlaylist(songs)
        }
    }

    val tabs = listOf("Home" to Icons.Filled.Home, "Library" to Icons.Filled.LibraryMusic, "Search" to Icons.Filled.Search, "Favorites" to Icons.Filled.Favorite)

    Box(modifier = modifier.fillMaxSize().background(MusicProColors.Background)) {
        // Background gradient
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(MusicProColors.Primary.copy(alpha = 0.08f), Color.Transparent, Color.Transparent)
                )
            )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MusicProColors.Surface,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(
                        text = "MusicPro",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MusicProColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Native Edition",
                        style = MaterialTheme.typography.labelMedium,
                        color = MusicProColors.Primary
                    )
                }
            }

            // Content Area
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        songs = allSongs,
                        onPlaySong = { song ->
                            player.playSong(song)
                        },
                        onPlayAll = {
                            player.setPlaylist(allSongs, 0)
                            player.play()
                        }
                    )
                    1 -> LibraryScreen(
                        songs = allSongs,
                        sortMode = sortMode,
                        onSortChange = { sortMode = it },
                        onPlaySong = { song ->
                            player.setPlaylist(allSongs, allSongs.indexOf(song))
                            player.play()
                        }
                    )
                    2 -> SearchScreen(
                        songs = allSongs,
                        onPlaySong = { song ->
                            player.setPlaylist(allSongs, allSongs.indexOf(song))
                            player.play()
                        }
                    )
                    3 -> FavoritesScreen(
                        songs = allSongs.filter { it.id in favoriteIds },
                        favoriteIds = favoriteIds,
                        onToggleFavorite = { id ->
                            favoriteIds = if (id in favoriteIds) favoriteIds - id else favoriteIds + id
                        },
                        onPlaySong = { song ->
                            val favs = allSongs.filter { it.id in favoriteIds }
                            player.setPlaylist(if (favs.isNotEmpty()) favs else allSongs, 0)
                            player.play()
                        }
                    )
                }
            }

            // Mini Player
            AnimatedVisibility(
                visible = currentSong != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                currentSong?.let { song ->
                    MiniPlayer(
                        song = song,
                        isPlaying = isPlaying,
                        onPlayPause = { player.togglePlayPause() },
                        onNext = { player.skipToNext() },
                        onTap = { showFullPlayer = true }
                    )
                }
            }

            // Tab Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MusicProColors.Surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, (label, icon) ->
                        val selected = selectedTab == index
                        TabItem(
                            icon = if (selected) icon else getOutlinedIcon(icon),
                            label = label,
                            selected = selected,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        }

        // Pull-up Full Player
        AnimatedVisibility(
            visible = showFullPlayer,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            if (showFullPlayer && currentSong != null) {
                FullPlayerScreen(
                    songs = player.playlist,
                    currentIndex = player.getCurrentIndex(),
                    currentSong = currentSong!!,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    repeatMode = repeatMode,
                    shuffleEnabled = shuffleEnabled,
                    favoriteIds = favoriteIds,
                    onPlayPause = { player.togglePlayPause() },
                    onNext = { player.skipToNext() },
                    onPrevious = { player.skipToPrevious() },
                    onSeek = { player.seekTo(it) },
                    onToggleRepeat = { player.toggleRepeat() },
                    onToggleShuffle = { player.toggleShuffle() },
                    onToggleFavorite = { id ->
                        favoriteIds = if (id in favoriteIds) favoriteIds - id else favoriteIds + id
                    },
                    onClose = { showFullPlayer = false },
                    onBookmark = { bookmark ->
                        bookmarks = (bookmarks + bookmark) as List<Pair<Song, Long>>
                    },
                    onSeekForward = { player.seekForward() },
                    onSeekBackward = { player.seekBackward() }
                )
            }
        }
    }
}

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onTap: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        color = MusicProColors.SurfaceVariant.copy(alpha = 0.95f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.albumArtUri ?: android.net.Uri.EMPTY)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album Art",
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Song Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = MusicProColors.TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = MusicProColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Play/Pause
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = MusicProColors.Primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Next
            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = MusicProColors.TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun TabItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) MusicProColors.Primary else MusicProColors.TextTertiary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) MusicProColors.Primary else MusicProColors.TextTertiary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

private fun getOutlinedIcon(filled: ImageVector): ImageVector = when (filled) {
    Icons.Filled.Home -> Icons.Outlined.Home
    Icons.Filled.LibraryMusic -> Icons.Outlined.LibraryMusic
    Icons.Filled.Search -> Icons.Outlined.Search
    Icons.Filled.Favorite -> Icons.Outlined.FavoriteBorder
    else -> filled
}
