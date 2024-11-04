package com.darbaast.driver.repository

import com.darbaast.driver.data.model.AppVersion
import com.darbaast.driver.data.model.CarouselData
import com.darbaast.driver.data.model.ResponseMessage
import com.darbaast.driver.data.model.User
import com.darbaast.driver.server.retrofit.ApiService
import com.google.gson.JsonObject
import retrofit2.http.Body
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(jsonObject: JsonObject): ResponseMessage{
        return apiService.login(jsonObject)
    }

    suspend fun verificationCode(jsonObject: JsonObject): ResponseMessage{
        return apiService.verificationCode(jsonObject)
    }

    suspend fun completeProfile(jsonObject: JsonObject): ResponseMessage{
        return apiService.completeProfile(jsonObject)
    }
    suspend fun getCaruselData():List<CarouselData>{
        return apiService.getCaruselData()
    }

    suspend fun authUser(): User {
        return apiService.authUser()
    }

    suspend fun getVersion(): AppVersion {

        return apiService.getVersion()
    }



}
