package com.jcy.letsgohiking.home.tab2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcy.letsgohiking.databinding.ItemOnlineReviewBinding

import com.jcy.letsgohiking.home.tab2.model.Review

class MountainReviewAdapter(): ListAdapter<Review, MountainReviewAdapter.ReviewItemViewHolder>(
    diffUtil
) {
    inner class ReviewItemViewHolder(val binding: ItemOnlineReviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind( review: Review){
            binding.review.text = review.review

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewItemViewHolder {
        return ReviewItemViewHolder(ItemOnlineReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ReviewItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<Review>(){
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }

        }
    }

}