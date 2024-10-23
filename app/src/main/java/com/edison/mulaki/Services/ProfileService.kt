package com.edison.mulaki.Services

import com.edison.mulaki.Utils.RyanDahl
import com.edison.mulaki.Utils.TimBernersLee
import org.json.JSONObject

class ProfileService {

    private val tim = TimBernersLee()

    fun fetchProfile (AUTH_UID:String,unameoruid:String,lim:Int,set:Int):String{
        return tim.httpPost(RyanDahl().API_USER_PROFILE, JSONObject().apply {
            put("myuid",AUTH_UID)
            put("unameoruid",unameoruid)
            put("lim",lim)
            put("set",set)
        })

    }
}