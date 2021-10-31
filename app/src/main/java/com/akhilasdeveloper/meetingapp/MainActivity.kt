package com.akhilasdeveloper.meetingapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.android.gms.common.api.ApiException

import android.content.Context
import com.akhilasdeveloper.meetingapp.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.webkit.WebView

import android.webkit.WebViewClient
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.*
import java.io.*

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import java.util.*
import com.google.api.client.extensions.android.http.AndroidHttp

import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope

import com.google.android.gms.tasks.OnCompleteListener

import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private val TAG = "drive-quickstart"
    private val APPLICATION_NAME = "Google Calendar API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
     private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                authenticate(APPLICATION_NAME,CalendarScopes.CALENDAR_READONLY)
            }
        }
    }

    private fun getSignInClient(scopeName: String): GoogleSignInClient? {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        if (scopeName.isNotEmpty()) signInOptions.requestScopes(Scope(scopeName))
        signInOptions.requestEmail()
        return GoogleSignIn.getClient(this, signInOptions.build())
    }

    private suspend fun authenticate(appName: String?, scopeName: String?): Boolean {
        val signInAccount = GoogleSignIn.getLastSignedInAccount(this)
        withContext(Dispatchers.Main){
            Toast.makeText(this@MainActivity, "** Silent ${signInAccount?.email}", Toast.LENGTH_SHORT).show()
        }
        if (signInAccount != null) {
            val accountCredential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
                this,
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
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items = events.items
        if (items.isEmpty()) {
            println("** No upcoming events found.")
        } else {
            println("** Upcoming events")
            for (event in items) {
                var start = event.start.dateTime
                if (start == null) {
                    start = event.start.date
                }
                System.out.printf("** %s (%s)\n", event.summary, start)
            }

            withContext(Dispatchers.Main){
                binding.recycler.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    setHasFixedSize(true)
                    adapter = MeetingRecyclerAdapter(events)
                }
            }
        }
    }

}