package com.example.runduo.frags

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runduo.PopupCustom
import com.example.runduo.R
import com.example.runduo.misc.utilTracks
import com.example.runduo.models.viewModelStats
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.stats_frags.*
import kotlin.math.round

@AndroidEntryPoint
class StatsFrags : Fragment(R.layout.stats_frags) {
    private val viewingModel: viewModelStats by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ObserverSub()
        barChartEdit()
    }

    private fun barChartEdit()
    {
        chartBAR.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            textColor = Color.BLACK
            axisLineColor = Color.BLACK
            setDrawGridLines(false)
        }
        chartBAR.axisLeft.apply {
            textColor = Color.BLACK
            axisLineColor = Color.BLACK
            setDrawGridLines(false)
        }
        chartBAR.axisRight.apply {
            textColor = Color.BLACK
            axisLineColor = Color.BLACK
            setDrawGridLines(false)
        }
        chartBAR.apply {
            description.text = "Speed OverTime (Average)"
            legend.isEnabled = false
        }
    }

    private fun ObserverSub()
    {
        viewingModel.runsTimingTotal.observe(viewLifecycleOwner, Observer {
            it?.let{
                val runsTimingTotal = utilTracks.formatTimingStopWatch(it)
                statsTimingData.text = runsTimingTotal
            }
        })
        viewingModel.caloriesTotal.observe(viewLifecycleOwner, Observer {
            it?.let{
                val burnedTotal = "${it}kcal"
                statsCaloriesData.text = burnedTotal
            }
        })
        viewingModel.avgSpeedTotal.observe(viewLifecycleOwner, Observer {
            it?.let{
                val speedAvg = round(it * 10f) / 10f
                val stringForm = "${speedAvg}km/h"
                statsSpeedData.text = stringForm
            }
        })
        viewingModel.distanceTotal.observe(viewLifecycleOwner, Observer {
            it?.let{
                val inKM = it / 1000f
                val distanceInTotal = round(inKM * 10f) / 10f
                val stringForm = "${distanceInTotal}km"
                statsDistanceData.text = stringForm
            }
        })
        viewingModel.sortingWithDates.observe(viewLifecycleOwner, Observer {
            it?.let{
                val speedAvgALL = it.indices.map { i -> BarEntry(i.toFloat(),it[i].averageSpeedInKMH) }
                val dataSET = BarDataSet(speedAvgALL,"Speeds OverTime (Average)").apply {
                    valueTextColor = Color.BLACK
                    color = ContextCompat.getColor(requireContext(), R.color.base)
                }
                chartBAR.data = BarData(dataSET)
                val marker = PopupCustom(it,requireContext(),R.layout.popup_view)
                chartBAR.marker = marker
                chartBAR.invalidate()
            }
        })
    }
}