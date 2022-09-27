package com.example.runduo.misc

import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import android.Manifest
import android.location.Location
import com.example.runduo.LinePoly
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

object utilTracks {
    fun hasLocationPermissions(context: Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    else
        {
            EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

    fun formatUserTimeStamp(time: Long): String? {
         val SECMillis: Int = 1000
         val MINMillis: Int = 60 * SECMillis
         val HRMillis: Int = 60 * MINMillis
         val DAYMillis: Int = 24 * HRMillis

        var timing = time
        if (timing < 1000000000000L) {
            timing *= 1000
        }
        val current = System.currentTimeMillis()
        if (timing > current || timing <= 0) {
            return null
        }
        val differents = current - timing

        return when {
            differents < MINMillis -> {
                "Just now"
            }
            differents < 2 * MINMillis -> {
                "1 minute ago"
            }
            differents < 50 * MINMillis -> {
                (differents / MINMillis).toString() + " minutes ago"
            }
            differents < 90 * MINMillis -> {
                "1 hour ago"
            }
            differents < 24 * HRMillis -> {
                (differents / HRMillis).toString() + " hours ago"
            }
            differents < 48 * HRMillis -> {
                "Yesterday"
            }
            else -> {
                (differents / DAYMillis).toString() + " days ago"
            }
        }
    }

    fun compiledLength(poly: LinePoly): Float
    {
        var totalDist = 0f
        for (i in 0..poly.size - 2)
        {
            val posi1 = poly[i]
            val posi2 = poly[i + 1]
            val posiResult = FloatArray(1)
            Location.distanceBetween(posi1.latitude,
                posi1.longitude,
                posi2.latitude,
                posi2.longitude,
                posiResult
            )
            totalDist += posiResult[0]
        }
        return totalDist
    }

    fun formatTimingStopWatch(ms: Long, Millis:Boolean=false): String
    {
        var millis = ms
        val inHours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(inHours)
        val inMinute = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(inMinute)
        val inSeconds = TimeUnit.MILLISECONDS.toSeconds(millis)

        if(!Millis)
        {
            return "${if (inHours < 10) "0" else ""}$inHours:" +
                    "${if (inMinute < 10) "0" else ""}$inMinute:" +
                    "${if (inSeconds < 10) "0" else ""}$inSeconds"
        }
        millis -= TimeUnit.SECONDS.toMillis(inSeconds)
        millis /= 10
        return "${if (inHours < 10) "0" else ""}$inHours:" +
                "${if (inMinute < 10) "0" else ""}$inMinute:" +
                "${if (inSeconds < 10) "0" else ""}$inSeconds:" +
                "${if (millis < 10) "0" else ""}$millis"
    }

    fun retriveUserID(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }
}