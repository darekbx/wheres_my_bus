package com.darekbx.wheresmybus.domain.stops

import kotlinx.serialization.Serializable

@Serializable
data class BusStops(val result: List<BusStop>)

@Serializable
data class BusStop(val values: List<ValueItem>)

@Serializable
data class ValueItem(val key: String, val value: String)
