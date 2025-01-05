package com.darekbx.wheresmybus.model

import com.darekbx.wheresmybus.repository.local.dto.BusStopDto
import com.google.android.gms.maps.model.LatLng

data class BusStop(
    val name: String,
    val position: LatLng,
    val direction: String,
    val validFrom: String
) {

    companion object {
        fun BusStopDto.toBusStop(): BusStop {
            return BusStop(
                name = name,
                position = LatLng(latitude, longitude),
                direction = direction,
                validFrom = validFrom
            )
        }
    }
}
