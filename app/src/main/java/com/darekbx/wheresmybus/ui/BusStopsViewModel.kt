package com.darekbx.wheresmybus.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.wheresmybus.busstops.BusStopsUseCase
import kotlinx.coroutines.launch

class BusStopsViewModel(
    private val busStopsUseCase: BusStopsUseCase
): ViewModel() {

    fun fetchBusStops() {
        viewModelScope.launch {
            val r = busStopsUseCase.fetchBusStops() ?: emptyList()
            Log.v("sigma", "${r.size}")
        }
    }
}