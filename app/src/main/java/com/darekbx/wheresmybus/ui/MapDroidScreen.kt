package com.darekbx.wheresmybus.ui

import android.Manifest
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.darekbx.wheresmybus.R
import com.darekbx.wheresmybus.domain.livedata.LiveDataItem
import com.darekbx.wheresmybus.model.BusStop
import com.darekbx.wheresmybus.utils.LineNumberCreator
import com.darekbx.wheresmybus.viewmodel.BusStopsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.system.exitProcess

/**
 * TODO:
 *  - mark trams stops in different icon
 *  - bus stop icon
 *  - add support for trams
 */

@Composable
fun MapDroidScreen(
    modifier: Modifier,
    busStopsViewModel: BusStopsViewModel = koinViewModel(),
    lineNumberCreator: LineNumberCreator = koinInject()
) {
    val busStops by busStopsViewModel.busStops.collectAsState()
    val busLines by busStopsViewModel.busLines.collectAsState()
    val liveItems by busStopsViewModel.liveItems.collectAsState()
    val errorResponse by busStopsViewModel.errorResponse.collectAsState()
    val progress by busStopsViewModel.progress.collectAsState()
    val context = LocalContext.current

    val isLocationEnabled = remember { busStopsViewModel.isLocationEnabled() }

    var activeStop by remember { mutableStateOf<BusStop?>(null) }
    var map by remember { mutableStateOf<MapView?>(null) }

    DisplayUserLocation(busStopsViewModel, map, context)

    LaunchedEffect(Unit) {
        busStopsViewModel.fetchStops()
    }

    // Display live lines
    LaunchedEffect(liveItems) {
        liveItems.forEach { liveItem ->
            map?.createLineMarker(liveItem, lineNumberCreator)
            map?.invalidate()
        }
    }

    if (busStops.isNotEmpty()) {
        MapWidget(
            modifier,
            context,
            busStops,
            onMapReady = { map = it },
            onBusStopClick = {
                activeStop = it
                busStopsViewModel.fetchBusLines(it.busStopId, it.busStopNr)
            }
        )
    }

    if (progress) {
        LoadingBox()
    }

    if (busLines.isNotEmpty()) {
        BusLines(
            modifier,
            busLines = busLines,
            busStop = activeStop,
            onBusLineClick = { busLine ->
                // Close the dialog
                busStopsViewModel.clearBusLines()
                busStopsViewModel.fetchLiveBuses(busLine)
            },
            onClose = { busStopsViewModel.clearBusLines() }
        )
    }

    errorResponse?.let {
        ErrorBox(it) {
            busStopsViewModel.clearError()
        }
    }

    if (!isLocationEnabled) {
        ErrorBox(
            IllegalStateException("Location is disabled, please enable and open application again."),
            onClose = { exitProcess(0) })
    }
}

private fun MapView.createLineMarker(
    liveItem: LiveDataItem,
    lineNumberCreator: LineNumberCreator
) {
    val marker = Marker(this).apply {
        position = GeoPoint(liveItem.lat, liveItem.lon)
        icon = lineNumberCreator.createBitmap(liveItem.lines)
    }
    overlays?.add(marker)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun DisplayUserLocation(
    busStopsViewModel: BusStopsViewModel,
    map: MapView?,
    context: Context
) {
    val locationPermission = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(map) {
        map?.let { mapView ->
            when {
                !locationPermission.allPermissionsGranted ->
                    locationPermission.launchMultiplePermissionRequest()

                else -> {
                    busStopsViewModel.fetchLocation { location ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(location.latitude, location.longitude)
                            icon = AppCompatResources.getDrawable(context, R.drawable.ic_pin_person)
                        }
                        mapView.overlays?.add(marker)
                        mapView.invalidate()
                    }
                }
            }
        }
    }
}
