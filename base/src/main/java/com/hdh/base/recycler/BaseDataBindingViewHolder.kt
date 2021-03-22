package com.hdh.base.recycler

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseDataBindingViewHolder<T : Any, B : ViewDataBinding> : RecyclerView.ViewHolder {

    private lateinit var data: T
    protected lateinit var binding: B

    constructor(layoutId: Int, parent: ViewGroup,
                binding: B = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false))
            : this(binding.root) {
        this.binding = binding
    }

    private constructor(view: View) : super(view)

    protected abstract fun B.onDataBind(data: T)

    fun bindData(data: T) {
        this.data = data

        binding.onDataBind(data)
    }

    fun getData() = data
}