package com.notiapp.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

//Create Singleton instance of DataStore Preference
val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "LocalStore")
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val name = "JetpackPushNotification"
        val description = "Jetpack Push Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("Global",name, importance)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        channel.description = description;

        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}