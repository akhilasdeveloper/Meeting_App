package com.akhilasdeveloper.meetingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.databinding.DetailsFragmentBinding


class SettingsFragment: Fragment(R.layout.settings_fragment) {
    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DetailsFragmentBinding.bind(view)
    }
}