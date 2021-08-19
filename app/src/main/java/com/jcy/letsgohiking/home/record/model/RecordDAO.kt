package com.jcy.letsgohiking.home.record.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface RecordDAO{

        @Query("SELECT * FROM record")
        fun getRecordData(): List<Record>

        @Insert
        fun insertRecord(record: Record)

        @Query("DELETE FROM record WHERE mntnName == :keyword")
        fun delete(keyword: String)

        @Query("SELECT * FROM record WHERE mntnName == :keyword")
        fun find(keyword: String): Record

}