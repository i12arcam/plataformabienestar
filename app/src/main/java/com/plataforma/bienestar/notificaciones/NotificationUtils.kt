package com.plataforma.bienestar.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationUtils {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "daily_channel_id",
                "Recordatorios Diarios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "No olvides completar tu rutina diaria"
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}