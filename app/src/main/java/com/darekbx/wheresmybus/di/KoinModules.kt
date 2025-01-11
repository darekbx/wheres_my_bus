package com.darekbx.wheresmybus.di

import android.app.Application
import androidx.room.Room
import com.darekbx.wheresmybus.BuildConfig
import com.darekbx.wheresmybus.domain.buslines.BusLinesUseCase
import com.darekbx.wheresmybus.domain.busstops.BusStopsUseCase
import com.darekbx.wheresmybus.domain.livebuses.LiveBusesUseCase
import com.darekbx.wheresmybus.repository.local.AppDatabase
import com.darekbx.wheresmybus.repository.local.dao.BusStopDao
import com.darekbx.wheresmybus.ui.BusStopsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named("API_URL")) { "https://api.um.warszawa.pl/api" }

    single(named("API_KEY")) { "apikey=${BuildConfig.UM_API_KEY}" }

    factory {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    factory { BusStopsUseCase(get(), get(), get(named("API_URL")), get(named("API_KEY"))) }

    factory { BusLinesUseCase(get(), get(named("API_URL")), get(named("API_KEY"))) }

    factory { LiveBusesUseCase(get(), get(named("API_URL")), get(named("API_KEY"))) }
}

val viewModelModule = module {
    viewModel { BusStopsViewModel(get(), get(), get()) }
}

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(get<Application>(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }

    single<BusStopDao> { get<AppDatabase>().busStopDaoDao() }
}
