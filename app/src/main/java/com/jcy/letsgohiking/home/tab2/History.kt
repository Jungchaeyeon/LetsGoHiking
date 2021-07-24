package com.jcy.letsgohiking.home.tab2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey var uid: Int?,
    @ColumnInfo(name="mntnName") val mntnName: String
)
