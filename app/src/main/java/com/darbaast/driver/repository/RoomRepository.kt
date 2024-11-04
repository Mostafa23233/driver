package com.darbaast.driver.repository

import com.darbaast.driver.data.model.Token
import com.darbaast.driver.room.TokenDao
import javax.inject.Inject


class RoomRepository @Inject constructor(private val tokenDao: TokenDao){
   suspend fun saveToken(token: Token){
       return tokenDao.insertToken(token)
    }
    suspend fun getAllToken():List<Token>{
        return tokenDao.getAllToken()
   }
    suspend fun deleteAll(){
        return tokenDao.deleteAll()
    }
}