package com.darekbx.wheresmybus.di

import android.app.Application
import androidx.room.Room
import com.darekbx.wheresmybus.busstops.BusStopsUseCase
import com.darekbx.wheresmybus.repository.local.AppDatabase
import com.darekbx.wheresmybus.repository.local.dao.BusStopDao
import com.darekbx.wheresmybus.ui.BusStopsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    factory { BusStopsUseCase(get(), get()) }
}

val viewModelModule = module {
    viewModel { BusStopsViewModel(get()) }
}

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(get<Application>(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }

    single<BusStopDao> { get<AppDatabase>().busStopDaoDao() }
}
