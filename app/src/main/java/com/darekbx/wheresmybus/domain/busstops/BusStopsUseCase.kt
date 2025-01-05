package com.darekbx.wheresmybus.domain.busstops

import com.darekbx.wheresmybus.BuildConfig
import com.darekbx.wheresmybus.repository.local.dao.BusStopDao
import com.darekbx.wheresmybus.repository.local.dto.BusStopDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class BusStopsUseCase(
    private val client: HttpClient,
    private val busStopDao: BusStopDao
) {

    suspend fun fetchBusStops(forceRefresh: Boolean = false): List<BusStopDto>? {
        try {
            if (!forceRefresh) {
                // Try to get persisted data
                val persistedData = busStopDao.getAll()
                if (persistedData.isNotEmpty()) {
                    return persistedData
                }
            }

            // Fetch data from API when kocal data is empty or force was called
            val response = client.get("$API_URL/$ACTION_BUS_STOPS&$API_KEY")
            response.body<BusStops>()?.let {
                val mappedData = it.result.map { it.mapToDto() }
                busStopDao.insertAll(mappedData)
            }

            return busStopDao.getAll()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            client.close()
        }
    }

    private fun BusStop.mapToDto(): BusStopDto {
        val valueMap = values.associateBy { it.key }
        return BusStopDto(
            name = valueMap.getValue("nazwa_zespolu").value,
            latitude = valueMap.getValue("szer_geo").value.toDouble(),
            longitude = valueMap.getValue("dlug_geo").value.toDouble(),
            direction = valueMap.getValue("kierunek").value,
            validFrom = valueMap.getValue("obowiazuje_od").value
        )
    }

    companion object {
        private const val API_URL = "https://api.um.warszawa.pl/api"
        private const val ACTION_BUS_STOPS =
            "action/dbstore_get?id=ab75c33d-3a26-4342-b36a-6e5fef0a3ac3"
        private const val API_KEY = "apikey=${BuildConfig.UM_API_KEY}"
    }
}
