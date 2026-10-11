package com.example.gluedin.presentation.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.gluedin.databinding.ActivityVideoPlayerBinding
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.presentation.common.ServiceLocator
import kotlinx.coroutines.launch

@UnstableApi
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding

    private val viewModel: PlayerViewModel by viewModels {
        ServiceLocator.providePlayerViewModelFactory()
    }

    private var exoPlayer: ExoPlayer? = null
    private var playerEventHandler: PlayerEventHandler? = null

    private var mediaUrl: String = ""
    private var mediaTitle: String = ""
    private var playbackPosition: Long = 0L
    private var playWhenReadyState: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableFullscreenMode()

        mediaUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
        mediaTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Now Playing"

        binding.tvPlayerTitle.text = mediaTitle

        setupListeners()
        observeViewModelState()
    }

    private fun enableFullscreenMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupListeners() {
        binding.btnPlayerBack.setOnClickListener {
            finish()
        }

        binding.btnRetryPlayback.setOnClickListener {
            initializePlayer()
        }
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.bufferingProgress.visibility =
                        if (state.isBuffering) View.VISIBLE else View.GONE

                    binding.tvLiveEventBadge.text = "[EVENT] ${state.lastLoggedEvent}"

                    if (state.errorMessage != null) {
                        binding.errorContainer.visibility = View.VISIBLE
                        binding.tvPlayerError.text = state.errorMessage
                    } else {
                        binding.errorContainer.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun initializePlayer() {
        if (mediaUrl.isEmpty()) {
            binding.errorContainer.visibility = View.VISIBLE
            binding.tvPlayerError.text = "No valid video stream URL provided."
            return
        }

        val httpDataSourceFactory = androidx.media3.datasource.DefaultHttpDataSource.Factory()
            .setUserAgent("Gluedin-OTT-Player/1.0 (Android; Linux)")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setAllowCrossProtocolRedirects(true)

        val dataSourceFactory = androidx.media3.datasource.DefaultDataSource.Factory(this, httpDataSourceFactory)
        val mediaSourceFactory = androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory)

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()

        val eventHandler = PlayerEventHandler { event ->
            viewModel.onPlayerEvent(event)
        }
        player.addListener(eventHandler)
        playerEventHandler = eventHandler

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(mediaUrl))
        player.setMediaItem(mediaItem)
        player.playWhenReady = playWhenReadyState
        player.seekTo(playbackPosition)
        player.prepare()

        exoPlayer = player
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReadyState = player.playWhenReady
            playerEventHandler?.let { player.removeListener(it) }
            player.release()
        }
        exoPlayer = null
        playerEventHandler = null
        binding.playerView.player = null
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (exoPlayer == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    companion object {
        private const val EXTRA_VIDEO_URL = "extra_video_url"
        private const val EXTRA_VIDEO_TITLE = "extra_video_title"

        fun createIntent(context: Context, item: ContentItem): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, item.videoUrl)
                putExtra(EXTRA_VIDEO_TITLE, item.title)
            }
        }
    }
}
