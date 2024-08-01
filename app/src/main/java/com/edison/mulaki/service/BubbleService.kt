package com.edison.mulaki.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.edison.mulaki.io.AndyRubin
import com.edison.mulaki.io.RyanDahl
import com.edison.mulaki.io.TimBernersLee
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class BubbleService {
    private val tim = TimBernersLee()

    fun uploadFiles(_uploadProgress: MutableLiveData<String>, files:MutableList<File>, message:String?, uid:String, roomid:String):String{
        val fileInfoList = JSONArray()

        files.forEach { file ->

            val mimetype = AndyRubin().mimetypeByExtension(file.extension)

            val fileInfo = JSONObject().apply {
                put("file_name", file.name)
                put("file_size", file.length())
                put("file_type", mimetype)
            }
            fileInfoList.put(fileInfo)
        }


        return tim.uploadFiles(RyanDahl().API_BUBBLE_SEND_FILE, _uploadProgress, files, JSONObject().apply {
            put("message",message)
            put("sender_uid",uid)
            put("roomid",roomid)
            put("files", fileInfoList)

        })
    }

    fun downloadFileAndSave(_downloadProgress: MutableLiveData<String>, context: Context, fileUrl:String, fileName: String):String{
        return tim.downloadFileAndSave(_downloadProgress, context,fileUrl,fileName)

    }


    fun count(ID_ROOM: String):String{
        return tim.httpPost(RyanDahl().API_BUBBLE_COUNT,  JSONObject().apply {
            put("roomid",ID_ROOM)
        })

    }

    fun findAll(ID_ROOM:String,lim:Int,set:Int):String{
        return tim.httpPost(RyanDahl().API_BUBBLE_LIST, JSONObject().apply {
            put("roomid",ID_ROOM)
            put("lim",lim)
            put("set",set)
        })

    }

    fun send(message:String, sender_uid:String, ID_ROOM:String):String{
        return tim.httpPost(RyanDahl().API_BUBBLE_SEND, JSONObject().apply {
            put("message",message)
            put("sender_uid",sender_uid)
            put("roomid",ID_ROOM)
        })

    }
}