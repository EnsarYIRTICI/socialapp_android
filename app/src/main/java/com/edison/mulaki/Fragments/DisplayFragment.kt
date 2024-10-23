package com.edison.mulaki.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.edison.mulaki.Utils.Auth
import com.edison.mulaki.R
import com.edison.mulaki.ViewModels.DisplayViewModel

import com.edison.mulaki.databinding.FragmentDisplayBinding
import com.edison.mulaki.Adapters.DisplayAdapter

import org.json.JSONArray
import org.json.JSONObject

class DisplayFragment : Fragment() {

    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DisplayViewModel by viewModels()

    private val itemList = arrayListOf<JSONObject>()
    private var prevResult:String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displaySetup()
    }

    private fun displaySetup(){
        val ID_FID = Auth.authData.get("fid").toString()

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer{ result->
            if(result != prevResult){
                itemList.clear()
                createList(JSONArray(result.toString()))
            }
            prevResult = result
            createAdapter()
        })

        if(itemList.isEmpty()){
            viewModel.fetchDisplay(ID_FID)
        }

    }


    private fun createList(jsonArray:JSONArray){
        for(i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            itemList.add(jsonObject)
        }
    }

    private fun createAdapter() {
        val adapter = this.context?.let { DisplayAdapter(it,itemList) }
        binding.displayListView.adapter = adapter
        val bundle = Bundle()
        binding.displayListView.setOnItemClickListener(AdapterView.OnItemClickListener { _, _, position, _ ->
            bundle.putString("roomData",positionData(position))
            findNavController().navigate(R.id.action_display_to_chat, bundle)
        })
    }

    private fun positionData(position:Int):String{
        return binding.displayListView.getItemAtPosition(position).toString()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

