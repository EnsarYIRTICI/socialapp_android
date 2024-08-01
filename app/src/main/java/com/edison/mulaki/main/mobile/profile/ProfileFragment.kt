package com.edison.mulaki.main.mobile.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.edison.mulaki.AuthActivity
import com.edison.mulaki.R
import com.edison.mulaki.auth.Auth
import com.edison.mulaki.databinding.FragmentProfileBinding
import com.edison.mulaki.main.mobile.profile.adapter.PrePostAdapter
import org.json.JSONArray
import org.json.JSONObject


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private val lim:Int=20
    private val set:Int=0
    private val AUTH_UID = Auth.authData.get("uid").toString()
    private val username = Auth.authData.get("username").toString()

    private val itemList = arrayListOf<JSONObject>()
    private var prevResult:String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        val root:View = binding.root
        profileViewSetup()
        return root
    }

    private fun profileViewSetup(){

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer { result->
            val jsonObject = JSONObject(result.toString())
            val postList = jsonObject.getString("postList")
            if(result != prevResult) createList(JSONArray(postList))
            prevResult = result
            createAdapter()
            val userView = jsonObject.get("userView") as JSONObject
            binding.title.text = userView.getString("username")
            binding.username.text = userView.getString("username")
            binding.displayname.text = userView.getString("displayname")
            binding.posts.text = userView.getString("posts_count")
            binding.followers.text = userView.getString("followers_count")
            binding.follows.text = userView.getString("follows_count")

        })

        if(itemList.isEmpty()){
            viewModel.fetchProfile(AUTH_UID,username,lim,set)
        }

        binding.profileImage.setOnClickListener {
            Auth.logout()
            navigateAuthActivity()
        }
    }

    private fun createAdapter(){
        val adapter = PrePostAdapter(itemList) {position->
            val item = binding.postRecyclerView.getChildAt(position)
            val bundle = Bundle()
            bundle.putFloat("x",item.left.toFloat())
            bundle.putFloat("y",item.bottom.toFloat())
            findNavController().navigate(R.id.action_profile_to_post, bundle)
        }

        binding.postRecyclerView.layoutManager = GridLayoutManager(this.context, 3)
        binding.postRecyclerView.adapter = adapter

    }

    private fun createList(arrayList:JSONArray){
        for (i in 0 until arrayList.length()){
            val jsonObject = arrayList.getJSONObject(i)
            itemList.add(jsonObject)
        }

    }

    private fun navigateAuthActivity(){
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}