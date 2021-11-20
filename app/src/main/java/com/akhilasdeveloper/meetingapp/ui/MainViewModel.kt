package com.akhilasdeveloper.meetingapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.meetingapp.repository.MeetingAppRepository
import com.google.api.services.calendar.Calendar
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
    private val _dataStateCalendar: MutableLiveData<Calendar?> = MutableLiveData()

    val dataStateEvents: LiveData<Events>
        get() = _dataStateEvents

    val dataStateCalendar: LiveData<Calendar?>
        get() = _dataStateCalendar

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

    fun setCalendar(calendar: Calendar){
        CoroutineScope(Dispatchers.Main).launch {
            _dataStateCalendar.value = calendar
        }
    }

}