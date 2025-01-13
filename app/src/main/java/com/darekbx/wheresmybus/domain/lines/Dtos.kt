package com.darekbx.wheresmybus.domain.lines

import kotlinx.serialization.Serializable

@Serializable
data class BusLinesResponse(
    val result: List<BusLinesResult>
)

@Serializable
data class BusLinesResult(
    val values: List<BusLine>
)

@Serializable
data class BusLine(
    val key: String,
    val value: String
)