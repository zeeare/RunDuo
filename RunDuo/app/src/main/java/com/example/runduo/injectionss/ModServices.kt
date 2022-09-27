package com.example.runduo.injectionss

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runduo.MainActivity
import com.example.runduo.R
import com.example.runduo.misc.fixValues
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ModServices {

    @ServiceScoped
    @Provides
    fun locationFusing(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun pendingIntentMainScreen(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app, 0, Intent(app, MainActivity::class.java).also {
            it.action = fixValues.TRACKING_SHOW
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun NotifyBuilderBase(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, fixValues.CHANNEL_ID_NOTI)
        .setAutoCancel(false).setOngoing(true).setSmallIcon(R.drawable.directions_black)
        .setContentTitle("RunDuo").setContentText("00:00:00").setContentIntent(pendingIntent)
}