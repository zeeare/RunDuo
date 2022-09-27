package com.example.runduo

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class DataRun (
    var img: Bitmap? = null,
    var averageSpeedInKMH: Float = 0f,
    var timeStamps: Long = 0L,
    var timingInMillis: Long = 0L,
    var distanceInMeters: Int = 0,
    var caloriesBurned: Int = 0
    ){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}