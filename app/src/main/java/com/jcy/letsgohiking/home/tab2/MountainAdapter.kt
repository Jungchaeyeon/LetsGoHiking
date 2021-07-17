package com.jcy.letsgohiking.home.tab2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jcy.letsgohiking.databinding.ItemRecommendCourseBinding

class MountainAdapter: ListAdapter<MountainItem, MountainAdapter.ItemViewHolder>(diffUtil) {
    inner class ItemViewHolder(val binding: ItemRecommendCourseBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mountainItem: MountainItem){

            binding.mntnName.text = mountainItem.mntnName
            binding.mntnHeight.text = mountainItem.mntnHeight.toString()
            binding.mntnLocation.text = mountainItem.mntnLocation

            Glide.with(binding.mntnImg.context)
                .load(mountainItem.mntnImg)
                .into(binding.mntnImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater =LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemRecommendCourseBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object{
        val diffUtil=  object : DiffUtil.ItemCallback<MountainItem>() {
            override fun areItemsTheSame(oldItem: MountainItem, newItem: MountainItem): Boolean {
                return oldItem.mntnName == newItem.mntnName
            }

            override fun areContentsTheSame(oldItem: MountainItem, newItem: MountainItem): Boolean {
               return oldItem == newItem
            }

        }
    }
}