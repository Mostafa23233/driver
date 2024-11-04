package com.darbaast.driver.di

import android.content.Context
import androidx.room.Room
import com.darbaast.driver.room.AppDatabase
import com.darbaast.driver.room.TokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModules {
    @Provides
    @Singleton
    fun providesAppDataBase(@ApplicationContext context: Context): AppDatabase {

        return Room.databaseBuilder(context, AppDatabase::class.java,"db_darbast_driver")
            .fallbackToDestructiveMigration()
            .build()

    }
    @Provides
    @Singleton
    fun providesTokenDao(appDataBase: AppDatabase): TokenDao = appDataBase.tokenDao

}