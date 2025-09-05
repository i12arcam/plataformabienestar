package com.plataforma.bienestar.notificaciones

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler {

    companion object {

        // METODO PARA LAS 6 PM (18:00) CON ID DE USUARIO
        fun scheduleAtSixPM(context: Context, userId: String) {
            WorkManager.getInstance(context).cancelAllWorkByTag(DailyNotificationWorker.WORK_TAG)

            val now = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 18) // 6 PM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Si ya pasaron las 6 PM hoy, programar para mañana
            if (targetTime.before(now)) {
                targetTime.add(Calendar.DAY_OF_MONTH, 1)
            }

            val delayMillis = targetTime.timeInMillis - now.timeInMillis
            val delayHours = TimeUnit.MILLISECONDS.toHours(delayMillis)

            // ✅ Crear Data con el ID del usuario
            val inputData = Data.Builder()
                .putString("user_id", userId)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
                24, TimeUnit.HOURS,
                15, TimeUnit.MINUTES
            )
                .addTag(DailyNotificationWorker.WORK_TAG)
                .setInitialDelay(delayHours, TimeUnit.HOURS)
                .setInputData(inputData) // ✅ Pasar los datos
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DailyNotificationWorker.WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyWorkRequest
            )

            Log.d("NotificationScheduler", "Notificación programada para usuario: $userId")
        }

        fun cancelDailyNotifications(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(DailyNotificationWorker.WORK_TAG)
            Log.d("NotificationScheduler", "Notificaciones canceladas")
        }
    }
}