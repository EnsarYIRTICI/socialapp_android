package com.edison.mulaki.io

import java.util.UUID

class AndreyBreslav {

    fun generateRandomKey():String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    fun extname(fileName: String):String {
        val index = fileName.lastIndexOf('.')
        if (index > 0) { return fileName.substring(index) }
        return ""
    }

    fun emptyFilter(message:String):Boolean{
        for (i in message){
            if(i == ' ') { continue }
            else{ return true }
        }
        return false
    }



}