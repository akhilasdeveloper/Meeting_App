package com.akhilasdeveloper.meetingapp.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.meetingapp.GenerateMeetingRooms
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.Utilities
import com.akhilasdeveloper.meetingapp.data.EventData
import com.akhilasdeveloper.meetingapp.data.MeetingRoom
import com.akhilasdeveloper.meetingapp.data.MeetingRoomData
import com.akhilasdeveloper.meetingapp.repository.MeetingAppRepository
import com.akhilasdeveloper.meetingapp.ui.Constants.NETWORK_ERROR
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val meetingAppRepository: MeetingAppRepository,
    private val generateMeetingRooms: GenerateMeetingRooms,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _dataStateEventData: MutableLiveData<List<EventData>> = MutableLiveData()
    private val _dataStateCalendar: MutableLiveData<Calendar?> = MutableLiveData()
    private val _dataStateLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val _dataStateMessage: MutableLiveData<String?> = MutableLiveData()

    val dataStateMessage: LiveData<String?>
        get() = _dataStateMessage

    val dataStateLoading: LiveData<Boolean>
        get() = _dataStateLoading

    val dataStateCalendar: LiveData<Calendar?>
        get() = _dataStateCalendar

    val dataStateEventData: LiveData<List<EventData>?>
        get() = _dataStateEventData

    fun getEventData(calendar: Calendar, orderBy: String) {
        viewModelScope.launch {
            while (true) {
                setLoading(true)
                getEventDataFromRepository(calendar, orderBy)
                delay(Constants.REFRESH_DELAY)
            }
        }
    }

    fun getEventDataWithoutLoop(orderBy: String) {
        setLoading(true)
        viewModelScope.launch {
            dataStateCalendar.value?.let {
                getEventDataFromRepository(it, orderBy)
            }
        }
    }

    private suspend fun getEventDataFromRepository(calendar: Calendar, orderBy: String) {
        val meetingRooms = generateMeetingRooms.fetchMeetingData()

        try {
            meetingAppRepository.fetchDetails(calendar = calendar, orderBy = orderBy).collect {

                val data = it.items.filter { event ->
                    isMeetingRoom(meetingRooms, event.description)
                }.map { map ->

                    val startTime = getStartTime(map)
                    val endTime = getEndTime(map)
                    val startDay = getStartDay(startTime)
                    val endDay = getEndDay(endTime)

                    EventData(
                        title = map.summary ?: context.getString(R.string.no_title),
                        description = map.description ?: "NA",
                        organizerEmail = map.organizer.email
                            ?: context.getString(R.string.no_organizer),
                        meetingRoom = findMeetingRoom(meetingRooms, map.description),
                        startTime = startTime,
                        endTime = endTime,
                        startPer = getStartPer(startTime, startDay, endDay),
                        endPer = getEndPer(endTime, startDay, endDay),
                        isValid = true
                    )
                }

                val validatedData: List<EventData> = validatedData(data)

                setLoading(false)
                _dataStateEventData.value = validatedData
                setMessage(null)
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
            setLoading(false)
            setMessage(NETWORK_ERROR)
        }
    }

    private fun validatedData(data: List<EventData>): List<EventData> {

        val finalData = mutableListOf<EventData>()

        data.forEach { eventData ->

            val finalData0 =
                finalData.filter { eventData2 -> eventData2.meetingRoom == eventData.meetingRoom && eventData2.isValid }
                    .toMutableList()

            if (finalData0.isEmpty())
                finalData.add(eventData)
            else {

                var isValid = true

                finalData0.forEach { eventData0 ->
                    if (isValid)
                        isValid = isValidMeeting(eventData, eventData0)
                }

                finalData.add(eventData.copy(isValid = isValid))

            }
        }

        return finalData.toList()
    }

    private fun isValidMeeting(eventData: EventData, eventData0: EventData): Boolean {
        if (eventData.startTime>=eventData0.startTime && eventData.startTime<eventData0.endTime)
            return false
        if (eventData.endTime<=eventData0.endTime && eventData.endTime>eventData0.startTime)
            return false
        if (eventData0.startTime>=eventData.startTime && eventData0.startTime<eventData.endTime)
            return false
        if (eventData0.endTime<=eventData.endTime && eventData0.endTime>eventData.startTime)
            return false
        return true
    }

    private fun isMeetingRoom(meetingRooms: MeetingRoomData, description: String?): Boolean {
        description?.let {
            for (i in meetingRooms.meeting_rooms) {
                if (description.lowercase().contains(i.name.lowercase())) {
                    return true
                }
            }
        }
        return false
    }

    private fun findMeetingRoom(meetingRooms: MeetingRoomData, description: String?): MeetingRoom {
        description?.let {
            for (i in meetingRooms.meeting_rooms) {
                if (description.lowercase().contains(i.name.lowercase())) {
                    return i
                }
            }
        }
        return meetingRooms.meeting_rooms[0]
    }

    private fun getStartDay(start: Long) =
        Utilities.formatDateToMillis(Utilities.formatMillis(start))!!

    private fun getEndDay(end: Long) =
        Utilities.formatDateToMillis(Utilities.formatMillis(end))!! + Utilities.DAY_MILLIS

    private fun getStartPer(start: Long, startDay: Long, endDay: Long): Float {
        val dayDiff = endDay - startDay
        val startDayDiff = start - startDay
        return if (dayDiff == 0L) 0f else startDayDiff.toFloat() / dayDiff.toFloat()
    }

    private fun getEndPer(end: Long, startDay: Long, endDay: Long): Float {
        val dayDiff = endDay - startDay
        val endDayDiff = end - startDay
        return if (dayDiff == 0L) 0f else endDayDiff.toFloat() / dayDiff.toFloat()
    }

    private fun getStartTime(map: Event): Long {
        var start = map.start.dateTime
        if (start == null) {
            start = map.start.date
        }
        return start.value
    }

    private fun getEndTime(map: Event): Long {
        var end = map.end.dateTime
        if (end == null) {
            end = map.end.date
        }
        return end.value
    }

    fun setCalendar(calendar: Calendar) {
        viewModelScope.launch {
            _dataStateCalendar.value = calendar
        }
    }

    private fun setLoading(boolean: Boolean) {
        viewModelScope.launch {
            _dataStateLoading.value = boolean
        }
    }

    private fun setMessage(message: String?) {
        viewModelScope.launch {
            _dataStateMessage.value = message
        }
    }

}