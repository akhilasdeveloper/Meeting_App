package com.akhilasdeveloper.meetingapp

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.data.MeetingRoom

class MeetingRoomColorRecyclerAdapter(
    private val colorDatas: ColorDatas,
    private val meetingRoom: MeetingRoom,
    private val alertDialog: AlertDialog,
    private val interaction: MeetingRoomColorClickListener? = null
) :
    RecyclerView.Adapter<MeetingRoomColorRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meetingRoomColorItem: TextView = view.findViewById(R.id.meeting_color_item)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.meeting_color_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        val colorData = colorDatas.colors[position]
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorData.start, colorData.end)
        )
        viewHolder.meetingRoomColorItem.setTextColor(colorData.font)
        viewHolder.meetingRoomColorItem.background = gd
        viewHolder.meetingRoomColorItem.setOnClickListener {
            interaction?.onItemColorSelectorClicked(MeetingRoom(id = meetingRoom.id,endColor = meetingRoom.endColor, startColor = position, name = meetingRoom.name),position,alertDialog)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = colorDatas.colors.size

}