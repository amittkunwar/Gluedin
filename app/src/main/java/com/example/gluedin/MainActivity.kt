package com.example.gluedin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gluedin.databinding.ActivityMainBinding
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.HeroBanner
import com.example.gluedin.presentation.common.ServiceLocator
import com.example.gluedin.presentation.common.UiState
import com.example.gluedin.presentation.home.HomeViewModel
import com.example.gluedin.presentation.home.adapter.HomeFeedAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: HomeViewModel by viewModels {
        ServiceLocator.provideHomeViewModelFactory(this)
    }

    private lateinit var homeFeedAdapter: HomeFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        homeFeedAdapter = HomeFeedAdapter(
            onContentClick = { item -> navigateToDetail(item) },
            onHeroPlayClick = { hero -> navigateToPlayer(hero.toContentItem()) },
            onHeroDetailsClick = { hero -> navigateToDetail(hero.toContentItem()) }
        )

        binding.rvHomeFeed.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = homeFeedAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.loadHomeFeed()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: UiState<com.example.gluedin.domain.model.HomeFeed>) {
        when (state) {
            is UiState.Loading -> {
                binding.viewLoading.visibility = View.VISIBLE
                binding.viewError.visibility = View.GONE
                binding.viewEmpty.visibility = View.GONE
                binding.rvHomeFeed.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.viewLoading.visibility = View.GONE
                binding.viewError.visibility = View.GONE
                binding.viewEmpty.visibility = View.GONE
                binding.rvHomeFeed.visibility = View.VISIBLE
                homeFeedAdapter.submitHomeFeed(state.data)
            }
            is UiState.Empty -> {
                binding.viewLoading.visibility = View.GONE
                binding.viewError.visibility = View.GONE
                binding.viewEmpty.visibility = View.VISIBLE
                binding.rvHomeFeed.visibility = View.GONE
            }
            is UiState.Error -> {
                binding.viewLoading.visibility = View.GONE
                binding.viewError.visibility = View.VISIBLE
                binding.viewEmpty.visibility = View.GONE
                binding.rvHomeFeed.visibility = View.GONE
                binding.tvErrorMessage.text = state.message
            }
        }
    }

    private fun navigateToDetail(item: ContentItem) {
        val intent = com.example.gluedin.presentation.detail.DetailActivity.createIntent(this, item)
        startActivity(intent)
    }

    private fun navigateToPlayer(item: ContentItem) {
        val intent = com.example.gluedin.presentation.player.PlayerActivity.createIntent(this, item)
        startActivity(intent)
    }
}
