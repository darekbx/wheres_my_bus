package com.darekbx.wheresmybus.domain.livedata

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

// https://api.um.warszawa.pl/api/action/busestrams_get/?resource_id=f2e5503e-927d-4ad3-9500-4ab9e55deb59&type=1&apikey=a3e0ae07-e656-44a4-9691-cfa43dacf086&line=504
class LiveDataUseCase(
    private val client: HttpClient,
    private val apiUrl: String,
    private val apiKey: String
) {
    enum class DataType(val value: Int) {
        BUSES(1),
        TRAMS(2)
    }

    suspend fun fetchLiveData(line: String, type: DataType = DataType.BUSES): Result<List<LiveDataItem>> {
        try {
            val url = "$apiUrl/$ACTION_LIVE_DATA&$TYPE=${type.value}&$apiKey&$LINE=$line"
            Log.v("LiveBusesUseCase", "Live bus lines url: $url")
            val response = client.get(url)
            val lines = response.body<LiveDataResponse>()
            return Result.success(lines.result)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    companion object {
        private const val ACTION_LIVE_DATA =
            "action/busestrams_get/?resource_id=f2e5503e-927d-4ad3-9500-4ab9e55deb59"
        private const val TYPE = "type"
        private const val LINE = "line"
    }
}
