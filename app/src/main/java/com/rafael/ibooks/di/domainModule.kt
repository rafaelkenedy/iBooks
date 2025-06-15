package com.rafael.ibooks.di

import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import org.koin.dsl.module

val domainModule = module {

    single { BookRepositoryImpl(remoteDataSource = get()) }
    factory { SearchBooksUseCase(repository = get()) }
    factory { GetRecentBooksUseCase(repository = get()) }
    factory { GetBookDetailsUseCase(get()) }
}