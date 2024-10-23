package com.edison.mulaki.Services

import com.edison.mulaki.Utils.RyanDahl
import com.edison.mulaki.Utils.TimBernersLee
import org.json.JSONObject

class RoomService {

    private val tim = TimBernersLee()

    fun fetchDisplays(AUTH_FID:String):String{
        return tim.httpPost(RyanDahl().API_ROOM_LIST, JSONObject().apply {
            put("fid", AUTH_FID)

        })

    }
}