package com.jcy.trashclassification.di

import com.jcy.trashclassification.repository.ApiRepository
import com.jcy.trashclassification.repository.local.RepositoryCached
import com.jcy.trashclassification.repository.local.RepositoryDevicePreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<RepositoryCached> { RepositoryDevicePreference(androidContext()) }   //not used
    single { ApiRepository(get()) }
}