package com.example.runduo.injectionss

import android.app.Application
import androidx.room.Room
import com.example.runduo.DatabaseForRun
import com.example.runduo.misc.fixValues.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object ModApplication {

    @Singleton
    @Provides
    fun provideDAOforRun(db: DatabaseForRun) = db.retrieveDatabaseRUN()

    @Singleton
    @Provides
    fun provideDatabaseAPP(app: Application): DatabaseForRun {
        return Room.databaseBuilder(app, DatabaseForRun::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

}