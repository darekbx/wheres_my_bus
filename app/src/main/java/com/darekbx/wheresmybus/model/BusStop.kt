package com.darekbx.wheresmybus.model

import com.darekbx.wheresmybus.repository.local.dto.BusStopDto
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class BusStop(
    val name: String,
    val location: LatLng,
    val direction: String,
    val validFrom: String
): ClusterItem {

    companion object {
        fun BusStopDto.toBusStop(): BusStop {
            return BusStop(
                name = name,
                location = LatLng(latitude, longitude),
                direction = direction,
                validFrom = validFrom
            )
        }
    }

    override fun getPosition(): LatLng = location

    override fun getTitle(): String = name

    override fun getSnippet(): String = direction

    override fun getZIndex(): Float = 0F
}
