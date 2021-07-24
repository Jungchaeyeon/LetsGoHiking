package com.jcy.letsgohiking.home.tab2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jcy.letsgohiking.dao.HistoryDAO

@Database(entities = [History::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao() : HistoryDAO
}

fun getAppDatabase(context: Context): AppDatabase{
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "MountainSearchDB"
    )
        .build()
}