package com.akhilasdeveloper.meetingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhilasdeveloper.meetingapp.ColorDatas
import com.akhilasdeveloper.meetingapp.MeetingListRecyclerAdapter
import com.akhilasdeveloper.meetingapp.R
import com.akhilasdeveloper.meetingapp.data.EventData
import com.akhilasdeveloper.meetingapp.databinding.DetailsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment: BaseFragment(R.layout.details_fragment) {

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var colorDatas: ColorDatas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DetailsFragmentBinding.bind(view)

        init()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataStateEventData.observe(viewLifecycleOwner, {
            it?.let {
                setUI(it)
            }
        })
    }

    private fun setUI(events: List<EventData>) {
        binding.detailsRecycler.adapter = MeetingListRecyclerAdapter(events, colorDatas)
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