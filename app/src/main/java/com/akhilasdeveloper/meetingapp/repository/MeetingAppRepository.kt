package com.akhilasdeveloper.meetingapp.repository

import com.akhilasdeveloper.meetingapp.Utilities
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MeetingAppRepository @Inject constructor() {

    suspend fun fetchDetails(calendar: Calendar, orderBy: String) = flow<Events> {
        val time = Utilities.formatDateToMillis(Utilities.formatMillis(System.currentTimeMillis()))!!
        emit(
            calendar.events().list("primary")
                .setTimeMin(DateTime(time))
                .setTimeMax(DateTime(time + Utilities.DAY_MILLIS))
                .setOrderBy(orderBy)
                .setSingleEvents(true)
                .execute()
        )
    }.flowOn(Dispatchers.IO)

}