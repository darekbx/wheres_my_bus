package com.darekbx.wheresmybus.domain.livedata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveDataResponse(
    val result: List<LiveDataItem>
)

@Serializable
data class LiveDataItem(
    @SerialName("Lines") val lines: String,
    @SerialName("Lon") val lon: Double,
    @SerialName("VehicleNumber") val vehicleNumber: String,
    @SerialName("Time") val time: String,
    @SerialName("Lat") val lat: Double,
    @SerialName("Brigade") val brigade: String
)
