package com.akhilasdeveloper.meetingapp

import android.content.Context
import com.akhilasdeveloper.meetingapp.data.MeetingRoomData
import com.google.gson.Gson
import timber.log.Timber

class GenerateMeetingRooms (val context: Context, val gson: Gson) {
    fun getMeetingRooms() {
        val json: String =
            context.assets.open("meeting_rooms.json").bufferedReader().use {
                it.readText()
            }

        val topic = gson.fromJson(json, MeetingRoomData::class.java)

        Timber.d("json %s", topic.meeting_rooms.size)
    }
}