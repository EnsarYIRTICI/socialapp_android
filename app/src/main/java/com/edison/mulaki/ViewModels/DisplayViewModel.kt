package com.edison.mulaki.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.mulaki.Services.InboxService
import com.edison.mulaki.Services.RoomService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DisplayViewModel: ViewModel() {

    private val ds = InboxService()

    private val _resultLiveData = MutableLiveData<String>()
    val resultLiveData: LiveData<String> get() = _resultLiveData

    fun fetchDisplay(ID_F:String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    ds.fetchDisplays(ID_F)
                }
                _resultLiveData.value = result
            } catch (e: Exception) {
                _resultLiveData.value = "$e"
            }
        }
    }


}