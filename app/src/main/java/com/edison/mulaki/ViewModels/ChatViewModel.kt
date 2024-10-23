package com.edison.mulaki.ViewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.mulaki.Services.BubbleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class ChatViewModel: ViewModel() {

    private val rs = BubbleService()


    private val _resultLiveData = MutableLiveData<String>()
    val resultLiveData: LiveData<String> get() = _resultLiveData

    private val _count = MutableLiveData<String>()
    val count: LiveData<String> get() = _count

    private val _bubble = MutableLiveData<String>()
    val bubble: LiveData<String> get() = _bubble

    private val _file = MutableLiveData<String>()
    val file: LiveData<String> get() = _file

    private val _progress = MutableLiveData<String>()
    val progress: LiveData<String> get() = _progress

    private val _warn = MutableLiveData<String>()
    val warn: LiveData<String> get() = _warn



    fun uploadFiles(files: MutableList<File>, message:String?, ID_F:String, ID_ROOM:String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    rs.uploadFiles(_file, files, message, ID_F, ID_ROOM)
                }
                _file.value = result
            } catch (e: IOException) {
                _warn.value = e.message
            } catch (e: Exception) {
                _warn.value = "File could not be uploaded\n${e.message}"
            }
        }
    }

    fun downloadFileAndSave(context: Context,fileUrl:String, fileName: String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    rs.downloadFileAndSave(_progress, context,fileUrl,fileName)
                }
                _progress.value = result
                _warn.value = result
            } catch (e: IOException) {
                _warn.value = e.message
                _progress.value = e.message
            } catch (e:Exception){
                _warn.value = "File could not be downloaded\n${e.message}"
                _progress.value = e.message
            }
        }

    }

    fun send(message:String, sender_uid:String, ID_ROOM:String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    rs.send(message, sender_uid, ID_ROOM)
                }
                _bubble.value = result
            } catch (e: IOException) {
                _warn.value = e.message
            } catch (e: Exception) {
                _warn.value  = "Bubble could not be sent\n${e.message}"
            }
        }
    }

    fun count(ID_ROOM:String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    rs.count(ID_ROOM)
                }
                _count.value = result
            } catch (e: IOException) {
                _warn.value = e.message
            } catch (e: Exception){
                _warn.value = "Bubbles count could not be loaded\n${e.message}"
            }
        }
    }

    fun findAll(ID_ROOM:String,lim:Int,set:Int) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    rs.findAll(ID_ROOM, lim, set)
                }
                _resultLiveData.value = result
            } catch (e: IOException) {
                _warn.value = e.message
            } catch (e: Exception){
                _warn.value = "Bubbles could not be loaded\n${e.message}"
            }
        }
    }


}