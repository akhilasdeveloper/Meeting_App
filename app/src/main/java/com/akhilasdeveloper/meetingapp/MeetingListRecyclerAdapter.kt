package com.akhilasdeveloper.meetingapp

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.Utilities.DAY_MILLIS
import com.akhilasdeveloper.meetingapp.data.EventData
import timber.log.Timber

class MeetingListRecyclerAdapter(
    private val dataSet: List<EventData>,
    private val colorDatas: ColorDatas,
    private val context: Context
) :
    RecyclerView.Adapter<MeetingListRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meetingTitle: TextView = view.findViewById(R.id.meeting_name_details)
        val meetingFrom: TextView = view.findViewById(R.id.meeting_from_details)
        val meetingTo: TextView = view.findViewById(R.id.meeting_to_details)
        val meetingRoomDetails: TextView = view.findViewById(R.id.meeting_room_details)
        val meetingGuideFrom: Guideline = view.findViewById(R.id.guideline_from)
        val meetingGuideTo: Guideline = view.findViewById(R.id.guideline_to)
        val timeBackground: LinearLayout = view.findViewById(R.id.time_background)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.details_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val startColor = dataSet[position].meetingRoom.startColor
        val i = if (colorDatas.colors.size <= startColor) 0 else startColor
        val colorData = colorDatas.colors[i]
        val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorData.start, colorData.end))

        viewHolder.meetingTitle.text = dataSet[position].title
        if (!dataSet[position].isValid)
            viewHolder.itemView.alpha = .3f
        viewHolder.meetingRoomDetails.text = dataSet[position].meetingRoom.name
        viewHolder.meetingFrom.text = Utilities.formatMillisTime(dataSet[position].startTime)
        viewHolder.meetingTo.text = Utilities.formatMillisTime(dataSet[position].endTime)
        viewHolder.meetingGuideFrom.setGuidelinePercent(dataSet[position].startPer)
        viewHolder.meetingGuideTo.setGuidelinePercent(dataSet[position].endPer)
        viewHolder.timeBackground.background = gd
    }

    override fun getItemCount() = dataSet.size

}