package com.jcy.letsgohiking.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jcy.letsgohiking.home.tab2.MountainItem
@Dao
interface MountainDao{
    @Query("SELECT * FROM mountainItem")
    fun getAll(): List<MountainItem>

    @Insert
    fun insertMountain(mountain: MountainItem)

    @Query("SELECT * FROM mountainItem WHERE mntnId == :id")
    fun findMountain(id: Long): MountainItem

    @Query("DELETE FROM mountainItem")
    fun deleteAll()

    @Delete
    fun delete(mountain: MountainItem)
}