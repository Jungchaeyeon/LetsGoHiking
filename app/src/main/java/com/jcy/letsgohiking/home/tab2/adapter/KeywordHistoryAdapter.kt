package com.jcy.letsgohiking.home.tab2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcy.letsgohiking.databinding.ItemKeywordHistoryBinding
import com.jcy.letsgohiking.home.tab2.model.History

class KeywordHistoryAdapter(val historyDeleteClickedListener:(String)->Unit): ListAdapter<History, KeywordHistoryAdapter.HistoryItemViewHolder>(
    diffUtil
) {
    inner class HistoryItemViewHolder(val binding: ItemKeywordHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind( history: History){
            binding.keyword.text = history.mntnName
            binding.deleteBtn.setOnClickListener {
                historyDeleteClickedListener(history.mntnName.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        return HistoryItemViewHolder(ItemKeywordHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<History>(){
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.mntnName == newItem.mntnName
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }

        }
    }

}