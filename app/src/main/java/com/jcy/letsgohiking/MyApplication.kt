package com.jcy.letsgohiking

import android.app.Application
import com.facebook.stetho.Stetho
import com.jcy.letsgohiking.di.networkModule
import com.jcy.letsgohiking.di.repositoryModule
import com.jcy.letsgohiking.di.viewModelModule
import com.jcy.letsgohiking.network.adapter.RxErrorHandler
import com.kakao.sdk.common.KakaoSdk
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    companion object {
        private var instance: MyApplication? = null
        lateinit var prefs: PreferenceUtil
        val globalApplicationContext: MyApplication
            get() {
                checkNotNull(instance) { "this application does not inherit CoinoneApplication" }
                return instance as MyApplication
            }

        fun getString(stringId: Int): String = globalApplicationContext.getString(stringId)

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = PreferenceUtil(applicationContext)

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    networkModule, repositoryModule, viewModelModule
                )
            )
        }

        RxJavaPlugins.setErrorHandler { RxErrorHandler().processErrorHandler(it) }
        RxJavaPlugins.lockdown()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}