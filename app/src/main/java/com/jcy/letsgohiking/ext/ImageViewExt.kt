package com.jcy.letsgohiking.ext

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.util.Log


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

fun ImageView.getProfileImage(userId: String){
    val firebaseFirestore = FirebaseFirestore.getInstance()
    var url = ""
    firebaseFirestore.collection("users")
        .document(userId)
        .get()
        .addOnCompleteListener {
            url = it.result["profileImageUrl"].toString()

            if (!url.isNullOrEmpty()) {
                Glide.with(this).load(url).circleCrop()
                    .placeholder(R.drawable.ic_profile).into(this)
            }
        }
        .addOnFailureListener {
            Log.e("Image Url error", it.toString())
        }



}