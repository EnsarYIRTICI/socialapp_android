package com.edison.mulaki.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.mulaki.Services.HomeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val hs = HomeService()

    private val _resultLiveData = MutableLiveData<String>()
    val resultLiveData: LiveData<String> get() = _resultLiveData

    private val _count = MutableLiveData<String>()
    val count: LiveData<String> get() = _count

    private val _warn = MutableLiveData<String>()
    val warn: LiveData<String> get() = _warn


    fun fetchPostsCount(uid:String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    hs.fetchPostsCount(uid)
                }
                _count.value = result
            } catch (e: Exception) {
                _warn.value = "Posts count could not be loaded\n$e"
            }
        }
    }


    fun fetchPosts(uid:String, lim:Int, set:Int){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    hs.fetchPosts(uid, lim, set)
                }
                _resultLiveData.value = result
            } catch (e: Exception) {
                _warn.value = "Posts could not be loaded\n$e"
            }
        }
    }

    fun fetchPostsMore(uid:String, lim:Int, set:Int){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    hs.fetchPostsMore(uid, lim, set)
                }
                _resultLiveData.value = result
            } catch (e: Exception) {
                _warn.value = "Could not load more posts\n$e"
            }
        }
    }




}