package com.darekbx.wheresmybus.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.wheresmybus.repository.local.dto.BusStopDto

@Dao
interface BusStopDao {

    @Insert
    suspend fun insertAll(busStopDtos: List<BusStopDto>)

    @Query("DELETE FROM bus_stop")
    suspend fun deleteAll()

    @Query("SELECT * FROM bus_stop")
    suspend fun getAll(): List<BusStopDto>
}
