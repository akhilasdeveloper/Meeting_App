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
        val meetingFromTime: TextView = view.findViewById(R.id.card_meeting_from_time)
        val meetingToTime: TextView = view.findViewById(R.id.card_meeting_to_time)
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
        viewHolder.meetingOrganizer.isSelected = true
        viewHolder.meetingTitle.text = "${dataSet.items[position].summary?:"No title"}"
        viewHolder.meetingOrganizer.text = "Organizer : ${dataSet.items[position].organizer.email}"
        viewHolder.meetingDate.text = "Date : ${Utilities.formatMillis(start.value)}"
        viewHolder.meetingFromTime.text = "${Utilities.formatMillisTime(start.value)}"
        viewHolder.meetingToTime.text = "${
            Utilities.formatMillisTime(
                end.value
            )
        }"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.items.size

}