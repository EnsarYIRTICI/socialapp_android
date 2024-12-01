package com.edison.mulaki.Services

import com.edison.mulaki.Utils.RyanDahl
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class RoomService {

    private var _inboxSocket: Socket? = null
    private val inboxSocket get() = _inboxSocket!!

    var roomId:String? = null
    var userId:String? = null

    fun initialize(roomId: String, userId:String) {
        this.roomId = roomId
        this.userId = userId
        
        connect()
        join()
    }

    private fun connect(){
        val opts = IO.Options()
        opts.path = "/socket/inbox"

        _inboxSocket = IO.socket(RyanDahl().SOCKET_URL, opts)
        inboxSocket.connect()
    }

    private fun join(){
        inboxSocket.emit("join-room",roomId);
    }

    fun get(onGet:Emitter.Listener){
        inboxSocket.on("on-message", onGet)
    }

    fun send(message:String){
        inboxSocket.emit("message", JSONObject().apply {
            put("roomid",roomId)
            put("sender_uid", userId)
            put("message", message)

        })
    }

    fun disconnect(){
        inboxSocket.disconnect()
    }
}