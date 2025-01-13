package com.darekbx.wheresmybus.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class LineNumberCreator(private val context: Context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
        textSize = 42F
    }

    fun createBitmap(
        text: String,
        fontSize: Float = 42F,
        textColor: Int = Color.BLUE
    ): Drawable {
        return BitmapDrawable(context.resources, createDrawable(text, fontSize, textColor))
    }

    fun createDrawable(
        text: String,
        fontSize: Float = 42F,
        textColor: Int = Color.BLUE
    ): Bitmap {
        val padding = 8
        val corners = 8F

        paint.color = textColor
        paint.textSize = fontSize
        paint.typeface = Typeface.MONOSPACE
        paint.textAlign = Paint.Align.LEFT

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val width = textBounds.width() + padding * 3
        val height = textBounds.height() + padding * 3

        val bitmap = Bitmap.createBitmap(width, height + 15, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the rectangle
        val bgRect = RectF(
            padding / 2F,
            padding / 2F,
            width.toFloat() - padding / 2F,
            height.toFloat() - padding / 2F
        )
        canvas.drawRoundRect(bgRect, corners, corners, backgroundPaint)
        canvas.drawRoundRect(bgRect, corners, corners, borderPaint)

        // Draw the text with padding
        canvas.drawText(
            text,
            padding.toFloat() * 1.25F,
            (padding.toFloat() * 1.35F + textBounds.height()),
            paint
        )

        canvas.rotate(180F, width / 2F, height / 2F)
        canvas.drawText(
            "^",
            width.toFloat() / 2F - padding,
            18F,
            borderPaint
        )

        return bitmap
    }
}

@Preview
@Composable
fun LineNumberCreatorPreview() {
    val context = LocalContext.current
    val lineNumberCreator = LineNumberCreator(context)
    Column(
        Modifier
            .background(androidx.compose.ui.graphics.Color.LightGray)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = lineNumberCreator.createDrawable("709").asImageBitmap(),
            contentDescription = null
        )
        Image(
            bitmap = lineNumberCreator.createDrawable("N40").asImageBitmap(),
            contentDescription = null
        )
        Image(
            bitmap = lineNumberCreator.createDrawable("22").asImageBitmap(),
            contentDescription = null
        )
    }
}
