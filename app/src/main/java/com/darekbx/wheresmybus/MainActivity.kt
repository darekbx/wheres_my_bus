package com.darekbx.wheresmybus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.darekbx.wheresmybus.ui.BusStopsViewModel
import com.darekbx.wheresmybus.ui.theme.WheresMyBusTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheresMyBusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val singapore = LatLng(52.21, 21.10)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(singapore, 10f)
                    }


                    GoogleMap(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    )

                    Box(modifier = Modifier.padding(innerPadding)) {
                        test()
                    }
                }
            }
        }
    }

    @Composable
    private fun test(busStopsViewModel: BusStopsViewModel = koinViewModel()) {
        Button(onClick = { busStopsViewModel.fetchBusStops() }) { Text("test") }
    }
}