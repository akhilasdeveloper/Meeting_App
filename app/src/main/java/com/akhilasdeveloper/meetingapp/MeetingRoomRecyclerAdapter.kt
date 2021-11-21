package com.akhilasdeveloper.meetingapp

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akhilasdeveloper.meetingapp.data.EventData
import com.akhilasdeveloper.meetingapp.data.MeetingRoom

class MeetingRoomRecyclerAdapter(
    private val dataSet: List<MeetingRoom>,
    private val colorDatas: ColorDatas,
    private val meetingRoom: String,
    private val interaction: MeetingRoomClickListener? = null
) :
    RecyclerView.Adapter<MeetingRoomRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meetingRoomItemName: TextView = view.findViewById(R.id.meeting_room_item_name)
        val meetingRoomItemEdit: ImageView = view.findViewById(R.id.meeting_room_item_edit)
        val meetingRoomItemDelete: ImageView = view.findViewById(R.id.meeting_room_item_delete)
        val meetingRoomItemColor: LinearLayout = view.findViewById(R.id.meeting_room_item_color)
        val meetingRoomItemRadio: RadioButton = view.findViewById(R.id.meeting_room_item_radio)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.meeting_room_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        val startColor = dataSet[position].startColor
        val i = if (colorDatas.colors.size <= startColor) 0 else startColor
        val colorData = colorDatas.colors[i]
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorData.start, colorData.end)
        )

        viewHolder.meetingRoomItemName.text = dataSet[position].name
        viewHolder.meetingRoomItemColor.background = gd
        viewHolder.meetingRoomItemRadio.isChecked = dataSet[position].name == meetingRoom

        viewHolder.meetingRoomItemRadio.setOnClickListener {
            interaction?.onItemRadioSelected(dataSet[position],position)
        }

        viewHolder.meetingRoomItemEdit.setOnClickListener {
            interaction?.onItemEditSelected(dataSet[position],position)
        }

        viewHolder.meetingRoomItemDelete.setOnClickListener {
            interaction?.onItemDeleteSelected(dataSet[position],position)
        }

        viewHolder.meetingRoomItemColor.setOnClickListener {
            interaction?.onItemColorSelected(dataSet[position],position)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}