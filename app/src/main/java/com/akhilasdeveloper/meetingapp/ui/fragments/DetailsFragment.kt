package com.akhilasdeveloper.meetingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.MeetingListRecyclerAdapter
import com.akhilasdeveloper.meetingapp.MeetingRecyclerAdapter
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.databinding.DetailsFragmentBinding
import com.google.api.services.calendar.model.Events
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment: BaseFragment(R.layout.details_fragment) {
    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DetailsFragmentBinding.bind(view)

        init()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataStateEvents.observe(viewLifecycleOwner, {
            it?.let {
                setUI(it)
            }
        })
    }

    private fun setUI(events: Events) {
        val ev = events.items.filter { event -> if (event.description == null) false else event.description.lowercase().contains("test") }
        binding.detailsRecycler.adapter = MeetingListRecyclerAdapter(ev)
    }

    private fun init() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.detailsRecycler) { v, insets ->
            val systemWindows =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            binding.detailsRecycler.updatePadding(top = systemWindows.top)
            binding.detailsRecycler.updatePadding(bottom = systemWindows.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        binding.detailsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
    }
}