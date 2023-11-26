package com.fadhlalhafizh.storyapplication.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fadhlalhafizh.storyapplication.data.api.response.ListStoryItem
import com.fadhlalhafizh.storyapplication.databinding.ItemlistStoryBinding
import com.fadhlalhafizh.storyapplication.utils.withDateFormat
import com.fadhlalhafizh.storyapplication.view.detailstory.DetailStoryActivity

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListStoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding =
            ItemlistStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null)
            holder.bind(data)
    }

    class ListStoryViewHolder(private val binding: ItemlistStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(response.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = response.name
            binding.tvItemDesc.text = response.description
            binding.tvCreatedItem.text = response.createdAt?.withDateFormat()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.NAME, response.name)
                intent.putExtra(DetailStoryActivity.DESCRIPTION, response.description)
                intent.putExtra(DetailStoryActivity.PHOTO_URL, response.photoUrl)
                intent.putExtra(DetailStoryActivity.CREATE_AT, response.createdAt)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.ivItemPhoto, "photo"),
                        androidx.core.util.Pair(binding.tvItemName, "name"),
                        androidx.core.util.Pair(binding.tvItemDesc, "description"),
                        androidx.core.util.Pair(binding.tvCreatedItem, "create_date")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}