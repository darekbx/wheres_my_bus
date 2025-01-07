package com.darekbx.wheresmybus.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.darekbx.wheresmybus.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel

// TODO move, refactor, or use different method to get marker, maybe directly from bitmap?
private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    vectorDrawable.draw(Canvas(bitmap))
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun MapScreen(modifier: Modifier, busStopsViewModel: BusStopsViewModel = koinViewModel()) {
    val busStops by busStopsViewModel.busStops.collectAsState()
    val context = LocalContext.current
    val icon = remember { bitmapDescriptorFromVector(context, R.drawable.ic_bus_stop) }

    LaunchedEffect(Unit) {
        busStopsViewModel.fetchBusStops()
    }

    when {
        busStops.isEmpty() -> { Text("TODO: Loading or empty") }
        else -> {

            val singapore = LatLng(52.21, 21.10)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(singapore, 10f)
            }

            // TODO add clustering??
            GoogleMap(
                modifier = modifier
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // TODO: throws ANR
                Clustering(
                    items = busStops,
                )
            }

        }
    }
}
