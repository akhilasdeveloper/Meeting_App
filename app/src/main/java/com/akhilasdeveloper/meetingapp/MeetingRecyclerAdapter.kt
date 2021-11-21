package com.akhilasdeveloper.meetingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.data.EventData

class MeetingRecyclerAdapter(private val dataSet: List<EventData>) :
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

        viewHolder.meetingOrganizer.isSelected = true
        viewHolder.meetingTitle.text = dataSet[position].title
        viewHolder.meetingOrganizer.text = "Organizer : ${dataSet[position].organizerEmail}"
        viewHolder.meetingDate.text = "Date : ${Utilities.formatMillis(dataSet[position].startTime)}"
        viewHolder.meetingFromTime.text = "${Utilities.formatMillisTime(dataSet[position].startTime)}"
        viewHolder.meetingToTime.text = "${
            Utilities.formatMillisTime(
                dataSet[position].endTime
            )
        }"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}