package com.darbaast.driver.socket

import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javax.inject.Inject

class SocketManager @Inject constructor(
    private val socket:Socket,
    private val darbastSharedPreference: DarbastSharedPreference
) {
    fun connect()
    {
        if (!isConnected()&& darbastSharedPreference.getPhone().isNotEmpty())
        socket.connect()
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
    }
    private val onConnectError = Emitter.Listener {
        try {
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun disconnect(){
        if (socket.connected()){
            socket.disconnect()
        }
    }

    fun isConnected():Boolean{
        return socket.connected()
    }
}