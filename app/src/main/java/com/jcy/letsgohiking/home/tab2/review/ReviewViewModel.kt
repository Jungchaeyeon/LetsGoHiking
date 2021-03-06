package com.jcy.letsgohiking.home.tab2.review

import com.google.firebase.firestore.FirebaseFirestore
import com.hdh.base.viewmodel.BaseViewModel

import com.jcy.letsgohiking.home.tab2.model.Review
import com.jcy.letsgohiking.util.Log

class ReviewViewModel(): BaseViewModel() {
    val fireStoreDB = FirebaseFirestore.getInstance()
    val reviewList = ArrayList<Review>()

    fun setMountainReview(mntnName: String, reviewItem: Review, respon: (Boolean) -> Unit ){
        Thread{
            fireStoreDB.collection(mntnName).document(reviewItem.writer).set(reviewItem)
                .addOnCompleteListener {
                    respon.invoke(true)
                }
                .addOnFailureListener {
                    respon.invoke(false)
                }
        }.start()

    }
    fun deleteMountainReview(mntnName: String, writer: String, respon: (Boolean) -> Unit ){
        Thread{
            fireStoreDB.collection(mntnName).document(writer).delete()
                .addOnCompleteListener {
                    Log.e("삭제성공", "내 리뷰가 삭제되었습니다.")
                    respon.invoke(true)
                }
                .addOnFailureListener {
                    respon.invoke(false)
                }
        }.start()
    }
    fun getMountainReview(mntnName: String, respon: (Boolean) -> Unit){
        fireStoreDB.collection(mntnName).addSnapshotListener { list, error ->
            reviewList.clear()
            if(list == null) respon.invoke(false)
            if (list != null) {
                for(data in list.documents){
                    var review = data.toObject(Review::class.java)

                    review.takeIf { it?.review?.isNotEmpty()!! }?.let { reviewList.add(it) }
                }
                respon.invoke(true)
            }
        }
    }
}