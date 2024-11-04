package com.darbaast.driver.data.model

data class ResponseMessage(
    val message: String,
    val token: String ?= null,
    val type: String
)