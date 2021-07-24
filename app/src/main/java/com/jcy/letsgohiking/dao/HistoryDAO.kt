package com.jcy.letsgohiking.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jcy.letsgohiking.home.tab2.History

@Dao
interface HistoryDAO {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE mntnName == :keyword")
    fun delete(keyword: String)

    @Query("SELECT * FROM history WHERE mntnName == :keyword")
    fun find(keyword: String): List<History>
}