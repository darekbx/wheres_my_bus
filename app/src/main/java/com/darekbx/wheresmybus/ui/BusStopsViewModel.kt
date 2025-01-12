package com.darekbx.wheresmybus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.wheresmybus.domain.buslines.BusLinesUseCase
import com.darekbx.wheresmybus.domain.busstops.BusStopsUseCase
import com.darekbx.wheresmybus.domain.livebuses.LiveBusesUseCase
import com.darekbx.wheresmybus.domain.livebuses.LiveDataItem
import com.darekbx.wheresmybus.model.BusStop
import com.darekbx.wheresmybus.model.BusStop.Companion.toBusStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BusStopsViewModel(
    private val busStopsUseCase: BusStopsUseCase,
    private val busLinesUseCase: BusLinesUseCase,
    private val liveBusesUseCase: LiveBusesUseCase
) : ViewModel() {

    private val _busStops = MutableStateFlow(emptyList<BusStop>())
    val busStops: StateFlow<List<BusStop>> = _busStops

    private val _busLines = MutableStateFlow(emptyList<String>())
    val busLines: StateFlow<List<String>> = _busLines

    private val _liveItems = MutableStateFlow(emptyList<LiveDataItem>())
    val liveItems: StateFlow<List<LiveDataItem>> = _liveItems

    fun fetchBusStops() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _busStops.value =
                    busStopsUseCase.fetchBusStops()
                        ?.map { it.toBusStop() }
                        ?: emptyList()
            }
        }
    }

    fun fetchBusLines(busStopId: String, busStopNr: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _busLines.value = busLinesUseCase.fetchBusLines(busStopId, busStopNr)
            }
        }
    }

    fun fetchLiveBuses(line: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _liveItems.value = liveBusesUseCase.fetchBusLines(line)
            }
        }
    }

    fun clearBusLines() {
        _busLines.value = emptyList()
    }
}
