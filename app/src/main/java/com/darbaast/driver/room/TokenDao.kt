package com.darbaast.driver.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darbaast.driver.data.model.Token

@Dao
interface TokenDao {

@Query(value = "SELECT * FROM token")
suspend fun getAllToken():List<Token>

@Insert
suspend fun insertToken(token: Token)

@Query(value = "DELETE  FROM token")
suspend fun deleteAll()

}