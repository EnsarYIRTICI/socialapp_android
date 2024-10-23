package com.edison.mulaki.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.edison.mulaki.databinding.FragmentShowpostBinding

class ShowPostFragment: Fragment() {

    private var _binging: FragmentShowpostBinding? = null
    private val binding get() = _binging!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN
//        flags =
//            flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//
//        requireActivity().window.decorView.systemUiVisibility = flags

        _binging = FragmentShowpostBinding.inflate(inflater, container,false)
        val root:View = binding.root
        val x = arguments?.getFloat("x")
        val y = arguments?.getFloat("y")
        applyPageAnimation(root,x!!,y!!)
        return root
    }

    private fun applyPageAnimation(view: View,x:Float,y:Float) {
        view.pivotX = x
        view.pivotY = y

        view.scaleX = 0f
        view.scaleY = 0f

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }


}

