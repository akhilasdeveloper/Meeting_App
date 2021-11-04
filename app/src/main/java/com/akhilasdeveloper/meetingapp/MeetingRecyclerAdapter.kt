package com.akhilasdeveloper.meetingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Events

class MeetingRecyclerAdapter(private val dataSet: Events) :
    RecyclerView.Adapter<MeetingRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meetingTitle: TextView = view.findViewById(R.id.card_title)
        val meetingDate: TextView = view.findViewById(R.id.card_date)
        val meetingTime: TextView = view.findViewById(R.id.card_meeting_time)
        val meetingOrganizer: TextView = view.findViewById(R.id.card_organizer)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var start = dataSet.items[position].start.dateTime
        var end = dataSet.items[position].end.dateTime
        if (start == null) {
            start = dataSet.items[position].start.date
        }
        if (end == null) {
            end = dataSet.items[position].end.date
        }
        viewHolder.meetingTitle.text = "${dataSet.items[position].summary}"
        viewHolder.meetingOrganizer.text = "${dataSet.items[position].organizer.email}"
        viewHolder.meetingDate.text = "${start.value}"
        viewHolder.meetingTime.text = "${start.value} to ${end.value}"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}