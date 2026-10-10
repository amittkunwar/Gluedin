package com.example.gluedin.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gluedin.databinding.ItemContentCardBinding
import com.example.gluedin.domain.model.ContentItem

/**
 * Adapter for the horizontally scrolling items inside a Content Rail.
 * Uses [ListAdapter] with [DiffUtil] for optimal rendering and payload calculations.
 */
class ContentItemAdapter(
    private val onItemClick: (ContentItem) -> Unit
) : ListAdapter<ContentItem, ContentItemAdapter.ContentItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentItemViewHolder {
        val binding = ItemContentCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContentItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ContentItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContentItemViewHolder(
        private val binding: ItemContentCardBinding,
        private val onItemClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContentItem) {
            binding.tvTitle.text = item.title
            binding.tvCategory.text = "${item.category} • ${item.releaseYear}"
            binding.tvBadge.text = item.rating

            // Load poster image with Coil
            binding.ivThumbnail.load(item.thumbnailUrl) {
                crossfade(true)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ContentItem>() {
            override fun areItemsTheSame(oldItem: ContentItem, newItem: ContentItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ContentItem, newItem: ContentItem): Boolean =
                oldItem == newItem
        }
    }
}
