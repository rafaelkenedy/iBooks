package com.rafael.ibooks.di

import com.rafael.ibooks.presentation.viewmodel.BookDetailViewModel
import com.rafael.ibooks.presentation.viewmodel.BookListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel { BookListViewModel(get(), get(), get()) }
    viewModel { BookDetailViewModel(get()) }
}