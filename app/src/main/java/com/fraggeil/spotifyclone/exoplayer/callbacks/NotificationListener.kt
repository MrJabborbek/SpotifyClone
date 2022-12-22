package com.fraggeil.spotifyclone.exoplayer.callbacks

import android.app.Notification
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.fraggeil.spotifyclone.exoplayer.MusicService
import com.fraggeil.spotifyclone.other.Constants.NOTIFICATION_CHANNEL_ID
import com.fraggeil.spotifyclone.other.Constants.NOTIFICATION_ID
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class NotificationListener(
    val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            isForegroundService = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }else{
                stopForeground(true)
            }
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if (ongoing && !isForegroundService){

                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                isForegroundService = true
                startForeground(NOTIFICATION_ID, notification)
            }
        }
    }
}