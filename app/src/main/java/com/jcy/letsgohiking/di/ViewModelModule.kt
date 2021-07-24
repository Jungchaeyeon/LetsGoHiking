package com.jcy.letsgohiking.di

import com.jcy.letsgohiking.home.tab2.SearchViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel() }
}