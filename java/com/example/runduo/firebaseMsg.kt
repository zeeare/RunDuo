package com.example.runduo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.example.runduo.misc.fixValues
import com.example.runduo.misc.utilTracks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.collections.HashMap

class firebaseMsg : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val mapping: Map<String, String> = remoteMessage.data

            val msgHead = mapping["msgHead"]
            val msgBody = mapping["msgBody"]
            val otherId = mapping["otherId"]
            val otherImg = mapping["otherImg"]
            val chatroomId = mapping["chatroomId"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                notifCreation(msgHead!!, msgBody!!, otherId!!, otherImg!!, chatroomId!!)
        }
    }

    private fun updateToken(token: String) {

        val dataRef =
            FirebaseDatabase.getInstance().getReference("Users").child(utilTracks.retriveUserID()!!)
        val map: MutableMap<String, Any> = HashMap()
        map["tokenId"] = token
        dataRef.updateChildren(map)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifCreation(
        msgHead: String,
        msgBody: String,
        otherId: String,
        otherImg: String,
        chatroomId: String
    ) {
        val notif = NotificationChannel(fixValues.MSGCHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH
        )

        notif.setShowBadge(true)
        notif.enableLights(true)
        notif.enableVibration(true)
        notif.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val notiManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notiManager.createNotificationChannel(notif)

        val ScreenNext = Intent(this, ChattingActs::class.java)

        ScreenNext.putExtra("otherId", otherId)
        ScreenNext.putExtra("otherImg", otherImg)
        ScreenNext.putExtra("chatroomId", chatroomId)
        ScreenNext.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val inPending = PendingIntent.getActivity(this, 0, ScreenNext, PendingIntent.FLAG_ONE_SHOT)

        val notify = Notification.Builder(this, fixValues.MSGCHANNEL_ID)
            .setContentTitle(msgHead)
            .setContentText(msgBody)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.black, null))
            .setContentIntent(inPending)
            .build()

        notiManager.notify(100, notify)
    }

}