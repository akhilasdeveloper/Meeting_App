package com.akhilasdeveloper.meetingapp

import com.akhilasdeveloper.meetingapp.data.MeetingRoom

interface MeetingRoomClickListener {
    fun onItemSelected(meetingRoom: MeetingRoom, position: Int)
    fun onItemRadioSelected(meetingRoom: MeetingRoom, position: Int)
    fun onItemEditSelected(meetingRoom: MeetingRoom, position: Int)
    fun onItemDeleteSelected(meetingRoom: MeetingRoom, position: Int)
    fun onItemColorSelected(meetingRoom: MeetingRoom, position: Int)
}