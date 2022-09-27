package com.example.runduo

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DataRun::class],
    version = 1
)

@TypeConverters(ImageConvert::class)
abstract class DatabaseForRun : RoomDatabase() {
    abstract fun retrieveDatabaseRUN(): DAOforRun

}