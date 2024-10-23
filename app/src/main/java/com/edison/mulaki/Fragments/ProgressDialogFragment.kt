package com.edison.mulaki.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.edison.mulaki.R

class ProgressDialogFragment : DialogFragment() {

    var progressLiveData: MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_progress, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressText = view.findViewById<TextView>(R.id.progressText)

        progressLiveData.observe(viewLifecycleOwner, Observer { progress ->
            progressText.text = progress
        })
    }

}


