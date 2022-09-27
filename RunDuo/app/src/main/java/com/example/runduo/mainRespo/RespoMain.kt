package com.example.runduo.mainRespo

import com.example.runduo.DAOforRun
import com.example.runduo.DataRun
import javax.inject.Inject

class RespoMain @Inject constructor( val daoRun: DAOforRun)
{
    suspend fun insertedData(datarun: DataRun) = daoRun.insertedData(datarun)

    suspend fun deletedData(datarun: DataRun) = daoRun.deletedData(datarun)

    fun getEverythingByAvgSpeed() = daoRun.getEverythingByAvgSpeed()

    fun getEverythingByDate() = daoRun.getEverythingByDate()

    fun getEverythingByTimeInMillis() = daoRun.getEverythingByTimeInMillis()

    fun getEverythingByDistance() = daoRun.getEverythingByDistance()

    fun getEverythingByCaloriesBurned() = daoRun.getEverythingByCaloriesBurned()

    fun getTimingTotalInMili() = daoRun.getTimingTotalInMili()

    fun getDistanceTotalByMeters() = daoRun.getDistanceTotalByMeters()

    fun getCaloriesTotalBurned() = daoRun.getCaloriesTotalBurned()

    fun getSpeedTotalAverageInKMH() = daoRun.getSpeedTotalAverageInKMH()

}