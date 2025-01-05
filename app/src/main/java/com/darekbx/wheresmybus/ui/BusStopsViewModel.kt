package com.darekbx.wheresmybus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.wheresmybus.domain.busstops.BusStopsUseCase
import com.darekbx.wheresmybus.model.BusStop
import com.darekbx.wheresmybus.model.BusStop.Companion.toBusStop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BusStopsViewModel(
    private val busStopsUseCase: BusStopsUseCase
) : ViewModel() {

    private val _busStops = MutableStateFlow(emptyList<BusStop>())
    val busStops: StateFlow<List<BusStop>> = _busStops

    fun fetchBusStops() {
        viewModelScope.launch {
            _busStops.value =
                busStopsUseCase.fetchBusStops()
                    ?.map { it.toBusStop() }
                    ?: emptyList()
        }
    }
}
