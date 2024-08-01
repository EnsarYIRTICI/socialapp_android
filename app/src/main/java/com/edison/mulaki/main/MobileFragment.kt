package com.edison.mulaki.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.edison.mulaki.R
import com.edison.mulaki.databinding.FragmentMobileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MobileFragment : Fragment() {

    private var _binding: FragmentMobileBinding? = null
    private val binding get() = _binding!!

    private val defaultLocation = R.id.navigation_home
    private var location:Int = defaultLocation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMobileBinding.inflate(inflater,container,false)
        val root:View = binding.root
        return root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view.findViewById(R.id.nav_host_mobile_navigation))
        val botNavView: BottomNavigationView = binding.navView

//        botNavView.setupWithNavController(navController)

//        view.isFocusableInTouchMode = true
//        view.requestFocus()
//        view.setOnKeyListener { _, keyCode, _ ->
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                true
//            } else {
//                false
//            }
//        }


        botNavView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_home -> {
                    if(location != item.itemId){
                        navController.navigate(R.id.navigation_home)
                        location = item.itemId
                        true
                    } else{ false }
                }
                R.id.navigation_search -> {
                    println("Search")
                    false
                }
                R.id.show_create_post -> {
                    println("Create Post")
                    false
                }
                R.id.nav_graph_display -> {
                    findNavController().navigate(R.id.action_mobile_to_display)
                    false
                }
                R.id.nav_graph_profile -> {
                    if(location != item.itemId){
                        navController.navigate(R.id.nav_graph_profile)
                        location =  item.itemId
                        true
                    } else{ false }
                }
                else -> false
            }
        }


    }




}