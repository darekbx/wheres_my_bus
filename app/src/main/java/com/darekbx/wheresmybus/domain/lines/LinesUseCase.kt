package com.darekbx.wheresmybus.domain.lines

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class LinesUseCase(
    private val client: HttpClient,
    private val apiUrl: String,
    private val apiKey: String
) {
    suspend fun fetchLines(stopId: String, stopNr: String): Result<List<String>> {
        try {
            val url = "$apiUrl/$ACTION_BUS_LINES&$BUS_STOP_ID=$stopId&$BUS_STOP_NR=$stopNr&$apiKey"
            Log.v("LinesUseCase", "Fetching bus lines url: $url")
            val response = client.get(url)
            val busLines = response.body<BusLinesResponse>()
            return Result.success(busLines.result.flatMap { it.values.map { it.value } })
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    companion object {
        private const val ACTION_BUS_LINES =
            "action/dbtimetable_get?id=88cd555f-6f31-43ca-9de4-66c479ad5942"
        private const val BUS_STOP_ID = "busstopId"
        private const val BUS_STOP_NR = "busstopNr"
    }
}
