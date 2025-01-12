package com.darekbx.wheresmybus.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.darekbx.wheresmybus.R
import com.darekbx.wheresmybus.model.BusStop
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.bonuspack.clustering.MarkerClusterer
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapDroidScreen(modifier: Modifier, busStopsViewModel: BusStopsViewModel = koinViewModel()) {
    val busStops by busStopsViewModel.busStops.collectAsState()
    val busLines by busStopsViewModel.busLines.collectAsState()
    val liveItems by busStopsViewModel.liveItems.collectAsState()
    val context = LocalContext.current

    var map by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        busStopsViewModel.fetchBusStops()
    }

    LaunchedEffect(liveItems) {
        liveItems.forEach { liveItem ->
            val marker = Marker(map)
            marker.position = GeoPoint(liveItem.lat, liveItem.lon)
            marker.title = liveItem.lines
            marker.snippet = liveItem.time
            marker.icon = AppCompatResources.getDrawable(context, R.drawable.ic_bus)
            marker.setOnMarkerClickListener { _, _ ->
                // TODO
                true
            }
            map?.overlays?.add(marker)
            map?.invalidate()
        }
    }

    when {
        busStops.isEmpty() -> LoadingBox(modifier)
        else -> MapWidget(
            modifier,
            context,
            busStops,
            onMapReady = { map = it },
            onBusStopClick = { busStopsViewModel.fetchBusLines(it.busStopId, it.busStopNr) })
    }

    if (busLines.isNotEmpty()) {
        BusLines(
            modifier,
            busLines,
            onBusLineClick = { busLine -> busStopsViewModel.fetchLiveBuses(busLine) },
            onClose = { busStopsViewModel.clearBusLines() }
        )
    }
}

@Composable
private fun LoadingBox(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.size(64.dp))
    }
}
