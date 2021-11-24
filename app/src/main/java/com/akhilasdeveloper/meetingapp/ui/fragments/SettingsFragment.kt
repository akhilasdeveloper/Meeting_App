package com.akhilasdeveloper.meetingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.akhilasdeveloper.meetingapp.*
import com.akhilasdeveloper.meetingapp.data.MeetingRoom
import com.akhilasdeveloper.meetingapp.databinding.MeetingRoomColorPopupLayoutBinding
import com.akhilasdeveloper.meetingapp.databinding.MeetingRoomNamePopupLayoutBinding


@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment), MeetingRoomClickListener, MeetingRoomColorClickListener {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var generateMeetingRooms: GenerateMeetingRooms

    @Inject
    lateinit var colorDatas: ColorDatas


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = SettingsFragmentBinding.bind(view)

        init()
        setClickListener()
    }

    private fun setClickListener() {
        binding.addNewMeeting.setOnClickListener {

            val alertCustomdialog = MeetingRoomNamePopupLayoutBinding.inflate(LayoutInflater.from(requireContext()),binding.settingsLayout,false)
            val alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alert.setView(alertCustomdialog.root)
            val dialog: AlertDialog = alert.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            alertCustomdialog.meetingRoomNamePopupEditCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
            alertCustomdialog.meetingRoomNamePopupEditOk.setOnClickListener {
                alertCustomdialog.meetingRoomNamePopupEditText.text?.toString().let { text->
                    if (!text.isNullOrEmpty())
                        viewLifecycleOwner.lifecycleScope.launch {
                            generateMeetingRooms.addMeetingRoomName(
                                MeetingRoom(
                                    name = text,
                                    startColor = 0,
                                    endColor = 0,
                                    id = System.currentTimeMillis()
                                )
                            )
                            refreshUI()
                            dialog.dismiss()
                        }
                }
            }

        }
    }

    private fun init() {

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsLayout) { v, insets ->
            val systemWindows =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            binding.settingsLayout.updatePadding(top = systemWindows.top)
//            binding.meetingRoom.updatePadding(bottom = systemWindows.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        binding.meetingRoomsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }

        refreshUI()
    }

    private fun refreshUI(){
        viewLifecycleOwner.lifecycleScope.launch {
            binding.meetingRoomsRecycler.adapter = MeetingRoomRecyclerAdapter(
                generateMeetingRooms.fetchMeetingData().meeting_rooms,
                colorDatas,
                generateMeetingRooms.fetchDefaultMeetingRoomName(),
                this@SettingsFragment
            )
        }
    }

    override fun onItemSelected(meetingRoom: MeetingRoom, position: Int) {

    }

    override fun onItemRadioSelected(meetingRoom: MeetingRoom, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val msg = generateMeetingRooms.updateDefaultMeetingRoom(meetingRoom)
            displayToast(msg)
            refreshUI()
        }
    }

    override fun onItemEditSelected(meetingRoom: MeetingRoom, position: Int) {
        val alertCustomdialog = MeetingRoomNamePopupLayoutBinding.inflate(LayoutInflater.from(requireContext()),binding.settingsLayout,false)
        val alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alert.setView(alertCustomdialog.root)
        val dialog: AlertDialog = alert.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertCustomdialog.meetingRoomNamePopupEditText.setText(meetingRoom.name)
        dialog.show()

        alertCustomdialog.meetingRoomNamePopupEditCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        alertCustomdialog.meetingRoomNamePopupEditOk.setOnClickListener {
            alertCustomdialog.meetingRoomNamePopupEditText.text?.toString().let { text->
                if (!text.isNullOrEmpty())
                    viewLifecycleOwner.lifecycleScope.launch {
                        val saveData = MeetingRoom(name = text, startColor = meetingRoom.startColor, endColor = meetingRoom.endColor, id = meetingRoom.id)
                        val msg = generateMeetingRooms.updateMeetingRoomName(saveData)
                        displayToast(msg)
                        refreshUI()
                        dialog.dismiss()
                    }
            }
        }
    }

    override fun onItemDeleteSelected(meetingRoom: MeetingRoom, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            generateMeetingRooms.deleteMeetingRoom(meetingRoom)
            refreshUI()
        }
    }

    override fun onItemColorSelected(meetingRoom: MeetingRoom, position: Int) {
        val alertCustomdialog = MeetingRoomColorPopupLayoutBinding.inflate(LayoutInflater.from(requireContext()),binding.settingsLayout,false)
        val alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alert.setView(alertCustomdialog.root)
        val dialog: AlertDialog = alert.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertCustomdialog.colorRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(),5)
            setHasFixedSize(true)
            adapter = MeetingRoomColorRecyclerAdapter(colorDatas,meetingRoom,dialog,this@SettingsFragment)
        }
        dialog.show()

        alertCustomdialog.meetingRoomNamePopupEditCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })

    }

    override fun onItemColorSelectorClicked(meetingRoom: MeetingRoom, position: Int, alertDialog: AlertDialog) {
        viewLifecycleOwner.lifecycleScope.launch {
            val msg = generateMeetingRooms.updateMeetingRoom(meetingRoom)
            displayToast(msg)
            refreshUI()
            alertDialog.dismiss()
        }
    }

    private fun displayToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}