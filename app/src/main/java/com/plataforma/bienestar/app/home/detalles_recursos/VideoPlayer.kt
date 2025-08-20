package com.plataforma.bienestar.app.home.detalles_recursos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoId: String,
    onPlayerReady: (YouTubePlayer) -> Unit = {},
    onVideoStarted: () -> Unit = {},
    onVideoEnded: () -> Unit = {}
) {
    AndroidView(
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false
                initialize(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0f)
                            onPlayerReady(youTubePlayer)
                        }

                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            when (state) {
                                PlayerConstants.PlayerState.PLAYING -> onVideoStarted()
                                PlayerConstants.PlayerState.ENDED -> onVideoEnded()
                                else -> {}
                            }
                        }
                    },
                    true
                )
            }
        },
        modifier = modifier
    )
}

fun extractYouTubeId(url: String): String {
    val patterns = listOf(
        "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*",
        "(?<=v=)[^#\\&\\?]*",
        "(?<=be/)[^#\\&\\?]*"
    )

    patterns.forEach { pattern ->
        val compiled = java.util.regex.Pattern.compile(pattern)
        val matcher = compiled.matcher(url)
        if (matcher.find()) return matcher.group()
    }

    return url
}