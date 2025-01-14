package com.darekbx.wheresmybus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.wheresmybus.domain.lines.LinesUseCase
import com.darekbx.wheresmybus.domain.stops.StopsUseCase
import com.darekbx.wheresmybus.domain.livedata.LiveDataUseCase
import com.darekbx.wheresmybus.domain.livedata.LiveDataItem
import com.darekbx.wheresmybus.model.BusStop
import com.darekbx.wheresmybus.model.BusStop.Companion.toBusStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BusStopsViewModel(
    private val stopsUseCase: StopsUseCase,
    private val linesUseCase: LinesUseCase,
    private val liveDataUseCase: LiveDataUseCase
) : ViewModel() {

    private val _busStops = MutableStateFlow(emptyList<BusStop>())
    val busStops: StateFlow<List<BusStop>> = _busStops

    private val _busLines = MutableStateFlow(emptyList<String>())
    val busLines: StateFlow<List<String>> = _busLines

    private val _liveItems = MutableStateFlow(emptyList<LiveDataItem>())
    val liveItems: StateFlow<List<LiveDataItem>> = _liveItems

    private val _errorResponse = MutableStateFlow<Throwable?>(null)
    val errorResponse: StateFlow<Throwable?> = _errorResponse

    private val _progress = MutableStateFlow(false)
    val progress: StateFlow<Boolean> = _progress

    fun fetchStops() {
        _progress.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = stopsUseCase.fetchStops()
                when  {
                    result.isSuccess -> _busStops.value = result.getOrThrow().map { it.toBusStop() }
                    result.isFailure -> _errorResponse.value = result.exceptionOrNull()
                }
                _progress.value = false
            }
        }
    }

    fun fetchBusLines(busStopId: String, busStopNr: String) {
        _progress.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = linesUseCase.fetchLines(busStopId, busStopNr)
                when  {
                    result.isSuccess -> _busLines.value = result.getOrThrow()
                    result.isFailure -> _errorResponse.value = result.exceptionOrNull()
                }
                _progress.value = false
            }
        }
    }

    fun fetchLiveBuses(line: String) {
        _progress.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = liveDataUseCase.fetchLiveData(line)
                when  {
                    result.isSuccess -> _liveItems.value = result.getOrThrow()
                    result.isFailure -> _errorResponse.value = result.exceptionOrNull()
                }
                _progress.value = false
            }
        }
    }

    fun clearBusLines() {
        _busLines.value = emptyList()
    }

    fun clearError() {
        _errorResponse.value = null
    }
}
