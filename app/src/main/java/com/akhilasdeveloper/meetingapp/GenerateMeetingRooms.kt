package com.akhilasdeveloper.meetingapp

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.akhilasdeveloper.meetingapp.data.MeetingRoom
import com.akhilasdeveloper.meetingapp.data.MeetingRoomData
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import timber.log.Timber

class GenerateMeetingRooms(val context: Context, val gson: Gson) {

    suspend fun fetchMeetingData(): MeetingRoomData {
        var data = read(Utilities.MEETING_ROOM_DATASTORE_KEY)
        if (data == null) {
            val fromFile = readDataFromFile()
            save(Utilities.MEETING_ROOM_DATASTORE_KEY, fromFile)
            data = fromFile
        }

        val topic = gson.fromJson(data, MeetingRoomData::class.java)
        Timber.d("json %s", topic.meeting_rooms.size)
        return topic
    }

    suspend fun addMeetingRoomName(meetingRoom: MeetingRoom) {
        val data = fetchMeetingData().meeting_rooms.toMutableList()
        data.add(meetingRoom)
        save(
            Utilities.MEETING_ROOM_DATASTORE_KEY,
            gson.toJson(MeetingRoomData(data))
        )
    }

    suspend fun updateMeetingRoom(meetingRoom: MeetingRoom) {
        val data = fetchMeetingData().meeting_rooms.toMutableList()
        val updated = data.map { it ->
            if (it.id == meetingRoom.id)
                meetingRoom
            else
                it
        }
        save(
            Utilities.MEETING_ROOM_DATASTORE_KEY,
            gson.toJson(MeetingRoomData(updated))
        )
        if (meetingRoom.name == fetchDefaultMeetingRoomName())
            updateDefaultMeetingRoom(meetingRoom)
    }

    suspend fun deleteMeetingRoom(meetingRoom: MeetingRoom) {
        val data = fetchMeetingData().meeting_rooms.toMutableList()
        data.removeAll { it -> it.id == meetingRoom.id }
        save(
            Utilities.MEETING_ROOM_DATASTORE_KEY,
            gson.toJson(MeetingRoomData(data))
        )
    }

    suspend fun updateDefaultMeetingRoom(meetingRoom: MeetingRoom) {
        save(
            Utilities.MEETING_ROOM_NAME_DATASTORE_KEY,
            meetingRoom.name
        )
        saveInt(
            Utilities.MEETING_ROOM_COL1_DATASTORE_KEY,
            meetingRoom.startColor
        )
        saveInt(
            Utilities.MEETING_ROOM_COL2_DATASTORE_KEY,
            meetingRoom.endColor
        )
    }

    suspend fun fetchDefaultMeetingRoomName(): String {
        var data = read(Utilities.MEETING_ROOM_NAME_DATASTORE_KEY)
        if (data == null) {
            val fromFile = readDataFromFile()
            save(Utilities.MEETING_ROOM_DATASTORE_KEY, fromFile)
            val topic = gson.fromJson(fromFile, MeetingRoomData::class.java)
            data = topic.meeting_rooms[0].name
            save(Utilities.MEETING_ROOM_NAME_DATASTORE_KEY, data)
        }

        Timber.d("json name %s", data)
        return data
    }

    suspend fun fetchDefaultMeetingRoomCol1(): Int {
        var data = readInt(Utilities.MEETING_ROOM_COL1_DATASTORE_KEY)
        if (data == null) {
            val fromFile = readDataFromFile()
            save(Utilities.MEETING_ROOM_DATASTORE_KEY, fromFile)
            val topic = gson.fromJson(fromFile, MeetingRoomData::class.java)
            data = topic.meeting_rooms[0].startColor
            saveInt(Utilities.MEETING_ROOM_COL1_DATASTORE_KEY, data)
        }

        Timber.d("json name %s", data)
        return data
    }

    suspend fun fetchDefaultMeetingRoomCol2(): Int {
        var data = readInt(Utilities.MEETING_ROOM_COL2_DATASTORE_KEY)
        if (data == null) {
            val fromFile = readDataFromFile()
            save(Utilities.MEETING_ROOM_DATASTORE_KEY, fromFile)
            val topic = gson.fromJson(fromFile, MeetingRoomData::class.java)
            data = topic.meeting_rooms[0].startColor
            saveInt(Utilities.MEETING_ROOM_COL2_DATASTORE_KEY, data)
        }

        Timber.d("json name %s", data)
        return data
    }

    private suspend fun readDataFromFile() =
        context.assets.open("meeting_rooms.json").bufferedReader().use {
            it.readText()
        }

    suspend fun save(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun read(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun saveInt(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun readInt(key: String): Int? {
        val dataStoreKey = intPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey]
    }
}