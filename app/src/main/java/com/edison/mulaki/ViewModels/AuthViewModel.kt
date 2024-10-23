package com.edison.mulaki.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.mulaki.Services.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class AuthViewModel : ViewModel() {

    private val ats = AuthService()


    private val _resultLiveData = MutableLiveData<String>()
    val resultLiveData: LiveData<String> get() = _resultLiveData

    private val _registerData = MutableLiveData<String>()
    val registerData: LiveData<String> get() = _registerData

    private val _connection = MutableLiveData<String>()
    val connection: LiveData<String> get() = _connection

    private val _warn = MutableLiveData<String>()
    val warn: LiveData<String> get() = _warn

    fun signInWithUsernameAndPassword(username: String, password: String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    ats.signInWithUsernameAndPassword(username, password)
                }
                _resultLiveData.value = result
            } catch (e: IOException) {
                _connection.value = e.message
            } catch (e: Exception) {
                val jsonData = JSONObject(e.message.toString())
                _warn.value = jsonData.getString("error")
            }
        }
    }

    fun signUp(displayname:String, username:String, email:String, password:String){
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO){
                    ats.signUp(displayname,username,email,password)
                }
                _registerData.value = result
            } catch (e: IOException) {
                _warn.value = e.message
            } catch (e:Exception) {
                val jsonData = JSONObject(e.message.toString())
                _warn.value = jsonData.getString("error")
            }
        }
    }

}
