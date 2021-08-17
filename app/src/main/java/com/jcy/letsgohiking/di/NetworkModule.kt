package com.jcy.letsgohiking.di

import com.jcy.letsgohiking.BuildConfig
import com.jcy.letsgohiking.network.adapter.ApiCallAdapterFactory
import com.jcy.letsgohiking.network.api.Api
import com.jcy.letsgohiking.util.ConnectivityHelper
import com.jcy.letsgohiking.network.adapter.OkHttpClient
import com.jcy.letsgohiking.util.GsonFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
fun getFirebaseNetworkModule(url: String): Module = module {
    single {
        OkHttpClient.createOkHttpClient()
    }
    single {
        Retrofit.Builder()
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(url)
            .build()
    }
}