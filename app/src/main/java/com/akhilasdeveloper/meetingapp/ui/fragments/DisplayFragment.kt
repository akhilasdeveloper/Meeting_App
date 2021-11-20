package com.akhilasdeveloper.meetingapp.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.MeetingRecyclerAdapter
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.Utilities.formatDateToMillis
import com.akhilasdeveloper.meetingapp.Utilities.formatMillis
import com.akhilasdeveloper.meetingapp.Utilities.formatMillisDays
import com.akhilasdeveloper.meetingapp.Utilities.formatMillisTime
import com.akhilasdeveloper.meetingapp.databinding.DisplayFragmentBinding
import com.akhilasdeveloper.meetingapp.ui.Constants.REFRESH_DELAY
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DisplayFragment : BaseFragment(R.layout.display_fragment) {

    private var _binding: DisplayFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var jsonFactory: JacksonFactory

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
                viewModel.getEvents(it, "startTime")
            }
        })

        viewModel.dataStateEvents.observe(viewLifecycleOwner, {
            setUI(it)
        })
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

//        fetchDetails(calendar!!)
    }

    /*private suspend fun fetchDetails(calendar: Calendar) {

        Timber.d("Called fetchDetails ${System.currentTimeMillis()}")

        val events: Events = calendar.events().list("primary")
            .setTimeMin(DateTime(formatDateToMillis(formatMillis(System.currentTimeMillis()))!!))
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()

        withContext(Dispatchers.Main) {
            setUI(events)
        }
    }*/

    private fun setUI(events: Events) {

        val items = events.items
        binding.progress.visibility = View.GONE

        if (items.isEmpty()) {
            Timber.d("No upcoming events found")
            binding.upcoming.text = getString(R.string.no_upcoming_meeting)
            binding.nextMeeting.text = getString(R.string.no_upcoming_meeting)
            binding.meetingName.text = getString(R.string.no_meeting)
            binding.meetingTimeLayout.root.visibility = View.GONE
            binding.meetingTimeLayout.meetingOrganizer.visibility = View.GONE
        } else {
            binding.upcoming.text = getString(R.string.upcoming_events)

            var currEvent: Event? = null
            var nextEvent: Event? = null
            for (event in items) {
                var start = event.start.dateTime
                var end = event.end.dateTime
                if (start == null) {
                    start = event.start.date
                }
                if (end == null) {
                    end = event.end.date
                }

                if (currEvent == null)
                    if (start.value <= System.currentTimeMillis() && end.value >= System.currentTimeMillis())
                        currEvent = event

                if (nextEvent == null)
                    if (start.value > System.currentTimeMillis())
                        nextEvent = event

            }

            setMeeting(currEvent, nextEvent)
            binding.recycler.adapter = MeetingRecyclerAdapter(events)

        }
    }

    private fun setMeeting(currEvent: Event?, nextEvent: Event?) {
        if (currEvent == null) {
            binding.meetingTimeLayout.root.visibility = View.GONE
            binding.meetingTimeLayout.meetingOrganizer.visibility = View.GONE

            binding.meetingName.text = getString(R.string.no_meeting)

            if (nextEvent == null)
                binding.nextMeeting.text = getString(R.string.no_upcoming_meeting)
            else {
                var start = nextEvent.start.dateTime
                if (start == null) {
                    start = nextEvent.start.date
                }
                binding.nextMeeting.text = getString(
                    R.string.next_meeting,
                    formatMillisDays(start.value - System.currentTimeMillis())
                )
            }
        } else {
            binding.meetingTimeLayout.root.visibility = View.VISIBLE
            binding.meetingTimeLayout.meetingOrganizer.visibility = View.VISIBLE

            var start = currEvent.start.dateTime
            var end = currEvent.end.dateTime
            if (start == null) {
                start = currEvent.start.date
            }
            if (end == null) {
                end = currEvent.end.date
            }

            binding.meetingName.text = currEvent.summary ?: getString(R.string.no_title)
            binding.meetingTimeLayout.startTime.text = formatMillisTime(start.value)
            binding.meetingTimeLayout.endTime.text = formatMillisTime(end.value)
        }
        binding.meetingTimeLayout.meetingOrganizer.text = getString(
            R.string.organizer,
            currEvent?.organizer?.email ?: getString(R.string.no_organizer)
        )

        if (nextEvent == null)
            binding.nextMeeting.text = getString(R.string.no_upcoming_meeting)
        else {
            var start = nextEvent.start.dateTime
            if (start == null) {
                start = nextEvent.start.date
            }
            binding.nextMeeting.text = getString(
                R.string.next_meeting,
                formatMillisDays(start.value - System.currentTimeMillis())
            )
        }

    }

/*

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    binding.progress.visibility = View.VISIBLE
                }

                viewModel.dataStateCalendar.value?.let {
                    viewModel.getEvents(it,"startTime")
                }
                delay(REFRESH_DELAY)
            }
        }
    }
*/

}