package com.darekbx.wheresmybus.domain.livebuses

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

// https://api.um.warszawa.pl/api/action/busestrams_get/?resource_id=f2e5503e-927d-4ad3-9500-4ab9e55deb59&type=1&apikey=a3e0ae07-e656-44a4-9691-cfa43dacf086&line=504
class LiveBusesUseCase(
    private val client: HttpClient,
    private val apiUrl: String,
    private val apiKey: String
) {
    suspend fun fetchBusLines(line: String): List<LiveDataItem> {
        try {
            val url = "$apiUrl/$ACTION_LIVE_DATA&$TYPE=$TYPE_BUSES&$apiKey&$LINE=$line"
            val response = client.get(url)
            val busLines = response.body<LiveDataResponse>()
            return busLines.result
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    companion object {
        private const val ACTION_LIVE_DATA =
            "action/busestrams_get/?resource_id=f2e5503e-927d-4ad3-9500-4ab9e55deb59"
        private const val TYPE = "type"
        private const val LINE = "line"
        private const val TYPE_BUSES = 1
    }
}
