package com.akhilasdeveloper.meetingapp

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.Utilities.DAY_MILLIS
import com.akhilasdeveloper.meetingapp.data.EventData
import timber.log.Timber

class MeetingListRecyclerAdapter(private val dataSet: List<EventData>, private val colorDatas: ColorDatas) :
    RecyclerView.Adapter<MeetingListRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meetingTitle: TextView = view.findViewById(R.id.meeting_name_details)
        val meetingFrom: TextView = view.findViewById(R.id.meeting_from_details)
        val meetingTo: TextView = view.findViewById(R.id.meeting_to_details)
        val meetingRoomDetails: TextView = view.findViewById(R.id.meeting_room_details)
        val meetingGuideFrom: Guideline = view.findViewById(R.id.guideline_from)
        val meetingGuideTo: Guideline = view.findViewById(R.id.guideline_to)
        val timeBackground: LinearLayout = view.findViewById(R.id.time_background)

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

        val startColor = dataSet[position].meetingRoom.startColor
        val i = if (colorDatas.colors.size <= startColor) 0 else startColor
        val colorData = colorDatas.colors[i]
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorData.start, colorData.end)
        )

        viewHolder.meetingTitle.text = dataSet[position].title
        viewHolder.meetingRoomDetails.text = dataSet[position].meetingRoom.name
        viewHolder.meetingFrom.text = "${Utilities.formatMillisTime(dataSet[position].startTime)}"
        viewHolder.meetingTo.text = "${
            Utilities.formatMillisTime(
                dataSet[position].endTime
            )
        }"
        viewHolder.meetingGuideFrom.setGuidelinePercent(dataSet[position].startPer)
        viewHolder.meetingGuideTo.setGuidelinePercent(dataSet[position].endPer)
        viewHolder.timeBackground.background = gd
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}