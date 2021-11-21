package com.akhilasdeveloper.meetingapp

import androidx.appcompat.app.AlertDialog
import com.akhilasdeveloper.meetingapp.data.MeetingRoom

interface MeetingRoomColorClickListener {
    fun onItemColorSelectorClicked(meetingRoom: MeetingRoom, position: Int, alertDialog: AlertDialog)
}