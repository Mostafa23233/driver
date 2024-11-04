package com.darbaast.driver.socket

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.darbaast.driver.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SocketService @Inject constructor() : Service() {

    @Inject
    lateinit var socketManager: SocketManager

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannel()
            socketManager.connect()
            startForeground(1, socketNotification())
        }catch (e:Exception){
            Log.i("LOOOG", "onCreate: "+e)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channelId = "darbast_channel"
                val channelName = "Darbast Service Channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)

                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun socketNotification(): Notification? {
        try {
            return NotificationCompat.Builder(this, "darbast_channel")
                .setContentTitle("سرویس راننده در حال اجراست")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }


    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}