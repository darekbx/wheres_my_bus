package com.darekbx.wheresmybus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.wheresmybus.R
import com.darekbx.wheresmybus.ui.theme.WheresMyBusTheme
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
 *  - progress bars - load lines, load live data
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

    errorResponse?.let {
        ErrorBox(it) {
            busStopsViewModel.clearError()
        }
    }
}

@Composable
private fun LoadingBox(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.size(64.dp))
    }
}

@Composable
fun ErrorBox(error: Throwable = Throwable("Message"), onClose: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp)
                .background(
                    Color(0xFFFECCCD),
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = "Error",
                    tint = Color(0xFFEF4433)
                )
                Text(
                    text = error.message ?: "Unknown error",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4433)
                )
                Text(
                    text = error.toString(),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Button(onClick = onClose) { Text("Close") }
            }
        }
    }
}

@Preview
@Composable
fun ErrorBoxPreview() {
    WheresMyBusTheme {
        Surface {
            ErrorBox()
        }
    }
}