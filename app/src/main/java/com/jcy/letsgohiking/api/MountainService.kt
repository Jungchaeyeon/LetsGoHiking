package com.jcy.letsgohiking.api

import com.jcy.letsgohiking.dao.MountainDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MountainService {
    @GET("trailInfoService/getforeststoryservice")
    fun getMountainList(
        @Query("ServiceKey" , encoded = true) ServiceKey : String,
    ): Call<MountainDTO>

}