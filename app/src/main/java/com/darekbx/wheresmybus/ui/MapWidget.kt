package com.darekbx.wheresmybus.ui

import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.darekbx.wheresmybus.R
import com.darekbx.wheresmybus.model.BusStop
import org.osmdroid.bonuspack.clustering.MarkerClusterer
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapWidget(
    modifier: Modifier,
    context: Context,
    busStops: List<BusStop>,
    useClustering: Boolean = false,
    onMapReady: (MapView) -> Unit = {},
    onBusStopClick: (BusStop) -> Unit = {}
) {
    val mapView = rememberMapWithLifecycle()
    val defaultZoom = 18.0
    val defaultLocation = GeoPoint(52.1505, 21.0199)

    MapBox(modifier = modifier.padding(bottom = 8.dp)) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                .fillMaxSize()
        ) {
            AndroidView(factory = { mapView }) { mapView ->

                Configuration.getInstance()
                    .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))

                if (useClustering) {
                    val clusterer = RadiusMarkerClusterer(context)
                    clusterer.setRadius(500)
                    busStops.forEach { busStop ->
                        clusterer.drawPoint(mapView, busStop, onBusStopClick)
                    }
                    mapView.overlays.add(clusterer)
                } else {
                    busStops.forEach { busStop ->
                        mapView.overlays.add(Marker(mapView).apply {
                            position = busStop.getGeoPoint()
                            icon = AppCompatResources.getDrawable(context, R.drawable.wtp_logo)
                            setOnMarkerClickListener { _, _ ->
                                onBusStopClick(busStop)
                                true
                            }
                        })
                    }
                }

                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.setMultiTouchControls(true)
                mapView.controller.apply {
                    setZoom(defaultZoom)
                    setCenter(defaultLocation)
                }

                onMapReady(mapView)
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
        icon = AppCompatResources.getDrawable(mapView.context, R.drawable.wtp_logo)
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
