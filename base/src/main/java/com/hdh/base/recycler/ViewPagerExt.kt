package com.hdh.base.recycler

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceAll")
fun ViewPager2.replaceAll(list: List<Any>?) {
    if (list == null) {
        return
    }

    when {
        adapter as? BaseDataBindingRecyclerViewAdapter<Any> != null -> {
            (adapter as BaseDataBindingRecyclerViewAdapter<Any>).run {
                replaceAll(list)
            }
        }
    }
}