package com.example.runduo

import android.content.Context
import com.example.runduo.misc.utilTracks
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.popup_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class PopupCustom (
    val Datarun: List<DataRun>,
    c: Context,
    idLayout: Int
) : MarkerView (c , idLayout) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f,-height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if ( e == null)
        {
            return
        }
        val idRunNow = e.x.toInt()
        val listingRun = Datarun[idRunNow]
        val currTime = Calendar.getInstance().apply {
            timeInMillis = listingRun.timeStamps
        }
        val formatDate = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        stuffsDate.text = formatDate.format(currTime.time)

        val speedAvg = "${listingRun.averageSpeedInKMH / 1000f}km/h"
        stuffsSpeedAvg.text = speedAvg

        val distKM = "${listingRun.distanceInMeters / 1000f}km"
        stuffsDistance.text = distKM

        popupTiming.text = utilTracks.formatTimingStopWatch(listingRun.timingInMillis)

        val burnedCalories = "${listingRun.caloriesBurned}kcal"
        popupCalories.text = burnedCalories
    }
}