package com.jcy.letsgohiking.home.tab2

import android.media.Image
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.Key
import com.jcy.letsgohiking.Url
import com.jcy.letsgohiking.home.tab2.model.MountainItem
import com.jcy.letsgohiking.home.tab2.model.ResultSearchImage
import com.jcy.letsgohiking.home.tab2.model.Review
import com.jcy.letsgohiking.network.api.Api
import com.jcy.letsgohiking.repository.ApiRepository
import com.jcy.letsgohiking.util.Log
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailMountainViewModel(): BaseViewModel() {
    val liveMountainItem = MutableLiveData<MountainItem>()
    val liveMountainImageItems = MutableLiveData<ArrayList<String>>()
    val mountainImageViewList = arrayListOf<String>()
    val fireStoreDB = FirebaseFirestore.getInstance()
    val reviewList = ArrayList<Review>()

    fun addAllItems(){
        liveMountainImageItems.value = mountainImageViewList
    }
    fun getMountainReview(mntnName: String, respon: (Boolean) -> Unit){
        fireStoreDB.collection(mntnName).addSnapshotListener { list, error ->
            reviewList.clear()
            if(list == null) respon.invoke(false)
            for(data in list!!.documents){
                var review = data.toObject(Review::class.java)
                if (review != null) {
                    reviewList.add(review)
                }
            }
            respon.invoke(true)
        }
    }

    fun getMountainImages(mntnName: String, respon: (Boolean) -> Unit ){

        val retrofit = Retrofit.Builder()
            .baseUrl(Url.NAVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(Api::class.java)
        val callGetSearchImage = api.getMountainImages(query = mntnName)
        callGetSearchImage.enqueue(object: Callback<ResultSearchImage>{
            override fun onResponse(
                call: Call<ResultSearchImage>,
                response: Response<ResultSearchImage>
            ) {
                for(item in response.body()?.items!!){
                    mountainImageViewList.add(item.link)
                }
                addAllItems()
                respon.invoke(true)
            }

            override fun onFailure(call: Call<ResultSearchImage>, t: Throwable) {
                Log.e("결과", "실패")
                respon.invoke(false)
            }
        })

    }
}