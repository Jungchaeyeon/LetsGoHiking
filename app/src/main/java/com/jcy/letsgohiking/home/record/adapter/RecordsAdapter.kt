package com.jcy.letsgohiking.home.record.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcy.letsgohiking.databinding.ItemRecordMainPrevBinding
import com.jcy.letsgohiking.home.record.model.Record


class RecordsAdapter(val callback:(Record)->Unit): ListAdapter<Record, RecordsAdapter.ItemViewHolder>(
    diffUtil
) {
    inner class ItemViewHolder(val binding: ItemRecordMainPrevBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(recordItem: Record){

            binding.mntnName.text = recordItem.mntnName
            binding.mvhikingDate.text = recordItem.hikingDate.drop(6)
            binding.mvhikingWith.text = recordItem.hikingWith

            binding.root.setOnClickListener {
                callback(recordItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemRecordMainPrevBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil=  object : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem.mntnName == newItem.mntnName
            }

            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
               return oldItem == newItem
            }

        }
    }
}