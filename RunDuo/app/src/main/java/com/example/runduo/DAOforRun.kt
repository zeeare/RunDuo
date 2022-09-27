package com.example.runduo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DAOforRun {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertedData(run: DataRun)

    @Delete
    suspend fun deletedData(run: DataRun)

    @Query("SELECT * FROM run_table ORDER BY averageSpeedInKMH DESC")
    fun getEverythingByAvgSpeed(): LiveData<List<DataRun>>

    @Query("SELECT * FROM run_table ORDER BY timeStamps DESC")
    fun getEverythingByDate(): LiveData<List<DataRun>>

    @Query("SELECT * FROM run_table ORDER BY timingInMillis DESC")
    fun getEverythingByTimeInMillis(): LiveData<List<DataRun>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMeters DESC")
    fun getEverythingByDistance(): LiveData<List<DataRun>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun getEverythingByCaloriesBurned(): LiveData<List<DataRun>>

    @Query("SELECT SUM(timingInMillis) FROM run_table")
    fun getTimingTotalInMili(): LiveData<Long>

    @Query("SELECT SUM(distanceInMeters) FROM run_table")
    fun getDistanceTotalByMeters(): LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getCaloriesTotalBurned(): LiveData<Int>

    @Query("SELECT AVG(averageSpeedInKMH) FROM run_table")
    fun getSpeedTotalAverageInKMH(): LiveData<Float>
}