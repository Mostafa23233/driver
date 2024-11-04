package com.darbaast.driver.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class Token(
    @PrimaryKey val id:Int,
    val token:String
)
