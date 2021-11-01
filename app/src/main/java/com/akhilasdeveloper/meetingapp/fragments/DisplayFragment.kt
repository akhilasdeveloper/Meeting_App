package com.akhilasdeveloper.meetingapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.databinding.DisplayFragmentBinding

class DisplayFragment: Fragment(R.layout.display_fragment) {
    private var _binding: DisplayFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DisplayFragmentBinding.bind(view)
    }
}