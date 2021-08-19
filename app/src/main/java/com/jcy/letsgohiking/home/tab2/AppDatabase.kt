package com.jcy.letsgohiking.home.tab2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jcy.letsgohiking.dao.HistoryDAO
import com.jcy.letsgohiking.dao.MountainDao
import com.jcy.letsgohiking.home.record.model.Record
import com.jcy.letsgohiking.home.record.model.RecordDAO
import com.jcy.letsgohiking.home.tab2.model.History
import com.jcy.letsgohiking.home.tab2.model.MountainItem

@Database(entities = [History::class, MountainItem::class, Record::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao() : HistoryDAO
    abstract fun mountainDao(): MountainDao
    abstract fun recordDao() : RecordDAO
}

fun getAppDatabase(context: Context): AppDatabase{
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "MountainSearchDB"
    )
        .allowMainThreadQueries()
        //.fallbackToDestructiveMigrationFrom(2, 5)
        .build()
}
fun getAppDatabaseMntn(context: Context): AppDatabase{
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "MountainBookmarkDB"
    )  .allowMainThreadQueries()
        //.fallbackToDestructiveMigrationFrom(2, 5)
        .build()
}
fun getAppDatabaseRecord(context: Context): AppDatabase{
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "HikingRecordDB"
    )  .allowMainThreadQueries()
        .build()
}