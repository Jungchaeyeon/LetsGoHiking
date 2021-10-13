package com.jcy.letsgohiking.home.tab2

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.Url
import com.jcy.letsgohiking.home.tab2.model.MountainItem
import com.jcy.letsgohiking.home.tab2.model.NaverSearchImage
import com.jcy.letsgohiking.home.tab2.model.ResultSearchImage
import com.jcy.letsgohiking.home.tab2.model.Review
import com.jcy.letsgohiking.network.api.Api
import com.jcy.letsgohiking.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailMountainViewModel(): BaseViewModel() {
    val liveMountainItem = MutableLiveData<MountainItem>()
    val liveMountainImageItems = MutableLiveData<ArrayList<NaverSearchImage>>()
    val mountainImageViewList = arrayListOf<NaverSearchImage>()
    val fireStoreDB = FirebaseFirestore.getInstance()
    val reviewList = ArrayList<Review>()

    fun addAllItems(){
        liveMountainImageItems.value = mountainImageViewList
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
                    val imageModel = NaverSearchImage(imgUrl = item.link, originLink = item.link)
                    mountainImageViewList.add(imageModel)
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