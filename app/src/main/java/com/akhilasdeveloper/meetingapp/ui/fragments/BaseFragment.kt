package com.akhilasdeveloper.meetingapp.ui.fragments

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment(layout: Int): Fragment(layout)  {
}