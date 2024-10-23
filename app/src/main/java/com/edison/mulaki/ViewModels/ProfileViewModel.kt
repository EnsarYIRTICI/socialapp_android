package com.edison.mulaki.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.mulaki.Services.ProfileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel:ViewModel() {

    private val ps = ProfileService()

    private val _resultLiveData = MutableLiveData<String>()
    val resultLiveData get() = _resultLiveData


    fun fetchProfile(AUTH_ID_USER:String, unameoruid:String, lim:Int, set:Int){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    ps.fetchProfile(AUTH_ID_USER, unameoruid, lim, set)
                }
                _resultLiveData.value = result
            }catch (e:Exception){
                _resultLiveData.value = "$e"

            }
        }

    }



}