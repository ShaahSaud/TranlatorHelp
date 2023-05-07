package com.gigafx.mytranslator.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gigafx.mytranslator.R
import com.gigafx.mytranslator.ScanActivity
import com.gigafx.mytranslator.TextTranslationActivity
import com.gigafx.mytranslator.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(layoutInflater,container,false)


        binding.apply {
            btnScan.setOnClickListener {
                requireContext().startActivity(Intent(requireContext(),ScanActivity::class.java))
            }

            btnTxtTranslate.setOnClickListener {
                requireContext().startActivity(Intent(requireContext(),TextTranslationActivity::class.java))
            }
        }


        return binding.root
    }

}