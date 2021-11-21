package com.akhilasdeveloper.meetingapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.meetingapp.Utilities
import com.akhilasdeveloper.meetingapp.data.EventData
import com.akhilasdeveloper.meetingapp.repository.MeetingAppRepository
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val meetingAppRepository: MeetingAppRepository
) : ViewModel() {

    private val _dataStateEvents: MutableLiveData<Events> = MutableLiveData()
    private val _dataStateEventData: MutableLiveData<List<EventData>> = MutableLiveData()
    private val _dataStateCalendar: MutableLiveData<Calendar?> = MutableLiveData()

    val dataStateEvents: LiveData<Events>
        get() = _dataStateEvents

    val dataStateCalendar: LiveData<Calendar?>
        get() = _dataStateCalendar

    val dataStateEventData: LiveData<List<EventData>>
        get() = _dataStateEventData

    fun getEvents(calendar: Calendar, orderBy: String){
        viewModelScope.launch {
            while (true) {
                meetingAppRepository.fetchDetails(calendar = calendar, orderBy = orderBy).collect {
                    Timber.d("MainViewModel : getEvents")
                    _dataStateEvents.value = it
                }
                delay(Constants.REFRESH_DELAY)
            }
        }
    }

    fun getEventData(calendar: Calendar, orderBy: String){
        viewModelScope.launch {
            while (true) {
                meetingAppRepository.fetchDetails(calendar = calendar, orderBy = orderBy).collect {
                    val data = it.items.map { map->

                        val startTime = getStartTime(map)
                        val endTime = getEndTime(map)
                        val startDay = getStartDay(startTime)
                        val endDay = getEndDay(endTime)

                        EventData(
                            title = map.summary?:"No title",
                            description = map.description?:"NA",
                            organizerEmail = map.organizer.email?:"Not found",
                            meetingRoom = "test",
                            startTime = startTime,
                            endTime = endTime,
                            startPer = getStartPer(startTime, startDay, endDay),
                            endPer = getEndPer(endTime, startDay, endDay)
                        )
                    }
                    _dataStateEventData.value = data
                }
                delay(Constants.REFRESH_DELAY)
            }
        }
    }

    private fun getStartDay(start: Long) = Utilities.formatDateToMillis(Utilities.formatMillis(start))!!
    private fun getEndDay(end: Long) = Utilities.formatDateToMillis(Utilities.formatMillis(end))!! + Utilities.DAY_MILLIS

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

    fun setCalendar(calendar: Calendar){
        CoroutineScope(Dispatchers.Main).launch {
            _dataStateCalendar.value = calendar
        }
    }

}