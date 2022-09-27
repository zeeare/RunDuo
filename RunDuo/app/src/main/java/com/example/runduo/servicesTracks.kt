package com.example.runduo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runduo.misc.fixValues.CHANNEL_ID_NOTI
import com.example.runduo.misc.fixValues.CHANNEL_NAME_NOTI
import com.example.runduo.misc.fixValues.DELAY_TIMER_UPDATE
import com.example.runduo.misc.fixValues.ID_NOTI
import com.example.runduo.misc.fixValues.INTERVAL_LOCAL_QUICK
import com.example.runduo.misc.fixValues.INTERVAL_LOCAL_UPDATE
import com.example.runduo.misc.fixValues.PAUSE_SERVICE
import com.example.runduo.misc.fixValues.STARTING_OR_RESUME_SERVICE
import com.example.runduo.misc.fixValues.STOP_SERVICE
import com.example.runduo.misc.utilTracks
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias LinePoly = MutableList<LatLng>
typealias MultiPoly = MutableList<LinePoly>

@AndroidEntryPoint
class servicesTracks : LifecycleService() {

    @Inject
    lateinit var locationFusing: FusedLocationProviderClient

    private var firstTrue = true
    private var terminated = false

    private var enabledTimer = false
    private var timingChanges = 0L
    private var timingOfRun = 0L
    private var startingTime = 0L
    private var timingInLastSec = 0L

    companion object {
        val timingMiliRun = MutableLiveData<Long>()
        val nowTracking = MutableLiveData<Boolean>()
        val pathingPath = MutableLiveData<MultiPoly>()
    }
    private val timingSecondsRun = MutableLiveData<Long>()

    @Inject
    lateinit var NotifyBuilderBase: NotificationCompat.Builder

    private lateinit var NowNotifyBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        NowNotifyBuilder = NotifyBuilderBase
        startingPostValues()
       // locationFusing = FusedLocationProviderClient(this)
        nowTracking.observe(this, Observer {
            locationUpdates(it)
            stateOfNotify(it)
        })
    }

    private fun startingPostValues()
    {
        nowTracking.postValue(false)
        pathingPath.postValue(mutableListOf())
        timingSecondsRun.postValue(0L)
        timingMiliRun.postValue(0L)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       intent?.let { when(it.action)
       {
           STARTING_OR_RESUME_SERVICE ->{
               if(firstTrue)
               {
                   foregroundServicesEnabled()
                   firstTrue = false
               } else {
                   Timber.d("Services is resuming...")
                   startingTiming()
               }
           }

           PAUSE_SERVICE ->{
               Timber.d("Services paused")
               serviceForPause()
           }

           STOP_SERVICE ->{
               Timber.d("Services stopped")
               terminated()
           }

       }}
        return super.onStartCommand(intent, flags, startId)
    }

    private fun terminated()
    {
        terminated = true
        firstTrue = true
        serviceForPause()
        startingPostValues()
        stopForeground(true)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifcationCreation(notificationManager: NotificationManager)
    {
        val notifChannels = NotificationChannel(CHANNEL_ID_NOTI, CHANNEL_NAME_NOTI, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(notifChannels)
    }

    private fun nullLinePoly() = pathingPath.value?.apply { add(mutableListOf())
    pathingPath.postValue(this)
    } ?: pathingPath.postValue(mutableListOf(mutableListOf()))

    private fun insertPathing(location: Location?)
    {
        location?.let{
            val reading = LatLng(location.latitude,location.longitude)
            pathingPath.value?.apply { last().add(reading)
            pathingPath.postValue(this)}
        }
    }

    val callingBack = object : LocationCallback()
    {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(nowTracking.value!!)
            {
                result?.locations?.let {
                    coordinates -> for(location in coordinates){ insertPathing(location)
                    Timber.d("Locations that is new: ${location.latitude},${location.longitude}")} }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun locationUpdates(nowTracking: Boolean)
    {
        if(nowTracking)
        {
            if(utilTracks.hasLocationPermissions(this))
            {
                val makeRequest = LocationRequest().apply { interval = INTERVAL_LOCAL_UPDATE
                fastestInterval = INTERVAL_LOCAL_QUICK
                priority = PRIORITY_HIGH_ACCURACY}
                locationFusing.requestLocationUpdates(
                    makeRequest,callingBack, Looper.getMainLooper())
            } else
            {
                locationFusing.removeLocationUpdates(callingBack)
            }
        }
    }

    private fun stateOfNotify(nowTracking: Boolean)
    {
        val notifyActText = if(nowTracking) "Pause" else "Continue"
        val pendingIntent = if(nowTracking){
            val pausingAct = Intent(this, servicesTracks::class.java).apply {
                action = PAUSE_SERVICE
            }
            PendingIntent.getService(this,1,pausingAct, FLAG_UPDATE_CURRENT)
        }
        else
        {
            val continueAct = Intent(this, servicesTracks::class.java).apply {
                action = STARTING_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this,2,continueAct, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NowNotifyBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(NowNotifyBuilder,ArrayList<NotificationCompat.Action>())
        }
        if (!terminated)
        {
            NowNotifyBuilder = NotifyBuilderBase.addAction(R.drawable.pause_black, notifyActText, pendingIntent)
            notificationManager.notify(ID_NOTI,NowNotifyBuilder.build())
        }
        }

    private fun serviceForPause()
    {
        nowTracking.postValue(false)
        enabledTimer = false
    }

    private fun startingTiming()
    {
        nullLinePoly()
        nowTracking.postValue(true)
        startingTime = System.currentTimeMillis()
        enabledTimer = true
        CoroutineScope(Dispatchers.Main).launch {
            while(nowTracking.value!!)
            {
                timingChanges = System.currentTimeMillis() - startingTime
                timingMiliRun.postValue(timingOfRun + timingChanges)
                if(timingMiliRun.value!! >= timingInLastSec + 1000L)
                {
                    timingSecondsRun.postValue(timingSecondsRun.value!! + 1)
                    timingInLastSec += 1000L
                }
                delay(DELAY_TIMER_UPDATE)
            }
            timingOfRun += timingChanges
        }
    }

    private fun foregroundServicesEnabled()
    {
        nowTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notifcationCreation(notificationManager)
        }
        startForeground(ID_NOTI,NowNotifyBuilder.build())
        startingTiming()
        timingSecondsRun.observe(this, Observer {
            if(!terminated)
            {
                val notify = NowNotifyBuilder.setContentText(utilTracks.formatTimingStopWatch(it * 1000L))
                notificationManager.notify(ID_NOTI,notify.build())
            }

        })
    }



}

