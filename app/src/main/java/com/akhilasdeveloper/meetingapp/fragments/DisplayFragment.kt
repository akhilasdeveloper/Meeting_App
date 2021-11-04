package com.akhilasdeveloper.meetingapp.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.MeetingRecyclerAdapter
import com.akhilasdeveloper.meetingapp.R
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DisplayFragment: Fragment(R.layout.display_fragment) {
    private var _binding: DisplayFragmentBinding? = null
    private val binding get() = _binding!!

    private val TAG = "drive-quickstart"
    private val APPLICATION_NAME = "Google Calendar API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()

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

        signIn()
    }

    private fun signIn() {
        Log.i(TAG, "Start sign in")
        getSignInClient(CalendarScopes.CALENDAR_READONLY)?.let {
            resultLauncher.launch(it.signInIntent)
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        val signInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        withContext(Dispatchers.Main){
            Toast.makeText(requireContext(), "** Silent ${signInAccount?.email}", Toast.LENGTH_SHORT).show()
        }
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
            val calendar = driveBuilder.build()
            fetchDetails(calendar)
            return true
        }
        return false
    }

    private suspend fun fetchDetails(calendar: Calendar) {

        // List the next 10 events from the primary calendar.

        // List the next 10 events from the primary calendar.
        val now = DateTime(System.currentTimeMillis())
        val events: Events = calendar.events().list("primary")
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items = events.items
        if (items.isEmpty()) {
            println("** No upcoming events found.")
        } else {
            println("** Upcoming events")
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
                    if (start.value <= System.currentTimeMillis() && end.value <= System.currentTimeMillis())
                        currEvent = event

                if (currEvent == null)
                    if (start.value > System.currentTimeMillis())
                        nextEvent = event

                System.out.printf("** %s (%s)\n", event.summary, start)
            }

            withContext(Dispatchers.Main){

                setMeeting(currEvent,nextEvent)

                binding.recycler.apply {
                    layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = LinearLayoutManager.HORIZONTAL
                    }
                    setHasFixedSize(true)
                    adapter = MeetingRecyclerAdapter(events)
                }
            }
        }
    }

    private fun setMeeting(currEvent: Event?, nextEvent: Event?) {
        if (currEvent==null){
            binding.meetingName.visibility = View.GONE
            binding.meetingCard.visibility = View.GONE
            binding.meetingCardEmpty.visibility = View.VISIBLE
            if (nextEvent==null)
                binding.nextMeetingEmpty.text = "No upcoming meeting for today"
            else {
                var start = nextEvent.start.dateTime
                if (start == null) {
                    start = nextEvent.start.date
                }
                binding.nextMeetingEmpty.text = "Next meeting in ${start.value-System.currentTimeMillis()} milliseconds"
            }
        }else{
            binding.meetingName.visibility = View.VISIBLE
            binding.meetingCard.visibility = View.VISIBLE
            binding.meetingCardEmpty.visibility = View.GONE

            var start = currEvent.start.dateTime
            var end = currEvent.end.dateTime
            if (start == null) {
                start = currEvent.start.date
            }
            if (end == null) {
                end = currEvent.end.date
            }

            binding.meetingName.text = currEvent.summary
            binding.meetingTime.text = "${start.value to end.value}"
            binding.meetingOrganizer.text = currEvent.organizer.email
        }
    }
}