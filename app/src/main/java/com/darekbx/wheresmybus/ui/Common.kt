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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.wheresmybus.R
import com.darekbx.wheresmybus.ui.theme.WheresMyBusTheme

@Composable
fun LoadingBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .shadow(8.dp)
                .background(
                    Color(0xFFFCFCFA),
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
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

@Preview
@Composable
fun LoadingBoxPreview() {
    WheresMyBusTheme {
        Surface {
            LoadingBox()
        }
    }
}