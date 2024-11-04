package com.darbaast.driver.custom

import androidx.lifecycle.ViewModel

open class DarbastViewModel:ViewModel() {

    val exceptionLiveData = SingleLiveEvent<Exception>()
    val progressBarLiveData = SingleLiveEvent<Boolean>()

}