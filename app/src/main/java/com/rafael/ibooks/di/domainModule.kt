package com.rafael.ibooks.di

import com.rafael.ibooks.domain.repository.GeminiRepositoryImpl
import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.usecase.GenerateBookTitlesUseCase
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.SearchBooksUseCase
import com.rafael.ibooks.utils.GENRE_MAP
import org.koin.dsl.module

val domainModule = module {

    single { BookRepositoryImpl(remoteDataSource = get()) }
    single { GeminiRepositoryImpl(remoteDataSource = get()) }
    factory { SearchBooksUseCase(repository = get()) }
    factory { GetRecentBooksUseCase(repository = get()) }
    factory { GetBookDetailsUseCase(get()) }
    factory { GenerateBookTitlesUseCase(get()) }
    single { GENRE_MAP }
}