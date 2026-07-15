package com.rafael.ibooks.di

import androidx.room.Room
import com.rafael.ibooks.data.local.db.IBooksDatabase
import com.rafael.ibooks.data.remote.datasource.BookRemoteDatasourceImpl
import com.rafael.ibooks.data.remote.datasource.GeminiRemoteDataSourceImpl
import com.rafael.ibooks.data.remote.datasource.IBookRemoteDataSource
import com.rafael.ibooks.data.remote.datasource.IGeminiRemoteDataSource
import com.rafael.ibooks.domain.repository.GeminiRepositoryImpl
import com.rafael.ibooks.domain.repository.BookRepositoryImpl
import com.rafael.ibooks.domain.repository.IBookRepository
import com.rafael.ibooks.domain.repository.IGeminiRepository
import com.rafael.ibooks.domain.repository.IWantToReadRepository
import com.rafael.ibooks.domain.repository.WantToReadRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            IBooksDatabase::class.java,
            "ibooks.db"
        ).build()
    }

    single { get<IBooksDatabase>().wantToReadBookDao() }
    single<IGeminiRepository> { GeminiRepositoryImpl(get()) }
    single<IBookRepository> { BookRepositoryImpl(get()) }
    single<IWantToReadRepository> { WantToReadRepositoryImpl(get()) }
    single<IBookRemoteDataSource> { BookRemoteDatasourceImpl(get()) }
    single<IGeminiRemoteDataSource> { GeminiRemoteDataSourceImpl(get()) }
}
