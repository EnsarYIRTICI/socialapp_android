package com.edison.mulaki.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.edison.mulaki.R
import com.edison.mulaki.Utils.RyanDahl
import com.squareup.picasso.Picasso
import org.json.JSONObject


class PrePostAdapter(private val dataList: List<JSONObject>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<PrePostAdapter.PrePostViewHolder>() {

    class PrePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prePostImage = itemView.findViewById<ImageView>(R.id.prePostImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrePostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_prepost, parent, false)
        return PrePostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrePostViewHolder, position: Int) {
        val currentItem = dataList[position]
        val uriStr = RyanDahl().URL_POST_MEDIA + currentItem.optString("uid") + "/" + currentItem.optString("view1")
        Picasso
            .get()
            .load(uriStr)
            .into(holder.prePostImage)
        holder.prePostImage.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}