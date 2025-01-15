package com.darekbx.wheresmybus.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.wheresmybus.model.BusStop
import com.darekbx.wheresmybus.ui.theme.WheresMyBusTheme
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BusLines(
    modifier: Modifier,
    busLines: List<String>,
    busStop: BusStop?,
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
            Column(
                modifier = Modifier.fillMaxWidth(0.8F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    busStop?.name ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Location",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "${busStop?.direction}",
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        fontSize = 18.sp
                    )
                }
                Text(
                    "Select line to load locations",
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 12.sp,
                )
                FlowRow(modifier = Modifier.padding(bottom = 16.dp)) {
                    busLines.forEach {
                        Text(
                            text = it,
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { onBusLineClick(it) },
                            color = Color.Blue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
                Button(modifier = Modifier.padding(top = 4.dp), onClick = onClose) {
                    Text("Close")
                }
            }
        }
    }
}

@Preview
@Composable
fun BusLinesPreview() {
    WheresMyBusTheme {
        Surface {
            BusLines(
                modifier = Modifier,
                busStop = BusStop("Kijowska", "", "", LatLng(0.0, 0.0), "al.Zieleniecka", ""),
                busLines = listOf("N74", "709", "3")
            )
        }
    }
}
