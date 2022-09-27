package com.example.runduo.models

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.runduo.mainRespo.RespoMain

class viewModelStats @ViewModelInject constructor(respoMain: RespoMain): ViewModel()
{
    val runsTimingTotal = respoMain.getTimingTotalInMili()
    val caloriesTotal = respoMain.getCaloriesTotalBurned()
    val distanceTotal = respoMain.getDistanceTotalByMeters()
    val avgSpeedTotal = respoMain.getSpeedTotalAverageInKMH()
    val sortingWithDates = respoMain.getEverythingByDate()
}