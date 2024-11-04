package com.darbaast.driver.feature.home

import androidx.lifecycle.viewModelScope
import com.darbaast.driver.custom.DarbastViewModel
import com.darbaast.driver.custom.SingleLiveEvent
import com.darbaast.driver.data.model.CarouselData
import com.darbaast.driver.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val apiRepository: ApiRepository): DarbastViewModel() {

    val carouselLiveData = SingleLiveEvent<List<CarouselData>>()

    fun getCarouselData(){

        viewModelScope.launch(Dispatchers.IO){

            try {
                carouselLiveData.postValue(apiRepository.getCaruselData())
            }catch (e:Exception){
                exceptionLiveData.postValue(e)
            }

        }


    }

}