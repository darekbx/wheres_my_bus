package com.darekbx.wheresmybus.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.wheresmybus.repository.local.dao.BusStopDao
import com.darekbx.wheresmybus.repository.local.dto.BusStopDto

@Database(entities = [BusStopDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun busStopDaoDao(): BusStopDao

    companion object {
        const val DB_NAME = "app_database"
    }
}