package com.edison.mulaki.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.edison.mulaki.R
import com.edison.mulaki.Utils.RyanDahl
import com.squareup.picasso.Picasso
import org.json.JSONObject
import kotlin.math.max
import kotlin.math.min

class ShowImageAdapter(private val dataList: List<JSONObject>, private val ID_ROOM:String, private val context: Context):
    RecyclerView.Adapter<ShowImageAdapter.ShowImageViewHolder>() {
    class ShowImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage = itemView.findViewById<ImageView>(R.id.showImage)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_chat_showimage, parent, false)
        return ShowImageViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ShowImageViewHolder, position: Int) {

        val currentItem = dataList[position]
        val file = currentItem.opt("file") as JSONObject
        val topName = file.get("top_name")
        val fileName = file.get("file_name")

        val uriStr = RyanDahl().URL_ROOM_MEDIA + ID_ROOM + "/" + topName + "/" + fileName
        Picasso
            .get()
            .load(uriStr)
            .into(holder.showImage)

        val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            var scaleFactor = 1.0f

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = max(0.1f, min(scaleFactor, 5.0f))

                holder.showImage.scaleX = scaleFactor
                holder.showImage.scaleY = scaleFactor

                return true
            }
        })

        holder.showImage.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}