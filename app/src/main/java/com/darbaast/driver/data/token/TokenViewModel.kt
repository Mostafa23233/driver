package com.darbaast.driver.data.token

import androidx.lifecycle.viewModelScope
import com.darbaast.driver.custom.DarbastViewModel
import com.darbaast.driver.custom.SingleLiveEvent
import com.darbaast.driver.data.model.Token
import com.darbaast.driver.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(private val roomRepository: RoomRepository):
    DarbastViewModel() {

    val tokenLiveData = SingleLiveEvent<List<Token>>()


    fun getToken(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                tokenLiveData.postValue(roomRepository.getAllToken())

            }catch (e:Exception){
                exceptionLiveData.postValue(e)
            }

        }
    }

    fun deleteAllToken(){
        viewModelScope.launch(Dispatchers.IO){
            roomRepository.deleteAll()
        }
    }

    fun insertToken(token: String){

        viewModelScope.launch(Dispatchers.IO){
            val tokenObject = Token(1,token)
            roomRepository.saveToken(tokenObject)
        }
    }


}