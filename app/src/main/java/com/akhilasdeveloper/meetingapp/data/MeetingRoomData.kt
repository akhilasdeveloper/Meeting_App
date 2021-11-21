package com.akhilasdeveloper.meetingapp.data

data class MeetingRoomData(
    val meeting_rooms: List<MeetingRoom>
)

data class MeetingRoom(
    val id: Long,
    val endColor: Int,
    val name: String,
    val startColor: Int
)