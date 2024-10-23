package com.edison.mulaki.Services

import com.edison.mulaki.Utils.RyanDahl
import com.edison.mulaki.Utils.TimBernersLee
import org.json.JSONObject

class HomeService {

    private val tim = TimBernersLee()
    fun fetchPostsCount(uid:String):String{
        return tim.httpPost(RyanDahl().API_POST_COUNT, JSONObject().apply {
            put("uid",uid)
        })
    }

    fun fetchPosts(uid:String, lim:Int, set:Int):String{
        return tim.httpPost(RyanDahl().API_POST_LIST, JSONObject().apply {
            put("uid",uid)
            put("lim",lim)
            put("set",set)
        })
    }

    fun fetchPostsMore(uid:String, lim:Int, set:Int):String{
        return tim.httpPost(RyanDahl().API_POST_MORE, JSONObject().apply {
            put("uid",uid)
            put("lim",lim)
            put("set",set)
        })
    }

}