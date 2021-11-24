package com.akhilasdeveloper.meetingapp.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.ColorDatas
import com.akhilasdeveloper.meetingapp.GenerateMeetingRooms
import com.akhilasdeveloper.meetingapp.MeetingRecyclerAdapter
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.Utilities.formatMillisDays
import com.akhilasdeveloper.meetingapp.Utilities.formatMillisTime
import com.akhilasdeveloper.meetingapp.data.EventData
import com.akhilasdeveloper.meetingapp.databinding.DisplayFragmentBinding
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import android.graphics.drawable.GradientDrawable
import android.widget.Toast


@AndroidEntryPoint
class DisplayFragment : BaseFragment(R.layout.display_fragment) {

    private var _binding: DisplayFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var jsonFactory: JacksonFactory

    @Inject
    lateinit var generateMeetingRooms: GenerateMeetingRooms

    @Inject
    lateinit var colorDatas: ColorDatas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DisplayFragmentBinding.bind(view)

        init()
        clickListeners()
        initSignIn()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataStateCalendar.observe(viewLifecycleOwner, {
            it?.let {
                fetchEvents(it)
            }
        })

        viewModel.dataStateEventData.observe(viewLifecycleOwner, {
            it?.let {
                setUI(it)
            }
        })

        viewModel.dataStateMessage.observe(viewLifecycleOwner, {
            it?.let {
                displayToast(it)
            }
        })

        viewModel.dataStateLoading.observe(viewLifecycleOwner, {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun fetchEvents(it: Calendar) {
        if (viewModel.dataStateEventData.value == null)
            viewModel.getEventData(it, "startTime")
    }

    private fun initSignIn() {

        val signInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (signInAccount != null) {

            Timber.d("Signed in as ${signInAccount.email}")

            authenticate(
                signInAccount
            )
        } else {

            Timber.d("Not Signed In")

            getSignInClient()?.let {
                resultLauncher.launch(it.signInIntent)
            }

        }

    }

    private fun clickListeners() {
        binding.showAll.setOnClickListener {
            findNavController().navigate(R.id.action_displayFragment_to_detailsFragment)
        }
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_displayFragment_to_settingsFragment)
        }
    }

