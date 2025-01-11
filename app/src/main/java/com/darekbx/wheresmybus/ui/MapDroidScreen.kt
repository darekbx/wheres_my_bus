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
import androidx.compose.runtime.remember
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

    LaunchedEffect(Unit) {
        busStopsViewModel.fetchBusStops()
    }

    when {
        busStops.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(64.dp))
            }
        }

        else -> {
            MapWidget(modifier, context, busStops) { busStop ->
                busStopsViewModel.fetchBusLines(busStop.busStopId, busStop.busStopNr)
            }
        }
    }

    if (busLines.isNotEmpty()) {
        BusLines(
            modifier,
            busLines,
            onBusLineClick = { busLine ->

                busStopsViewModel.fetchLiveBuses(busLine)

            },
            onClose = { busStopsViewModel.clearBusLines() }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BusLines(
    modifier: Modifier,
    busLines: List<String>,
    onBusLineClick: (String) -> Unit = { },
    onClose: () -> Unit = { }
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .shadow(8.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.8F)) {
                FlowRow(modifier = Modifier.padding(bottom = 16.dp)) {
                    busLines.forEach {
                        Text(
                            text = it,
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { onBusLineClick(it) },
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(modifier = Modifier, onClick = onClose) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun MapWidget(
    modifier: Modifier,
    context: Context,
    busStops: List<BusStop>,
    onBusStopClick: (BusStop) -> Unit = {}
) {
    val mapView = rememberMapWithLifecycle()
    val defaultZoom = 14.0
    val defaultLocation = GeoPoint(52.15, 21.025)

    MapBox(modifier = modifier.padding(bottom = 8.dp)) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                .fillMaxSize()
        ) {
            AndroidView(factory = { mapView }) { mapView ->

                Configuration.getInstance()
                    .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))

                val clusterer = RadiusMarkerClusterer(context)
                clusterer.setRadius(500)

                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.controller.apply {
                    setZoom(defaultZoom)
                    setCenter(defaultLocation)
                }
                mapView.overlays.clear()

                busStops.forEach { busStop ->
                    clusterer.drawPoint(mapView, busStop, onBusStopClick)
                }

                mapView.overlays.add(clusterer)
            }
        }
    }
}

fun MarkerClusterer.drawPoint(
    mapView: MapView,
    busStop: BusStop,
    onBusStopClick: (BusStop) -> Unit = {}
) {
    add(Marker(mapView).apply {
        position = busStop.getGeoPoint()
        icon = AppCompatResources.getDrawable(mapView.context, R.drawable.ic_bus_stop)
        setOnMarkerClickListener { _, _ ->
            onBusStopClick(busStop)
            true
        }
    })
}

@Composable
fun MapBox(modifier: Modifier = Modifier, contents: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .defaultCard()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        contents()
    }
}

// TODO
@Composable
fun Modifier.defaultCard() = this
    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    .fillMaxWidth()
    .background(
        MaterialTheme.colorScheme.primaryContainer,
        RoundedCornerShape(8.dp)
    )

@Composable
fun rememberMapWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = View.generateViewId()
        }
    }
    val lifecycleObserver = rememberMapLifecycleObserver(mapView = mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> {}
            }
        }
    }
