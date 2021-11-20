package com.akhilasdeveloper.meetingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.Utilities.DAY_MILLIS
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import timber.log.Timber

class MeetingListRecyclerAdapter(private val dataSet: List<Event>) :
    RecyclerView.Adapter<MeetingListRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meetingTitle: TextView = view.findViewById(R.id.meeting_name_details)
        val meetingFrom: TextView = view.findViewById(R.id.meeting_from_details)
        val meetingTo: TextView = view.findViewById(R.id.meeting_to_details)
        val meetingGuideFrom: Guideline = view.findViewById(R.id.guideline_from)
        val meetingGuideTo: Guideline = view.findViewById(R.id.guideline_to)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.details_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var start = dataSet[position].start.dateTime
        var end = dataSet[position].end.dateTime
        if (start == null) {
            start = dataSet[position].start.date
        }
        if (end == null) {
            end = dataSet[position].end.date
        }

        val startDay = Utilities.formatDateToMillis(Utilities.formatMillis(start.value))!!
        val endDay = Utilities.formatDateToMillis(Utilities.formatMillis(end.value))!! + DAY_MILLIS
        val dayDiff = endDay - startDay
        val startDayDiff = start.value - startDay
        val endDayDiff = end.value - startDay
        val startPer = if (dayDiff == 0L) 0f else startDayDiff.toFloat() / dayDiff.toFloat()
        val endPer = if (dayDiff == 0L) 0f else endDayDiff.toFloat() / dayDiff.toFloat()

        Timber.d("MeetingListRecyclerAdapter startDay : $startDay : ${Utilities.formatMillis(startDay)}")
        Timber.d("MeetingListRecyclerAdapter start : ${start.value} : ${Utilities.formatMillis(start.value)}")
        Timber.d("MeetingListRecyclerAdapter end : ${end.value} : ${Utilities.formatMillis(end.value)}")
        Timber.d("MeetingListRecyclerAdapter endDay : $endDay : ${Utilities.formatMillis(endDay)}")
        Timber.d("MeetingListRecyclerAdapter dayDiff : $dayDiff : ${Utilities.formatMillis(dayDiff)}")
        Timber.d("MeetingListRecyclerAdapter startDayDiff : $startDayDiff : ${Utilities.formatMillis(startDayDiff)}")
        Timber.d("MeetingListRecyclerAdapter endDayDiff : $endDayDiff : ${Utilities.formatMillis(endDayDiff)}")
        Timber.d("MeetingListRecyclerAdapter startPer : $startPer")
        Timber.d("MeetingListRecyclerAdapter endPer : $endPer")

        viewHolder.meetingTitle.text = "${dataSet[position].summary?:"No title"}"
        viewHolder.meetingFrom.text = "${Utilities.formatMillisTime(start.value)}"
        viewHolder.meetingTo.text = "${
            Utilities.formatMillisTime(
                end.value
            )
        }"
        viewHolder.meetingGuideFrom.setGuidelinePercent(startPer)
        viewHolder.meetingGuideTo.setGuidelinePercent(endPer)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}