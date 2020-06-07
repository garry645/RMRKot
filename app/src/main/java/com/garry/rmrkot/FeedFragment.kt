package com.garry.rmrkot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.garry.rmrkot.databinding.FragmentFeedBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class FeedFragment: Fragment() {

    private lateinit var binding: FragmentFeedBinding
    lateinit var menu: BottomNavigationView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menu = (activity as MainActivity).menu!!
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed, container, false
        )
        (activity as MainActivity).menu?.visibility = View.VISIBLE
        //menu.setOnNavigationItemSelectedListener = navListner

        return binding.root
    }
}


