package com.akhilasdeveloper.meetingapp

import android.content.Context
import androidx.core.content.ContextCompat
import com.akhilasdeveloper.meetingapp.data.ColorData

class ColorDatas(val context: Context) {
    val colors = listOf<ColorData>(
        ColorData(
            start = ContextCompat.getColor(context, R.color.start0),
            end = ContextCompat.getColor(context, R.color.end0),
            font = ContextCompat.getColor(context, R.color.font0)
        ),
        ColorData(
            start = ContextCompat.getColor(context, R.color.start1),
            end = ContextCompat.getColor(context, R.color.end1),
            font = ContextCompat.getColor(context, R.color.font1)
        ),
        ColorData(
            start = ContextCompat.getColor(context, R.color.start2),
            end = ContextCompat.getColor(context, R.color.end2),
            font = ContextCompat.getColor(context, R.color.font2)
        ),
        ColorData(
            start = ContextCompat.getColor(context, R.color.start3),
            end = ContextCompat.getColor(context, R.color.end3),
            font = ContextCompat.getColor(context, R.color.font3)
        ),
        ColorData(
            start = ContextCompat.getColor(context, R.color.start4),
            end = ContextCompat.getColor(context, R.color.end4),
            font = ContextCompat.getColor(context, R.color.font4)
        )
    )
}