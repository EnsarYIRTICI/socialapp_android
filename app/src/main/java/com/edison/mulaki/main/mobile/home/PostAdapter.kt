package com.edison.mulaki.main.mobile.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.edison.mulaki.R
import com.squareup.picasso.Picasso
import org.json.JSONObject

import com.edison.mulaki.io.RyanDahl
import com.edison.mulaki.objects.PostData
import com.squareup.picasso.Callback

class PostAdapter(context: Context,
                  private val jsonArray: List<JSONObject>,
                  private val screenWidth: Int
) :
    ArrayAdapter<JSONObject>(context, 0, jsonArray) {

    var onEndOfListReached: (() -> Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_post, parent, false)
        }

        if (position == jsonArray.count()-1) {
            onEndOfListReached?.invoke()
        }

        val currentItem = getItem(position)
        if(currentItem != null)
        {
            val postData = PostData.fromJson(currentItem)

            //Post Image

            val imageView:ImageView = view!!.findViewById(R.id.postImage)
            imageView.layoutParams.width = screenWidth
            imageView.layoutParams.height = screenWidth
            val uriStr = RyanDahl().URL_POST_MEDIA + postData.sender_uid + "/" + currentItem.optString("view1")

            val progressBar: ProgressBar = view.findViewById(R.id.postProgressBar)
            progressBar.visibility = View.VISIBLE

            Picasso.get()
                .load(uriStr)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        imageView
                    }
                    override fun onError(e: Exception) {
                        progressBar.visibility = View.GONE
                        e.printStackTrace()
                    }
                })

            //Profile Username

            val postUsernameView: TextView = view.findViewById(R.id.postUsername)
            postUsernameView.text = currentItem.optString("username")

            //Profile Image

            val image = postData.image

            if(image != "null")
            {
                val userUriStr = RyanDahl().URL_USER_MEDIA + postData.sender_uid + "/" + image

                val userImageView: ImageView = view.findViewById(R.id.postUserImage)
                val userImageMiniView: ImageView = view.findViewById(R.id.postUserImageMini)

                Picasso.get()
                    .load(userUriStr)
                    .into(userImageView)

                Picasso.get()
                    .load(userUriStr)
                    .into(userImageMiniView)
            }
        }

        return view!!
    }
}



//class PostAdapter(private val postList: List<JSONObject>, private val screenWidth:Int) :
//    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
//
//
//    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun loadPicassoImage(url: String, target: ImageView) {
//            Picasso.get().load(url).into(target)
//        }
//
//        fun bindItems(post:JSONObject, screenWidth:Int) {
//            val postUserImage: ImageView = itemView.findViewById(R.id.postUserImage)
//            val postUserImageMini: ImageView = itemView.findViewById(R.id.postUserImageMini)
//            val postUsername: TextView = itemView.findViewById(R.id.postUsername)
//            val postUsernameMini: TextView = itemView.findViewById(R.id.postUsernameMini)
//            val postImage: ImageView = itemView.findViewById(R.id.postImage)
//            val postProgressBar: ProgressBar = itemView.findViewById(R.id.postProgressBar)
//
//            postUsername.text = post.getString("username")
//            postUsernameMini.text = post.getString("username")
//            postImage.layoutParams.width = screenWidth
//            postImage.layoutParams.height = screenWidth
//
//            val uid = post.getString("uid")
//
//            val image = post.getString("image")
//            val userUriStr = RyanDahl().URL_USER_MEDIA + uid +  "/" + image
//
//            if(image != "null"){
//                loadPicassoImage(userUriStr,postUserImage)
//                loadPicassoImage(userUriStr,postUserImageMini)
//            }
//
//            val postUriStr = RyanDahl().URL_POST_MEDIA + uid + "/" + post.getString("view1")
//
//            Picasso.get()
//                .load(postUriStr)
//                .into(postImage, object : Callback {
//                    override fun onSuccess() {
//                        postProgressBar.visibility = View.GONE
//                    }
//                    override fun onError(e: Exception) {
//                        postProgressBar.visibility = View.GONE
//                        e.printStackTrace()
//                    }
//                })
//
//
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.adapter_post, parent, false)
//        return PostViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//      holder.bindItems(postList[position],screenWidth)
//    }
//
//    override fun getItemCount(): Int {
//        return postList.size
//    }
//}

