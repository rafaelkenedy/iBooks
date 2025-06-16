package com.rafael.ibooks.di

import com.rafael.ibooks.BuildConfig
import com.rafael.ibooks.data.remote.datasource.BookRemoteDatasourceImpl
import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.data.remote.service.IGoogleBooksApi
import com.rafael.ibooks.data.remote.utils.ApiConstants
import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.repository.IBookRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val dataModule = module {

    single {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalUrl = chain.request().url
                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("key", BuildConfig.GOOGLE_BOOKS_API_KEY)
                    .build()
                val newRequest = chain.request().newBuilder()
                    .url(newUrl)
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .build()
    }

    single<IGoogleBooksApi> {
        Retrofit.Builder()
            .baseUrl(ApiConstants.GOOGLE_BOOKS_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IGoogleBooksApi::class.java)
    }

    single<IBookRemoteDataSource> { BookRemoteDatasourceImpl(get()) }
    single<IBookRepository> { BookRepositoryImpl(get()) }
}