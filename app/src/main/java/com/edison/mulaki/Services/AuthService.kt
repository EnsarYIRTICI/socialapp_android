package com.edison.mulaki.Services

import com.edison.mulaki.Utils.RyanDahl
import com.edison.mulaki.Utils.TimBernersLee
import org.json.JSONObject

class AuthService {

    private val tim = TimBernersLee()
    fun signInWithUsernameAndPassword(username: String, password: String): String {
        return tim.httpPost(RyanDahl().API_AUTH_LOGIN, JSONObject().apply {
            put("username",username)
            put("password",password)
        })
    }

    fun signUp(displayname:String,username:String, email:String, password:String):String{
        return tim.httpPost(RyanDahl().API_AUTH_REGISTER, JSONObject().apply {
            put("displayname",displayname)
            put("username",username)
            put("email",email)
            put("password",password)
        })
    }
}