package com.darekbx.wheresmybus.domain.stops

import com.darekbx.wheresmybus.repository.local.dao.BusStopDao
import com.darekbx.wheresmybus.repository.local.dto.BusStopDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class StopsUseCase(
    private val client: HttpClient,
    private val busStopDao: BusStopDao,
    private val apiUrl: String,
    private val apiKey: String
) {
    suspend fun fetchStops(forceRefresh: Boolean = false): Result<List<BusStopDto>> {
        try {
            if (!forceRefresh) {
                // Try to get persisted data
                val persistedData = busStopDao.getAll()
                if (persistedData.isNotEmpty()) {
                    return Result.success(persistedData)
                }
            }

            // Fetch data from API when kocal data is empty or force was called
            val response = client.get("$apiUrl/$ACTION_BUS_STOPS&$apiKey")
            response.body<BusStops>()?.let {
                val mappedData = it.result.map { it.mapToDto() }
                busStopDao.insertAll(mappedData)
            }

            return Result.success(busStopDao.getAll())
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    private fun BusStop.mapToDto(): BusStopDto {
        val valueMap = values.associateBy { it.key }
        return BusStopDto(
            name = valueMap.getValue("nazwa_zespolu").value,
            busStopId = valueMap.getValue("zespol").value,
            busStopNr = valueMap.getValue("slupek").value,
            latitude = valueMap.getValue("szer_geo").value.toDouble(),
            longitude = valueMap.getValue("dlug_geo").value.toDouble(),
            direction = valueMap.getValue("kierunek").value,
            validFrom = valueMap.getValue("obowiazuje_od").value
        )
    }

    companion object {
        private const val ACTION_BUS_STOPS =
            "action/dbstore_get?id=ab75c33d-3a26-4342-b36a-6e5fef0a3ac3"
    }
}
