package com.jcy.letsgohiking.home.tab2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jcy.letsgohiking.databinding.ItemBookmarkMntnBinding
import com.jcy.letsgohiking.home.tab2.model.MountainItem


class MountainBookmarkAdapter(val callback:(MountainItem)->Unit): ListAdapter<MountainItem, MountainBookmarkAdapter.ItemViewHolder>(
    diffUtil
) {
    var itemPosition: Int = -1
    inner class ItemViewHolder(val binding: ItemBookmarkMntnBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(mountainItem: MountainItem){


            binding.mntnName.text = mountainItem.mntnName
            binding.mntnHeight.text = mountainItem.mntnHeight.toString()
            binding.mntnSubinfo.text = mountainItem.mntnInfo
            binding.mntnLocation.text = mountainItem.mntnLocation
            binding.bookmarkBtn.isSelected = mountainItem.isBookmarked

            Glide.with(binding.mntnImg.context)
                .load(mountainItem.mntnImg)
                .into(binding.mntnImg)

            binding.root.setOnClickListener {
                callback(mountainItem)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemBookmarkMntnBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
        itemPosition = holder.adapterPosition
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