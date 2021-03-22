package com.jcy.trashclassification.ext

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("setImage")
fun ImageView.setImage(Image: Int) {
    this.setImageResource(Image)
}


@BindingAdapter("app:loadUrl")
fun ImageView.loadUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        return
    }

    Glide.with(this).load(url).into(this)
}