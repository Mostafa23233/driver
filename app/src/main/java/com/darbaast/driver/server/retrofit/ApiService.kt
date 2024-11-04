package com.darbaast.driver.server.retrofit

import com.darbaast.driver.data.model.AppVersion
import com.darbaast.driver.data.model.CarouselData
import com.darbaast.driver.data.model.ResponseMessage
import com.darbaast.driver.data.model.User
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body jsonObject: JsonObject): ResponseMessage

    @POST("login/verification")
    suspend fun verificationCode(@Body jsonObject: JsonObject): ResponseMessage

    @POST("login/complete")
    suspend fun completeProfile(@Body jsonObject: JsonObject): ResponseMessage

    @GET("data/carousel")
    suspend fun getCaruselData():List<CarouselData>

    @GET("auth/user")
    suspend fun authUser(): User

    @GET("data/get-last-app-version")
    suspend fun getVersion(): AppVersion















}