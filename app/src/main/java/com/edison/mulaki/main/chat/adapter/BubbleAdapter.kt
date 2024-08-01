package com.edison.mulaki.main.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.edison.mulaki.R
import com.edison.mulaki.io.AndyRubin
import com.edison.mulaki.io.RyanDahl
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.InputStream
import java.net.URI
import java.net.URL


class BubbleAdapter(context: Context,
                    jsonArray: List<JSONObject>,
                    private val roomid:String,
                    private val uid:String
) :
    ArrayAdapter<JSONObject>(context, 0, jsonArray) {

    var onEndOfListReached: (() -> Unit)? = null
    companion object {
        const val BUBBLE_ON_LONG_CLICK = "BUBBLE_ON_LONG_CLICK"
        const val BUBBLE_IMAGE_ON_CLICK = "BUBBLE_IMAGE_ON_CLICK"
    }


    private fun showResult(result:String){
        Toast.makeText(context,result,Toast.LENGTH_LONG).show()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_bubble, parent, false)
//        var view = convertView
//        if (view == null) {
//            view = LayoutInflater.from(context).inflate(R.layout.adapter_bubble, parent, false)
//        }

        if (position == 0) { onEndOfListReached?.invoke() }

        val currentItem = getItem(position)

        try {
            if(currentItem != null)
            {
                //xml

                val bubbleContainer:LinearLayout = view!!.findViewById(R.id.bubbleContainer)
                val bubbleMessage:TextView = view.findViewById(R.id.bubbleMessage)
                val bubbleSendate:TextView = view.findViewById(R.id.bubbleSendate)
                val bubbleCard:CardView = view.findViewById(R.id.bubbleCard)

                //data

                val creation_date = currentItem.optString("creation_date").substring(11,16)
                val sender_uid = currentItem.optString("sender_uid")
                val message = currentItem.optString("message")
                val file: JSONObject? = currentItem.optJSONObject("file")

                //for onLongClick

                bubbleContainer.setOnLongClickListener {
                    val intent = Intent(BUBBLE_ON_LONG_CLICK)
                    intent.putExtra("position", position)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    true
                }

                //for date

                bubbleSendate.text = creation_date

                //for align

                if(uid == sender_uid) {
                    bubbleContainer.setBackgroundResource(R.drawable.shape_bubble_green)
                    bubbleCard.setBackgroundResource(R.drawable.shape_bubble_green)
                    val bclp = bubbleContainer.layoutParams as LinearLayout.LayoutParams
                    bclp.gravity = Gravity.END
                }else{
                    bubbleCard.setBackgroundResource(R.drawable.shape_bubble_purple)
                }

                //for message

                if(message != "null") {bubbleMessage.text = message }
                else { bubbleMessage.visibility = View.GONE }

                //for file

                if(file != null){

                    val topName =  file.get("top_name") as String
                    val fileName = file.get("file_name") as String
                    val mimetype = file.optString("mimetype")
                    bubbleContainer.setPadding(10)

                    val bubbleImageContainer:LinearLayout = view.findViewById(R.id.bubbleImageContainer)
                    val bubbleImage:ImageView = view.findViewById(R.id.bubbleImage)

                    if(AndyRubin().mimetype(mimetype) === "image") {

                        val uriStr = RyanDahl().URL_ROOM_MEDIA + roomid + "/" + topName + "/" + fileName
                        val id = currentItem.optString("_id")

                        //for onClick

                        bubbleImageContainer.setOnClickListener {
                            val intent = Intent(BUBBLE_IMAGE_ON_CLICK)
                            intent.putExtra("_id", id)
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                        }


                        //progress

                        val progressBar = ProgressBar(this.context)
                        val progressBarContainer = LinearLayout(this.context)
                        val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        progressBarContainer.layoutParams = params
                        progressBarContainer.layoutParams.height = 350
                        progressBarContainer.layoutParams.width = 350
                        progressBarContainer.gravity = Gravity.CENTER

                        progressBarContainer.addView(progressBar)
                        bubbleCard.addView(progressBarContainer)

                        //image

                        Picasso.get()
                            .load(uriStr)
                            .into(bubbleImage, object : Callback {
                                override fun onSuccess() {
                                    progressBarContainer.visibility = View.GONE


                                }
                                override fun onError(e: Exception) {
                                    progressBarContainer.visibility = View.GONE
                                    e.printStackTrace()

                                }

                            })


                    }else{

                        // for other file

                        bubbleImage.setImageResource(R.drawable.ic_draft_24px)

                        val fileNameTextView = TextView(this.context)
                        fileNameTextView.maxWidth = 250
                        fileNameTextView.text = fileName
                        fileNameTextView.setTextColor(ContextCompat.getColor(context, R.color.white))

                        bubbleImageContainer.addView(fileNameTextView)
                    }
                }

            }
        }catch (e:Exception){
            Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
        }



        return view!!
    }


}
