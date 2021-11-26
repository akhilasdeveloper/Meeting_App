package com.akhilasdeveloper.meetingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.data.EventData

class MeetingRecyclerAdapter(private val dataSet: List<EventData>, private val context: Context) :
    RecyclerView.Adapter<MeetingRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val meetingTitle: TextView = view.findViewById(R.id.card_title)
        val meetingDate: TextView = view.findViewById(R.id.card_date)
        val meetingFromTime: TextView = view.findViewById(R.id.card_meeting_from_time)
        val meetingToTime: TextView = view.findViewById(R.id.card_meeting_to_time)
        val meetingOrganizer: TextView = view.findViewById(R.id.card_organizer)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.meetingOrganizer.isSelected = true
        viewHolder.meetingTitle.text = dataSet[position].title
        viewHolder.meetingOrganizer.text = context.getString(R.string.organizer, dataSet[position].organizerEmail)
        viewHolder.meetingDate.text = context.getString(R.string.date, Utilities.formatMillis(dataSet[position].startTime))
        viewHolder.meetingFromTime.text = Utilities.formatMillisTime(dataSet[position].startTime)
        viewHolder.meetingToTime.text =
            Utilities.formatMillisTime(
                dataSet[position].endTime
            )

    }

    override fun getItemCount() = dataSet.size

}