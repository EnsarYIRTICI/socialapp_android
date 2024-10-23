package com.edison.mulaki.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.edison.mulaki.Adapters.PostAdapter
import com.edison.mulaki.Utils.Auth
import com.edison.mulaki.databinding.FragmentHomeBinding
import com.edison.mulaki.ViewModels.HomeViewModel
import org.json.JSONArray
import org.json.JSONObject


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val itemList = arrayListOf<JSONObject>()
    private var prevResult:String? = null

    private val lim:Int = 10
    private var prevListCount:Int = 0
    private var count:Int = 0

    private var _screenWidth: Int? = null
    private val screenWidth get() = _screenWidth!!

    private var ID_U:String = Auth.authData.getString("uid")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root:View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeSetup()
    }

    private fun homeSetup() {
        _screenWidth = requireActivity().resources.displayMetrics.widthPixels

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer{ result->
            if(result != prevResult){
                createList(JSONArray(result.toString()))
            }
            prevResult = result.toString()
            createAdapter()
        })

        viewModel.warn.observe(viewLifecycleOwner, Observer { result->
            showResult(result.toString())
        })

        viewModel.count.observe(viewLifecycleOwner, Observer{ result->
            count = result.toInt()
        })

        if(itemList.isEmpty()){
            viewModel.fetchPosts(ID_U, lim, 0)
            viewModel.fetchPostsCount(ID_U)
        }

    }

    private fun createList(jsonArray:JSONArray){
        for(i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            itemList.add(jsonObject)
        }
    }

    private fun createAdapter() {
        val adapter = this.context?.let { PostAdapter(it, itemList, screenWidth) }
        binding.postListView.adapter = adapter
        scrollFixer()
        adapter?.onEndOfListReached={
            if(itemList.count() < count) {
                viewModel.fetchPostsMore(ID_U, lim, itemList.count())
            }
        }
    }

//    private fun createAdapter() {
//        val adapter = PostAdapter(itemList, screenWidth)
//        binding.postRecyclerView.layoutManager = LinearLayoutManager(this.context)
//        binding.postRecyclerView.adapter = adapter
//
//    }

    private fun scrollFixer(){
        if(itemList.count() != prevListCount){
            binding.postListView.setSelection(prevListCount)
            prevListCount = itemList.count()
        }
    }

    private fun showResult(result:String){
        Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


