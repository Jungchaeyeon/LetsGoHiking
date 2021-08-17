package com.jcy.letsgohiking.home.tab2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class MountainItem(

    @PrimaryKey @SerializedName("mntnid") var mntnId : Long,
    @ColumnInfo @SerializedName("mntnnm") var mntnName: String,
    @ColumnInfo @SerializedName("mntninfohght") var mntnHeight : Int,
    @ColumnInfo @SerializedName("mntninfopoflc") var mntnLocation : String,
    @ColumnInfo @SerializedName("mntnsbttlinfo") var mntnInfo : String,
    @ColumnInfo @SerializedName("mntnattchimageseq") var mntnImg: String,
    @ColumnInfo @SerializedName("hndfmsmtnslctnrson") var top100reson : String,
    @ColumnInfo @SerializedName("mntninfodtlinfocont") var mntnDetailInfo : String,
    @ColumnInfo @SerializedName("crcmrsghtnginfoetcdscrt") var courseInfo : String,
    @ColumnInfo var isBookmarked : Boolean = false
    ) :Serializable{
        constructor() : this(0,"", 0, "", "", "","","","",false)
    }

