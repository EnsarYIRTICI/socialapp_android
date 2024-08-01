package com.edison.mulaki.main.display

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.edison.mulaki.R
import com.edison.mulaki.io.RyanDahl
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DisplayAdapter (context: Context,
                      jsonArray: List<JSONObject>
) :
    ArrayAdapter<JSONObject>(context, 0, jsonArray) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_display, parent, false)
        }

        val currentItem = getItem(position)

        if (currentItem != null) {
            val username: TextView = view!!.findViewById(R.id.displayUsername)
            val displaymessage: TextView = view.findViewById(R.id.displayDisplaymessage)

            username.text = currentItem.optString("username")
            displaymessage.text = currentItem.optString("displaymessage")

            val imageName = currentItem.optString("image")
            val ID_UID = currentItem.optString("uid")

            if (imageName != "null") {
                val displayImage: ImageView = view.findViewById(R.id.displayImage)

                val location = ID_UID + "/" + imageName
                val photoPath = RyanDahl().URL_USER_MEDIA + location

                Picasso
                    .get()
                    .load(photoPath)
                    .into(displayImage);

            }

        }

        return view!!

    }

}