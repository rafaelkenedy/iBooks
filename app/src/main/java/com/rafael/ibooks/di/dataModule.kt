package com.rafael.ibooks.di

import com.rafael.ibooks.data.remote.datasource.BookRemoteDatasourceImpl
import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.repository.GeminiRepositoryImpl
import com.rafael.ibooks.domain.repository.IBookRepository
import com.rafael.ibooks.domain.repository.IGeminiRepository
import org.koin.dsl.module

val dataModule = module {
    single<IGeminiRepository> { GeminiRepositoryImpl(get()) }
    single<IBookRepository> { BookRepositoryImpl(get()) }
    single<IBookRemoteDataSource> { BookRemoteDatasourceImpl(get()) }
}