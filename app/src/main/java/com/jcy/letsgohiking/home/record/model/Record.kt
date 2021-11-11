package com.jcy.letsgohiking.home.record.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Record (
    @PrimaryKey var mntnName: String="",
    @ColumnInfo var rid: String= "",
    @ColumnInfo var hikingYear: String="",
    @ColumnInfo var hikingDate: String="",
    @ColumnInfo var hikingWith: String="",
    @ColumnInfo var hikingImg: Bitmap ?= null,
    @ColumnInfo var content: String="",
): Serializable {
    constructor() : this("","","","","",null,"")
}