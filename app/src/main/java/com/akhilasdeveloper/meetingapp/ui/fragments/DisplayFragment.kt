package com.akhilasdeveloper.meetingapp.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.MeetingRecyclerAdapter
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.Utilities.formatDateToMillis
import com.akhilasdeveloper.meetingapp.Utilities.formatMillis
import com.akhilasdeveloper.meetingapp.Utilities.formatMillisDays
import com.akhilasdeveloper.meetingapp.Utilities.formatMillisTime
import com.akhilasdeveloper.meetingapp.databinding.DisplayFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

class DisplayFragment : Fragment(R.layout.display_fragment) {
    private var _binding: DisplayFragmentBinding? = null
    private val binding get() = _binding!!

    private val APPLICATION_NAME = "Meeting App"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private var calendar: Calendar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DisplayFragmentBinding.bind(view)

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.displayFragmentView) { v, insets ->
            val systemWindows =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            binding.displayFragmentView.updatePadding(top = systemWindows.top)
            binding.displayFragmentView.updatePadding(bottom = systemWindows.bottom)
            return@setOnApplyWindowInsetsListener insets
        }
        CoroutineScope(Dispatchers.IO).launch {
            authenticate(APPLICATION_NAME, CalendarScopes.CALENDAR_READONLY)
        }
        binding.meetingName.isSelected = true
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            setHasFixedSize(true)
        }
        binding.showAll.setOnClickListener {
            findNavController().navigate(R.id.action_displayFragment_to_detailsFragment)
        }
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_displayFragment_to_settingsFragment)
        }
    }

    private suspend fun signIn() {
        Timber.d( "Start sign in")
        getSignInClient(CalendarScopes.CALENDAR_READONLY)?.let {
            resultLauncher.launch(it.signInIntent)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    authenticate(APPLICATION_NAME, CalendarScopes.CALENDAR_READONLY)
                }
            }
        }

    private fun getSignInClient(scopeName: String): GoogleSignInClient? {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        if (scopeName.isNotEmpty()) signInOptions.requestScopes(Scope(scopeName))
        signInOptions.requestEmail()
        return GoogleSignIn.getClient(requireActivity(), signInOptions.build())
    }

    private suspend fun authenticate(appName: String?, scopeName: String?): Boolean {

        withContext(Dispatchers.Main){
            binding.progress.visibility = View.VISIBLE
        }
        val signInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        Timber.d("Signed in as ${signInAccount?.email}")
        if (signInAccount != null) {
            val accountCredential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
                requireContext(),
                Collections.singleton(scopeName)
            )
            accountCredential.selectedAccount = signInAccount.account
            val driveBuilder =
                Calendar.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JSON_FACTORY,
                    accountCredential
                )
            driveBuilder.applicationName = appName
            calendar = driveBuilder.build()
            fetchDetails(calendar!!)
            return true
        } else {
            signIn()
        }
        return false
    }

    private suspend fun fetchDetails(calendar: Calendar) {

        Timber.d( "Called fetchDetails ${System.currentTimeMillis()}")

        val events: Events = calendar.events().list("primary")
            .setTimeMin(DateTime(formatDateToMillis(formatMillis(System.currentTimeMillis()))!!))
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()

        val items = events.items

        withContext(Dispatchers.Main){
            binding.progress.visibility = View.GONE
        }

        if (items.isEmpty()) {
            Timber.d( "No upcoming events found")
            withContext(Dispatchers.Main) {
                binding.upcoming.text = "No Upcoming Events"
            }
        } else {
            withContext(Dispatchers.Main) {
                binding.upcoming.text = "Upcoming Events"
            }
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

            withContext(Dispatchers.Main) {
                setMeeting(currEvent, nextEvent)
                binding.recycler.adapter = MeetingRecyclerAdapter(events)
            }
        }
    }

    private fun setMeeting(currEvent: Event?, nextEvent: Event?) {
        if (currEvent == null) {
            binding.startTime.visibility = View.GONE
            binding.endTime.visibility = View.GONE
            binding.meetingOrganizer.visibility = View.GONE

            binding.meetingName.text = "No Meeting"

            if (nextEvent == null)
                binding.nextMeeting.text = "No upcoming meeting for today"
            else {
                var start = nextEvent.start.dateTime
                if (start == null) {
                    start = nextEvent.start.date
                }
                binding.nextMeeting.text =
                    "Next meeting in ${formatMillisDays(start.value - System.currentTimeMillis())}"
            }
        } else {
            binding.meetingName.visibility = View.VISIBLE
            binding.startTime.visibility = View.VISIBLE
            binding.endTime.visibility = View.VISIBLE
            binding.meetingOrganizer.visibility = View.VISIBLE

            var start = currEvent.start.dateTime
            var end = currEvent.end.dateTime
            if (start == null) {
                start = currEvent.start.date
            }
            if (end == null) {
                end = currEvent.end.date
            }

            binding.meetingName.text = currEvent.summary ?: "No title"
            binding.startTime.text = "${formatMillisTime(start.value)}"
            binding.endTime.text = "${formatMillisTime(end.value)}"
            binding.meetingOrganizer.text = "Organizer : " + currEvent.organizer.email

            if (nextEvent == null)
                binding.nextMeeting.text = "No upcoming meeting for today"
            else {
                var start = nextEvent.start.dateTime
                if (start == null) {
                    start = nextEvent.start.date
                }
                binding.nextMeeting.text =
                    "Next meeting in ${formatMillisDays(start.value - System.currentTimeMillis())}"
            }

        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.Main){
                    binding.progress.visibility = View.VISIBLE
                }

                calendar?.let {
                    fetchDetails(it)
                }
                delay((1000 * 60).toLong())
            }
        }
    }

}