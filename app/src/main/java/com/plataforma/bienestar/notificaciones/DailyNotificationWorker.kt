package com.plataforma.bienestar.notificaciones

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.plataforma.bienestar.R
import com.plataforma.bienestar.data.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyNotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // ✅ OBTENER USER_ID DESDE LOS PARÁMETROS
            val userId = inputData.getString("user_id") ?: ""

            if (userId.isNotEmpty()) {
                // VERIFICAR SI YA SE REGISTRÓ HOY
                val yaRegistrado = withContext(Dispatchers.IO) {
                    checkIfAlreadyRegisteredToday(userId)
                }

                if (!yaRegistrado) {
                    showDailyNotification()
                    Log.d("DailyNotification", "Notificación mostrada - Usuario $userId NO ha registrado hoy")
                } else {
                    Log.d("DailyNotification", "Notificación omitida - Usuario $userId YA registró hoy")
                }
            } else {
                Log.e("DailyNotification", "No se proporcionó user_id")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyNotification", "Error: ${e.message}")
            Result.failure()
        }
    }

    // ✅ RECIBIR USER_ID COMO PARÁMETRO
    private suspend fun checkIfAlreadyRegisteredToday(userId: String): Boolean {
        return try {
            val emociones = ApiClient.apiService.getAllEmociones(userId)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaHoy = dateFormat.format(Calendar.getInstance().time)

            emociones.any { emocion ->
                emocion.fechaCreacion?.let { fecha ->
                    dateFormat.format(fecha) == fechaHoy
                } ?: false
            }
        } catch (e: Exception) {
            Log.e("DailyNotification", "Error al verificar registro: ${e.message}")
            false // Si hay error, mejor mostrar notificación por si acaso
        }
    }

    private fun showDailyNotification() {
        try {
            val notification = NotificationCompat.Builder(applicationContext, "daily_channel_id")
                .setSmallIcon(R.drawable.mindauralogo)
                .setContentTitle("¿Cómo ha ido tu día?")
                .setContentText("Es un buen momento para reflexionar sobre tu día y registrar tus emociones")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build()

            val notificationManager = NotificationManagerCompat.from(applicationContext)

            // ✅ Verificación doble de seguridad
            if (notificationManager.areNotificationsEnabled()) {
                try {
                    notificationManager.notify(DAILY_NOTIFICATION_ID, notification)
                    Log.d("DailyNotification", "Notificación mostrada correctamente")
                } catch (securityException: SecurityException) {
                    Log.e("DailyNotification", "Error de seguridad al mostrar notificación: ${securityException.message}")
                }
            } else {
                Log.w("DailyNotification", "Notificaciones desactivadas por el usuario")
            }

        } catch (e: Exception) {
            Log.e("DailyNotification", "Error inesperado: ${e.message}")
        }
    }

    companion object {
        const val DAILY_NOTIFICATION_ID = 1001
        const val WORK_TAG = "daily_wellness_reminder"
    }
}