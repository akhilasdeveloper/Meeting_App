package com.akhilasdeveloper.meetingapp.data

data class EventData(
    val title: String,
    val description: String,
    val organizerEmail: String,
    val meetingRoom: String,
    val startTime: Long,
    val endTime: Long,
    val startPer: Float,
    val endPer: Float
)