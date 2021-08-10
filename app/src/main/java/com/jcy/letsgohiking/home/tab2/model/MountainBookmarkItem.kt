package com.jcy.letsgohiking.home.tab2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class MountainBookmarkItem(

    @PrimaryKey  var mntnName: String,
    @ColumnInfo var mntnHeight : Int,
    @ColumnInfo  var mntnLocation : String,
    @ColumnInfo  var mntnInfo : String,
    @ColumnInfo  var mntnImg: String,
    @ColumnInfo  var top100reson : String,
    @ColumnInfo  var mntnDetailInfo : String,
    @ColumnInfo var courseInfo : String
    ) :Serializable{
        constructor() : this("", 0, "", "", "","","","")
    }

