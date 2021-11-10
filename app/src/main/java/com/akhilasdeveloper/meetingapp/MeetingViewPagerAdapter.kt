package com.akhilasdeveloper.meetingapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akhilasdeveloper.meetingapp.ui.fragments.DetailsFragment
import com.akhilasdeveloper.meetingapp.ui.fragments.DisplayFragment

class MeetingViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return DisplayFragment()
            1 -> return DetailsFragment()
            else -> return DisplayFragment()
        }
    }

}