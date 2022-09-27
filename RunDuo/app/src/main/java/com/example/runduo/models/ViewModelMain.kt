package com.example.runduo.models

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runduo.DataRun
import com.example.runduo.mainRespo.RespoMain
import com.example.runduo.misc.TypeSorting
import kotlinx.coroutines.launch

class viewModelMain @ViewModelInject constructor(val respoMain: RespoMain): ViewModel()
{
    private val sortingDateRuns = respoMain.getEverythingByDate()
    private val sortingCaloriesRuns = respoMain.getEverythingByCaloriesBurned()
    private val sortingDistanceRuns = respoMain.getEverythingByDistance()
    private val sortingAvgSpeedRuns = respoMain.getEverythingByAvgSpeed()
    private val sortingTimeMillisRuns = respoMain.getEverythingByTimeInMillis()

    val sortingRuns = MediatorLiveData<List<DataRun>>()
    var typeSorting = TypeSorting.DATE

    init {
        sortingRuns.addSource(sortingDateRuns) { r ->
            if(typeSorting == TypeSorting.DATE)
                r?.let { sortingRuns.value = it }
        }
        sortingRuns.addSource(sortingCaloriesRuns) { r ->
            if(typeSorting == TypeSorting.BURNED_CALORIES)
                r?.let { sortingRuns.value = it }
        }
        sortingRuns.addSource(sortingDistanceRuns) { r ->
            if(typeSorting == TypeSorting.DISTANCES)
                r?.let { sortingRuns.value = it }
        }
        sortingRuns.addSource(sortingAvgSpeedRuns) { r ->
            if(typeSorting == TypeSorting.SPEED_AVG)
                r?.let { sortingRuns.value = it }
        }
        sortingRuns.addSource(sortingTimeMillisRuns) { r ->
            if(typeSorting == TypeSorting.TIME_RUNS)
                r?.let { sortingRuns.value = it }
        }
    }

    fun addedRun(dataRun: DataRun) = viewModelScope.launch {
        respoMain.insertedData(dataRun)
    }

    fun removeRun(dataRun: DataRun) = viewModelScope.launch {
        respoMain.deletedData(dataRun)
    }

    fun runsSorting(typeSorting: TypeSorting) = when(typeSorting) {
        TypeSorting.DATE -> sortingDateRuns.value?.let { sortingRuns.value = it}
        TypeSorting.BURNED_CALORIES -> sortingCaloriesRuns.value?.let { sortingRuns.value = it}
        TypeSorting.DISTANCES -> sortingDistanceRuns.value?.let { sortingRuns.value = it}
        TypeSorting.SPEED_AVG -> sortingAvgSpeedRuns.value?.let { sortingRuns.value = it}
        TypeSorting.TIME_RUNS -> sortingTimeMillisRuns.value?.let { sortingRuns.value = it}
    }.also {
        this.typeSorting = typeSorting
    }
}