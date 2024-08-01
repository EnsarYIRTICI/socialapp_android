package com.edison.mulaki.main.chat.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.edison.mulaki.R
import com.edison.mulaki.io.AndyRubin
import com.edison.mulaki.main.chat.adapter.ShowImageAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray
import org.json.JSONObject

class GallerySheet: BottomSheetDialogFragment() {

    companion object {
        const val KEY_JSON_STRING = "result"
        const val KEY_ID_ROOM = "roomid"
        const val KEY_ID_BUBBLE = "_id"
        fun instance(result: String, ID_ROOM:String, ID_BUBBLE:String): GallerySheet {
            val fragment = GallerySheet()
            val args = Bundle()
            args.putString(KEY_JSON_STRING, result)
            args.putString(KEY_ID_ROOM, ID_ROOM)
            args.putString(KEY_ID_BUBBLE, ID_BUBBLE)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottomsheet_chat_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset < 0.5) {
                    dismiss()
                }
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })

        val jsonString = arguments?.getString(KEY_JSON_STRING)
        val ID_ROOM = arguments?.getString(KEY_ID_ROOM)
        val ID_BUBBLE = arguments?.getString(KEY_ID_BUBBLE)
        val jsonArray = JSONArray(jsonString)

        val itemList = arrayListOf<JSONObject>()

        for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)
            itemList.add(jsonObject)
        }

        val imageList = itemList.filter {item->
            try {
                val file = item.get("file") as JSONObject
                val mimetype = file.getString("mimetype")
                AndyRubin().mimetype(mimetype) == "image"
            }catch (e:Exception){
                false
            }
        }

        val showImageRecyclerView = view.findViewById<RecyclerView>(R.id.showImageRecyclerView)

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView( showImageRecyclerView)

        val adapter = ShowImageAdapter(imageList, ID_ROOM!!, requireContext())
        showImageRecyclerView.setAdapter(adapter)

        val scrollPosition  = findIndex(imageList, KEY_ID_BUBBLE, ID_BUBBLE!!)
        showImageRecyclerView.scrollToPosition(scrollPosition);
    }

    fun findIndex(list: List<JSONObject>, key: String, targetValue: Any): Int {
        for ((index, jsonObject) in list.withIndex()) {
            if (jsonObject.has(key) && jsonObject[key] == targetValue) {
                return index
            }
        }
        return -1
    }



}