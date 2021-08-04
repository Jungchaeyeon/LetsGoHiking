package com.jcy.letsgohiking.home.tab2

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MountainItem(

    @SerializedName("mntnnm") var mntnName: String,
    @SerializedName("mntninfohght") var mntnHeight : Int,
    @SerializedName("mntninfopoflc") var mntnLocation : String,
    @SerializedName("mntnsbttlinfo") var mntnInfo : String,
    @SerializedName("mntnattchimageseq") var mntnImg: String,
    @SerializedName("hndfmsmtnslctnrson") var top100reson : String,
    @SerializedName("mntninfodtlinfocont") var mntnDetailInfo : String,
    @SerializedName("crcmrsghtnginfoetcdscrt") var courseInfo : String
    ) :Serializable{
        constructor() : this("", 0, "", "", "","","","")
    }

