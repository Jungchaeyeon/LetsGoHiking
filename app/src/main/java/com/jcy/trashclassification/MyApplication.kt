package com.jcy.trashclassification

import android.app.Application
import com.facebook.stetho.Stetho
import com.jcy.trashclassification.di.networkModule
import com.jcy.trashclassification.di.repositoryModule
import com.jcy.trashclassification.di.viewModelModule
import com.jcy.trashclassification.network.adapter.RxErrorHandler
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    companion object {
        private var instance: MyApplication? = null
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
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}