package com.example.gluedin.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.gluedin.databinding.ActivityContentDetailBinding
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.presentation.common.ServiceLocator
import com.example.gluedin.presentation.common.UiState
import com.example.gluedin.presentation.player.PlayerActivity
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentDetailBinding

    private val viewModel: DetailViewModel by viewModels {
        ServiceLocator.provideDetailViewModelFactory(this)
    }

    private var currentItem: ContentItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contentId = intent.getStringExtra(EXTRA_CONTENT_ID) ?: ""

        @Suppress("DEPRECATION")
        val passedItem = intent.getSerializableExtra(EXTRA_CONTENT_ITEM) as? ContentItem

        setupListeners()
        observeUiState()

        if (passedItem != null) {
            bindContent(passedItem)
        } else if (contentId.isNotEmpty()) {
            viewModel.loadContentDetail(contentId)
        } else {
            Toast.makeText(this, "Content not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPlayVideo.setOnClickListener {
            currentItem?.let { item ->
                val intent = PlayerActivity.createIntent(this, item)
                startActivity(intent)
            } ?: Toast.makeText(this, "Media is not ready to play", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            bindContent(state.data)
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@DetailActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                        is UiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun bindContent(item: ContentItem) {
        currentItem = item
        with(binding) {
            progressBar.visibility = View.GONE
            tvDetailTitle.text = item.title
            tvDetailDescription.text = item.description
            tvBadgeCategory.text = item.category
            tvBadgeDuration.text = item.duration
            tvBadgeRating.text = item.rating
            tvBadgeYear.text = item.releaseYear

            ivDetailBackdrop.load(item.bannerUrl.ifEmpty { item.thumbnailUrl }) {
                crossfade(true)
                placeholder(com.example.gluedin.R.drawable.bg_card_placeholder)
                error(com.example.gluedin.R.drawable.bg_card_placeholder)
            }
        }
    }

    companion object {
        private const val EXTRA_CONTENT_ID = "extra_content_id"
        private const val EXTRA_CONTENT_ITEM = "extra_content_item"

        fun createIntent(context: Context, item: ContentItem): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_CONTENT_ID, item.id)
                putExtra(EXTRA_CONTENT_ITEM, item)
            }
        }
    }
}
