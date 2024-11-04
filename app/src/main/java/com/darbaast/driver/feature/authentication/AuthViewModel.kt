package com.darbaast.driver.feature.authentication

import androidx.lifecycle.viewModelScope
import com.darbaast.driver.custom.DarbastViewModel
import com.darbaast.driver.custom.SingleLiveEvent
import com.darbaast.driver.data.model.AppVersion
import com.darbaast.driver.data.model.ResponseMessage
import com.darbaast.driver.data.model.User
import com.darbaast.driver.repository.ApiRepository
import com.darbaast.driver.server.retrofit.ApiService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val apiRepository: ApiRepository):DarbastViewModel() {

    val loginExceptionLiveData = SingleLiveEvent<Exception>()
    val loginMassageLiveData = SingleLiveEvent<ResponseMessage>()
    val sendCodeLiveData = SingleLiveEvent<ResponseMessage>()
    val appVersionLiveData = SingleLiveEvent<AppVersion>()
    val userLiveData = SingleLiveEvent<User>()


    fun login(jsonObject: JsonObject){
        progressBarLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loginMassageLiveData.postValue(apiRepository.login(jsonObject))
                progressBarLiveData.postValue(false)
            }catch (e:Exception){
                loginExceptionLiveData.postValue(e)
                progressBarLiveData.postValue(false)
            }
        }
    }

    fun sendCode(jsonObject: JsonObject){
        progressBarLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sendCodeLiveData.postValue(apiRepository.verificationCode(jsonObject))
                progressBarLiveData.postValue(false)
            }catch (e:HttpException){
                loginExceptionLiveData.postValue(e)
                progressBarLiveData.postValue(false)
            }
        }
    }

    fun authUser() {
        viewModelScope.launch(Dispatchers.IO){

            try {
                userLiveData.postValue(apiRepository.authUser())

            }catch (e:Exception){
                exceptionLiveData.postValue(e)
            }
        }
    }

    fun getVersion(){

        viewModelScope.launch(Dispatchers.IO){

            try {
                appVersionLiveData.postValue(apiRepository.getVersion())
            }catch (e:Exception){
                exceptionLiveData.postValue(e)
            }
        }

    }



}