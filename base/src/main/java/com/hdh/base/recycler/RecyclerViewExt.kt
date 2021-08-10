package com.hdh.base.recycler

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceAll")
fun RecyclerView.replaceAll(list: List<Any>?) {

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


@Suppress("UNCHECKED_CAST")
@BindingAdapter("addAll")
fun RecyclerView.addAll(list: List<Any>?) {
    if (list == null) {
        return
    }

    when {
        adapter as? BaseDataBindingRecyclerViewAdapter<Any> != null -> {
            (adapter as BaseDataBindingRecyclerViewAdapter<Any>).run {
                addAll(list)
            }
        }
    }
}