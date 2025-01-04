package com.darekbx.wheresmybus

import android.app.Application
import com.darekbx.wheresmybus.di.appModule
import com.darekbx.wheresmybus.di.databaseModule
import com.darekbx.wheresmybus.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, viewModelModule, databaseModule)
        }
    }
}