package com.akhilasdeveloper.meetingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.Utilities.DAY_MILLIS
import com.akhilasdeveloper.meetingapp.data.EventData
import timber.log.Timber

class MeetingListRecyclerAdapter(private val dataSet: List<EventData>) :
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

        viewHolder.meetingTitle.text = dataSet[position].title
        viewHolder.meetingFrom.text = "${Utilities.formatMillisTime(dataSet[position].startTime)}"
        viewHolder.meetingTo.text = "${
            Utilities.formatMillisTime(
                dataSet[position].endTime
            )
        }"
        viewHolder.meetingGuideFrom.setGuidelinePercent(dataSet[position].startPer)
        viewHolder.meetingGuideTo.setGuidelinePercent(dataSet[position].endPer)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}