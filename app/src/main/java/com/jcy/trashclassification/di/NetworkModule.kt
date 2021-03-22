package com.jcy.trashclassification.di

import com.jcy.trashclassification.BuildConfig
import com.jcy.trashclassification.network.adapter.ApiCallAdapterFactory
import com.jcy.trashclassification.network.api.Api
import com.jcy.trashclassification.util.ConnectivityHelper
import com.jcy.trashclassification.network.adapter.OkHttpClient
import com.jcy.trashclassification.util.GsonFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        OkHttpClient.createOkHttpClient()
    }
    single {
        Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(get())
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.get()))
                .addCallAdapterFactory(ApiCallAdapterFactory())
                .build()
    }
    single { (get() as Retrofit).create(Api::class.java) }
    single { ConnectivityHelper.get(androidContext()) }

}