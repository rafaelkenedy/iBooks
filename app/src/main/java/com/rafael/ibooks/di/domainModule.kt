package com.rafael.ibooks.di

import com.rafael.ibooks.domain.repository.GeminiRepositoryImpl
import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.usecase.GenerateBookTitlesUseCase
import com.rafael.ibooks.domain.usecase.GetBookDetailsUseCase
import com.rafael.ibooks.domain.usecase.GetRecentBooksUseCase
import com.rafael.ibooks.domain.usecase.ObserveWantToReadBooksUseCase
import com.rafael.ibooks.domain.usecase.RemoveWantToReadBookUseCase
import com.rafael.ibooks.domain.usecase.SaveWantToReadBookUseCase
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
    factory { ObserveWantToReadBooksUseCase(get()) }
    factory { SaveWantToReadBookUseCase(get()) }
    factory { RemoveWantToReadBookUseCase(get()) }
    single { GENRE_MAP }
}
