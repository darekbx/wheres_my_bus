package com.darekbx.wheresmybus.system

import android.annotation.SuppressLint
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

class LocationUtils(private val locationManager: LocationManager) {

    fun isLocationEnabled(): Boolean {
        return locationManager.isLocationEnabled
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onLocation: (LatLng) -> Unit) {
        val location = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
        if (location != null) {
            onLocation(LatLng(location.latitude, location.longitude))
        }
    }
}