package com.notiapp.notification.firebase

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.notiapp.notification.MainActivity
import com.notiapp.notification.dataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TechFirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.v("CloudMessage","From ${message.from}")

        if (message.data.isNotEmpty()){
            Log.v("Cloud Message","Message Data ${message.data}")
        }
        message.data.let {
            Log.v("Cloud Message", "Message Data Body ${it["body"]}")
            Log.v("Cloud Message", "Message Data Title ${it["title"]}")

            showNotificationOnStatusBar(it)

        }
        if(message.notification != null){
            Log.v("Cloud Message","Notification ${message.notification}")
            Log.v("Cloud Message","Notification Title ${message.notification!!.title}")
            Log.v("Cloud Message","Notification Body ${message.notification!!.body}")

        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotificationOnStatusBar(data: Map<String, String>) {

        val intent = Intent(this,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        intent.putExtra("title",data["title"])
        intent.putExtra("body",data["body"])

        var requestCode = System.currentTimeMillis().toInt()
        var pendingIntent : PendingIntent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent =
                PendingIntent.getActivity(this, requestCode,intent, FLAG_MUTABLE)
        }else{
            pendingIntent =
                PendingIntent.getActivity(this, requestCode, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(this,"Global").setAutoCancel(true)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText((data["body"])))
            .setContentIntent(pendingIntent)


        with(NotificationManagerCompat.from(this)){
            notify(requestCode,builder.build())
        }


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch {
            saveGCMToken(token)
        }
    }

    private suspend fun saveGCMToken(token: String){
        val gckTokenKey = stringPreferencesKey("gcm_token")
        baseContext.dataStore.edit { pref -> pref[gckTokenKey] = token }
    }
}