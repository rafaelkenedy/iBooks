package com.rafael.ibooks.activity

import android.app.Application
import com.rafael.ibooks.di.dataModule
import com.rafael.ibooks.di.domainModule
import com.rafael.ibooks.di.networkModule
import com.rafael.ibooks.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                presentationModule,
                dataModule,
                domainModule,
                networkModule
            ).androidContext(applicationContext)
        }
    }
}