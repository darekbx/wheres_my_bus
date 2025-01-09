package com.darekbx.wheresmybus.model

import com.darekbx.wheresmybus.repository.local.dto.BusStopDto
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.osmdroid.util.GeoPoint

data class BusStop(
    val name: String,
    val busStopId: String,
    val busStopNr: String,
    val location: LatLng,
    val direction: String,
    val validFrom: String
): ClusterItem {

    companion object {
        fun BusStopDto.toBusStop(): BusStop {
            return BusStop(
                name = name,
                busStopId = busStopId,
                busStopNr = busStopNr,
                location = LatLng(latitude, longitude),
                direction = direction,
                validFrom = validFrom
            )
        }
    }

    fun getGeoPoint(): GeoPoint = GeoPoint(location.latitude, location.longitude)

    override fun getPosition(): LatLng = location

    override fun getTitle(): String = name

    override fun getSnippet(): String = direction

    override fun getZIndex(): Float = 0F
}