    private fun init() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.displayFragmentView) { v, insets ->
            val systemWindows =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            binding.displayFragmentView.updatePadding(top = systemWindows.top)
            binding.displayFragmentView.updatePadding(bottom = systemWindows.bottom)
            return@setOnApplyWindowInsetsListener insets
        }
        binding.meetingName.isSelected = true
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.meetingRoomTitle.text = generateMeetingRooms.fetchDefaultMeetingRoomName()
            setUIColor(generateMeetingRooms.fetchDefaultMeetingRoomCol1())
            val startColor = generateMeetingRooms.fetchDefaultMeetingRoomCol1()
            val i = if (colorDatas.colors.size <= startColor) 0 else startColor
            val colorData = colorDatas.colors[i]
            binding.progress.setIndicatorColor(colorData.start)
        }

        loadFromViewModel()
    }

    private fun loadFromViewModel() {
        viewModel.dataStateEventData.value?.let {
            setUI(it)
        }?:kotlin.run{
            viewModel.dataStateCalendar.value?.let {
                viewModel.getEventDataWithoutLoop(it,"startTime")
            }
        }
    }

    private fun setUIColor(index: Int) {
        val i = if (colorDatas.colors.size <= index) 0 else index
        val colorData = colorDatas.colors[i]
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TR_BL, intArrayOf(colorData.start, colorData.end)
        )
        binding.meetingDetailsBackground.background = gd
        binding.showAll.setBackgroundColor(colorData.start)
        binding.settings.setBackgroundColor(colorData.end)
        binding.showAll.setTextColor(colorData.font)
        binding.settings.setTextColor(colorData.font)
        binding.meetingName.setTextColor(colorData.font)
        binding.nextMeeting.setTextColor(colorData.font)
        binding.meetingTimeLayout.startLabel.setTextColor(colorData.font)
        binding.meetingTimeLayout.endLabel.setTextColor(colorData.font)
        binding.meetingTimeLayout.startTime.setTextColor(colorData.font)
        binding.meetingTimeLayout.endTime.setTextColor(colorData.font)
        binding.meetingTimeLayout.meetingOrganizer.setTextColor(colorData.font)
        binding.divider1.setBackgroundColor(colorData.font)
        binding.meetingTimeLayout.divider2.setBackgroundColor(colorData.font)
        binding.meetingTimeLayout.divider3.setBackgroundColor(colorData.font)
        binding.meetingTimeLayout.divider4.setBackgroundColor(colorData.font)
        binding.meetingTimeLayout.divider5.setBackgroundColor(colorData.font)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                initSignIn()
            }
        }

    private fun getSignInClient(): GoogleSignInClient? {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        signInOptions.requestScopes(Scope(CalendarScopes.CALENDAR_READONLY))
        signInOptions.requestEmail()
        return GoogleSignIn.getClient(requireActivity(), signInOptions.build())
    }

    private fun authenticate(
        signInAccount: GoogleSignInAccount
    ) {

        val accountCredential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            Collections.singleton(CalendarScopes.CALENDAR_READONLY)
        )

        accountCredential.selectedAccount = signInAccount.account
        val driveBuilder =
            Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                jsonFactory,
                accountCredential
            )
        driveBuilder.applicationName = getString(R.string.app_name)

        viewModel.setCalendar(driveBuilder.build())

    }

    private fun setUI(eventsData: List<EventData>) {

        viewLifecycleOwner.lifecycleScope.launch {

            val meetingRoom = generateMeetingRooms.fetchDefaultMeetingRoomName()

            val events = eventsData.filter {
                it.description.lowercase().contains(meetingRoom.lowercase())
            }

            val mutEvents = events.toMutableList()

            if (events.isEmpty()) {
                Timber.d("No upcoming events found")
                binding.upcoming.text = getString(R.string.no_upcoming_meeting)
                binding.nextMeeting.text = getString(R.string.no_upcoming_meeting)
                binding.meetingName.text = getString(R.string.no_meeting)
                binding.meetingTimeLayout.root.visibility = View.GONE
                binding.meetingTimeLayout.meetingOrganizer.visibility = View.GONE
            } else {
                binding.upcoming.text = getString(R.string.upcoming_events)

                var currEvent: EventData? = null
                var nextEvent: EventData? = null
                for (event in events) {

                    if (event.startTime < System.currentTimeMillis() && event.endTime < System.currentTimeMillis())
                        mutEvents.remove(event)

                    if (currEvent == null)
                        if (event.startTime <= System.currentTimeMillis() && event.endTime >= System.currentTimeMillis())
                            currEvent = event

                    if (nextEvent == null)
                        if (event.startTime > System.currentTimeMillis()) {
                            nextEvent = event
                            break
                        }

                }

                mutEvents.remove(currEvent)
                setMeeting(currEvent, nextEvent)
                binding.recycler.adapter = MeetingRecyclerAdapter(mutEvents)
            }
        }
    }

    private fun setMeeting(currEvent: EventData?, nextEvent: EventData?) {
        if (currEvent == null) {
            binding.meetingTimeLayout.root.visibility = View.GONE
            binding.meetingTimeLayout.meetingOrganizer.visibility = View.GONE

            binding.meetingName.text = getString(R.string.no_meeting)

            if (nextEvent == null)
                binding.nextMeeting.text = getString(R.string.no_upcoming_meeting)
            else {
                binding.nextMeeting.text = getString(
                    R.string.next_meeting,
                    formatMillisDays(nextEvent.startTime - System.currentTimeMillis())
                )
            }
        } else {
            binding.meetingTimeLayout.root.visibility = View.VISIBLE
            binding.meetingTimeLayout.meetingOrganizer.visibility = View.VISIBLE

            binding.meetingName.text = currEvent.title
            binding.meetingTimeLayout.startTime.text = formatMillisTime(currEvent.startTime)
            binding.meetingTimeLayout.endTime.text = formatMillisTime(currEvent.endTime)
        }
        binding.meetingTimeLayout.meetingOrganizer.text = getString(
            R.string.organizer,
            currEvent?.organizerEmail ?: getString(R.string.no_organizer)
        )

        if (nextEvent == null)
            binding.nextMeeting.text = getString(R.string.no_upcoming_meeting)
        else {
            binding.nextMeeting.text = getString(
                R.string.next_meeting,
                formatMillisDays(nextEvent.startTime - System.currentTimeMillis())
            )
        }

    }

    private fun displayToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}