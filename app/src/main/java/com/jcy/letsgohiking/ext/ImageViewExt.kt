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
    var thisUrl = url
    if (thisUrl.isNullOrEmpty()) {
        thisUrl=""
    }
    Glide.with(this).load(thisUrl).into(this)
}

fun ImageView.getProfileImage(userId: String){
    val firebaseFirestore = FirebaseFirestore.getInstance()
    var url : String? = ""
    firebaseFirestore.collection("users")
        .document(userId)
        .get()
        .addOnCompleteListener {
            url = it.result["profileImageUrl"].toString()
//            url = ""

            if (!url.isNullOrEmpty()) {
                Glide.with(this).load(url).circleCrop()
                    .placeholder(R.drawable.ic_profile).into(this)
            }
        }
        .addOnFailureListener {
            Log.e("Image Url error", it.toString())
        }



}