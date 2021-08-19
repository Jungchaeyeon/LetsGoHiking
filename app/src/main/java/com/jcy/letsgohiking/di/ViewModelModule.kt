package com.jcy.letsgohiking.di

import com.jcy.letsgohiking.home.login.LoginViewModel
import com.jcy.letsgohiking.home.record.model.RecordViewModel
import com.jcy.letsgohiking.home.tab1.HomeViewModel
import com.jcy.letsgohiking.home.tab2.DetailMountainViewModel
import com.jcy.letsgohiking.home.tab2.ReviewViewModel
import com.jcy.letsgohiking.home.tab2.SearchViewModel
import com.jcy.letsgohiking.home.tab5.MoreInfoViewmodel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { RecordViewModel() }
    viewModel { ReviewViewModel() }
    viewModel { MoreInfoViewmodel(get()) }
    viewModel { LoginViewModel(get(),get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SearchViewModel() }
    viewModel { DetailMountainViewModel() }
}