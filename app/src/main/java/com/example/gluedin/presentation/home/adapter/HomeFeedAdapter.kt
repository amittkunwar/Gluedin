package com.example.gluedin.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gluedin.databinding.ItemContentRailBinding
import com.example.gluedin.databinding.ItemHeroBannerBinding
import com.example.gluedin.domain.model.ContentItem
import com.example.gluedin.domain.model.ContentRail
import com.example.gluedin.domain.model.HeroBanner
import com.example.gluedin.domain.model.HomeFeed

sealed interface HomeFeedSection {
    data class Hero(val banner: HeroBanner) : HomeFeedSection
    data class Rail(val rail: ContentRail) : HomeFeedSection
}

class HomeFeedAdapter(
    private val onContentClick: (ContentItem) -> Unit,
    private val onHeroPlayClick: (HeroBanner) -> Unit,
    private val onHeroDetailsClick: (HeroBanner) -> Unit
) : ListAdapter<HomeFeedSection, RecyclerView.ViewHolder>(DiffCallback) {

    private val sharedViewPool = RecyclerView.RecycledViewPool()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeFeedSection.Hero -> VIEW_TYPE_HERO
            is HomeFeedSection.Rail -> VIEW_TYPE_RAIL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HERO -> {
                val binding = ItemHeroBannerBinding.inflate(inflater, parent, false)
                HeroViewHolder(binding, onHeroPlayClick, onHeroDetailsClick)
            }
            VIEW_TYPE_RAIL -> {
                val binding = ItemContentRailBinding.inflate(inflater, parent, false)
                RailViewHolder(binding, sharedViewPool, onContentClick)
            }
            else -> throw IllegalArgumentException("Unsupported view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val section = getItem(position)) {
            is HomeFeedSection.Hero -> (holder as HeroViewHolder).bind(section.banner)
            is HomeFeedSection.Rail -> (holder as RailViewHolder).bind(section.rail)
        }
    }

    fun submitHomeFeed(feed: HomeFeed) {
        val sections = mutableListOf<HomeFeedSection>()
        if (feed.heroBanner.id.isNotEmpty()) {
            sections.add(HomeFeedSection.Hero(feed.heroBanner))
        }
        feed.rails.forEach { rail ->
            sections.add(HomeFeedSection.Rail(rail))
        }
        submitList(sections)
    }

    class HeroViewHolder(
        private val binding: ItemHeroBannerBinding,
        private val onHeroPlayClick: (HeroBanner) -> Unit,
        private val onHeroDetailsClick: (HeroBanner) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hero: HeroBanner) {
            binding.tvHeroTitle.text = hero.title
            binding.tvHeroDescription.text = hero.description
            binding.tvHeroCategory.text = hero.category
            binding.tvHeroRating.text = hero.rating

            binding.ivHeroBanner.load(hero.bannerUrl) {
                crossfade(true)
                placeholder(com.example.gluedin.R.drawable.bg_card_placeholder)
                error(com.example.gluedin.R.drawable.bg_card_placeholder)
            }

            binding.btnHeroPlay.setOnClickListener {
                onHeroPlayClick(hero)
            }

            binding.btnHeroDetails.setOnClickListener {
                onHeroDetailsClick(hero)
            }
        }
    }

    class RailViewHolder(
        private val binding: ItemContentRailBinding,
        viewPool: RecyclerView.RecycledViewPool,
        private val onContentClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val itemAdapter = ContentItemAdapter(onContentClick)

        init {
            binding.rvRailItems.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = itemAdapter
                setRecycledViewPool(viewPool)
                setHasFixedSize(true)
            }
        }

        fun bind(rail: ContentRail) {
            binding.tvRailTitle.text = rail.title
            itemAdapter.submitList(rail.items)
        }
    }

    companion object {
        private const val VIEW_TYPE_HERO = 0
        private const val VIEW_TYPE_RAIL = 1

        private val DiffCallback = object : DiffUtil.ItemCallback<HomeFeedSection>() {
            override fun areItemsTheSame(oldItem: HomeFeedSection, newItem: HomeFeedSection): Boolean {
                return when {
                    oldItem is HomeFeedSection.Hero && newItem is HomeFeedSection.Hero ->
                        oldItem.banner.id == newItem.banner.id
                    oldItem is HomeFeedSection.Rail && newItem is HomeFeedSection.Rail ->
                        oldItem.rail.id == newItem.rail.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: HomeFeedSection, newItem: HomeFeedSection): Boolean =
                oldItem == newItem
        }
    }
}
