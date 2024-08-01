package com.edison.mulaki.service

import com.edison.mulaki.io.RyanDahl
import com.edison.mulaki.io.TimBernersLee
import org.json.JSONObject

class RoomService {

    private val tim = TimBernersLee()

    fun fetchDisplays(AUTH_FID:String):String{
        return tim.httpPost(RyanDahl().API_ROOM_LIST, JSONObject().apply {
            put("fid", AUTH_FID)

        })

    }
}