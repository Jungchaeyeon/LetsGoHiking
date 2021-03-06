package com.jcy.letsgohiking.di

import com.jcy.letsgohiking.network.api.Api
import com.jcy.letsgohiking.repository.ApiRepository
import com.jcy.letsgohiking.repository.local.RepositoryCached
import com.jcy.letsgohiking.repository.local.RepositoryDevicePreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

val repositoryModule = module {
    single<RepositoryCached> { RepositoryDevicePreference(androidContext()) }   //not used
    single { ApiRepository(get()) }
}