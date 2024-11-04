package com.darbaast.driver.di

import com.darbaast.driver.custom.annotation.SocketTimeOut
import com.darbaast.driver.custom.annotation.SocketUrl
import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import com.darbaast.driver.socket.SocketManager
import com.darbaast.driver.socket.SocketService
import com.darbaast.driver.utils.Utils.Companion.darbastLog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SocketModules {


    @Provides
    @Singleton
    fun provideSocket(@SocketUrl url:String , @SocketTimeOut timeOut: Int):Socket {
        var socket = IO.socket(url)
        val option = IO.Options.builder()
            .setPath("/darbaastIO/")
            .build()
        val uri = URI.create(url)
        val toURL = uri.toURL()
        toURL.openConnection().connectTimeout = timeOut
            socket = IO.socket(uri,option)
        darbastLog(socket)
        return socket
    }


    @Provides
    @SocketUrl
    fun provideSocketUrl():String{
        return "https://socket.web-tool.cloud:2096/driver"
    }
    @Provides
    @SocketTimeOut
    fun provideSocketTimeOut():Int{
        return 60000
    }

    @Provides
    @Singleton
    fun provideSocketManager(socket: Socket, @SocketTimeOut timeOut:Int, darbastSharedPreference: DarbastSharedPreference) = SocketManager(socket,darbastSharedPreference)

    @Provides
    @Singleton
    fun provideSocketService(): SocketService {
        return SocketService()
    }
}