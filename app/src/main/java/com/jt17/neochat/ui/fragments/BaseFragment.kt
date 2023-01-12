package com.jt17.neochat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jt17.neochat.R
import com.jt17.neochat.databinding.AppBarMainBinding
import com.jt17.neochat.databinding.FragmentBaseBinding

class BaseFragment : Fragment() {
    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var appBarMainBinding: AppBarMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseBinding.inflate(inflater, container, false)
        appBarMainBinding = binding.appBarId
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDrawerLy()

    }

    private fun initDrawerLy() {
        appBarMainBinding.mToolbar.setNavigationOnClickListener {
            binding.drawerLy.open()
        }

        binding.navView.setNavigationItemSelectedListener {
            it.isChecked = true
            binding.drawerLy.close()
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}