package com.rafael.ibooks.di

import com.rafael.ibooks.BuildConfig
import com.rafael.ibooks.data.remote.service.IGeminiApi
import com.rafael.ibooks.data.remote.service.IGoogleBooksApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val GoogleBooksQualifier = named("GoogleBooks")
val GeminiQualifier = named("Gemini")

val networkModule = module {

    single(qualifier = GoogleBooksQualifier) {
        provideOkHttpClient(apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY)
    }

    single(qualifier = GeminiQualifier) {
        provideOkHttpClient(apiKey = BuildConfig.GEMINI_API_KEY)
    }

    single(qualifier = GoogleBooksQualifier) {
        provideRetrofit(
            okHttpClient = get(qualifier = GoogleBooksQualifier),
            baseUrl = BuildConfig.GOOGLE_BOOKS_API_BASE_URL
        )
    }

    single(qualifier = GeminiQualifier) {
        provideRetrofit(
            okHttpClient = get(qualifier = GeminiQualifier),
            baseUrl = BuildConfig.GEMINI_API_BASE_URL
        )
    }

    single {
        provideGoogleBooksApi(retrofit = get(qualifier = GoogleBooksQualifier))
    }

    single {
        provideGeminiApi(retrofit = get(qualifier = GeminiQualifier))
    }
}

private fun provideOkHttpClient(apiKey: String): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalUrl = chain.request().url
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("key", apiKey)
                .build()
            val newRequest = chain.request().newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

private fun provideGoogleBooksApi(retrofit: Retrofit): IGoogleBooksApi {
    return retrofit.create(IGoogleBooksApi::class.java)
}

private fun provideGeminiApi(retrofit: Retrofit): IGeminiApi {
    return retrofit.create(IGeminiApi::class.java)
}