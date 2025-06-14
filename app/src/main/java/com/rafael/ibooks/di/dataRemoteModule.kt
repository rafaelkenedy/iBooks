package com.rafael.ibooks.di

import com.rafael.ibooks.data.remote.datasource.BookRemoteDatasourceImpl
import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.data.remote.utils.ApiConstants
import com.rafael.ibooks.data.remote.utils.WebServiceFactory
import com.rafael.ibooks.data.remote.service.IGoogleBooksApi
import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.repository.IBookRepository
import org.koin.dsl.module

val dataRemoteModule = module {
    single { WebServiceFactory.providerOkHttpClient() }

    single {
        WebServiceFactory.createWebService<IGoogleBooksApi>(
            okHttpClient = get(),
            url = ApiConstants.GOOGLE_BOOKS_BASE_URL
        )
    }

    single<IBookRemoteDataSource> { BookRemoteDatasourceImpl(get()) }

    single<IBookRepository> { BookRepositoryImpl(get()) }
}