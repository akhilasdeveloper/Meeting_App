package com.akhilasdeveloper.meetingapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar

import com.akhilasdeveloper.meetingapp.databinding.ActivityMainBinding
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import java.io.*

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import java.util.*
import com.google.api.client.extensions.android.http.AndroidHttp

import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope

import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {


     private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MeetingViewPagerAdapter(supportFragmentManager,lifecycle)

        //signIn()

    }

}