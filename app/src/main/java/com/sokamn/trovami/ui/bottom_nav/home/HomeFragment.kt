package com.sokamn.trovami.ui.bottom_nav.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sokamn.trovami.R
import com.sokamn.trovami.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var masterAdapter: MasterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setUIComponents()
        initListeners()
        initObservers()
    }

    private fun setUIComponents() {
        masterAdapter = MasterAdapter(activity = "MainActivity")


        binding.rcvMasterFH.apply {
            adapter = masterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun initListeners() {

    }

    private fun initObservers() {

    }

}
