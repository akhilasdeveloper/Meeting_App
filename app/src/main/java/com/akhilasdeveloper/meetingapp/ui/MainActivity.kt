package com.akhilasdeveloper.meetingapp.ui

import android.os.Bundle

import com.akhilasdeveloper.meetingapp.databinding.ActivityMainBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {


     private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.viewPager.adapter = MeetingViewPagerAdapter(supportFragmentManager,lifecycle)

        //signIn()

    }

}