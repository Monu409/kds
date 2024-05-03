package com.zotto.kds.database

import android.content.Context
import androidx.room.Room
import com.zotto.kds.database.AppDatabase.Companion.MIGRATION

class DatabaseClient(mCtx: Context) {


    //our app database object
    private var appDatabase: AppDatabase? = null
    init {
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase::class.java, "zotto_kds")
            .addMigrations(MIGRATION)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    companion object{
        private var mInstance: DatabaseClient? = null
        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient? {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance
        }

    }
    fun getAppDatabase(): AppDatabase {
        return appDatabase!!
    }
}