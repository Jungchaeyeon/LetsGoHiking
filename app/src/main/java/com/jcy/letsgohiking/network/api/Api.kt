package com.jcy.letsgohiking.network.api

import com.jcy.letsgohiking.home.tab2.model.ResultSearchImage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


interface Api {
    @GET("/v1/search/image")
    fun getMountainImages(
        @Header("X-Naver-Client-Id") clientId: String = "Av1fZDBpnofuZHD7npGf",
        @Header("X-Naver-Client-Secret") clientSecret: String = "7UKKHa2Nzm",
        @Query("query") query: String,
        @Query("display") display: Int?= null,
        @Query("start") start: Int? = null
    ): Call<ResultSearchImage>

//    @GET(Url.GET_TMAP_LOCATION)
//    suspend fun getSearchLocation(
//        @Header("appKey") appKey: String = Key.TMAP_API,
//        @Query("version") version: Int = 1,
//        @Query("callback") callback: String? = null,
//        @Query("count") count: Int = 20,
//        @Query("searchKeyword") keyword: String,
//        @Query("areaLLCode") areaLLCode: String? = null,
//        @Query("areaLMCode") areaLMCode: String? = null,
//        @Query("resCoordType") resCoordType: String? = null,
//        @Query("searchType") searchType: String? = null,
//        @Query("multiPoint") multiPoint: String? = null,
//        @Query("searchtypCd") searchtypCd: String? = null,
//        @Query("radius") radius: String? = null,
//        @Query("reqCoordType") reqCoordType: String? = null,
//        @Query("centerLon") centerLon: String? = null,
//        @Query("centerLat") centerLat: String? = null
//    ): Response<SearchResponse>
}