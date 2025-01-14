package com.darekbx.wheresmybus.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.darekbx.wheresmybus.utils.LineNumberCreator
import com.darekbx.wheresmybus.viewmodel.BusStopsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * TODO:
 *  - my location marker
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

    var map by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        busStopsViewModel.fetchStops()
    }

    LaunchedEffect(liveItems) {
        liveItems.forEach { liveItem ->
            map?.run {
                val marker = Marker(this).apply {
                    position = GeoPoint(liveItem.lat, liveItem.lon)
                    icon = lineNumberCreator.createBitmap(liveItem.lines)
                }
                overlays?.add(marker)
                invalidate()
            }
        }
    }

    when {
        busStops.isEmpty() -> { /*TODO*/ }

        else -> MapWidget(
            modifier,
            context,
            busStops,
            onMapReady = { map = it },
            onBusStopClick = { busStopsViewModel.fetchBusLines(it.busStopId, it.busStopNr) })
    }

    if (progress) {
        LoadingBox()
    }

    if (busLines.isNotEmpty()) {
        BusLines(
            modifier,
            busLines,
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
}
