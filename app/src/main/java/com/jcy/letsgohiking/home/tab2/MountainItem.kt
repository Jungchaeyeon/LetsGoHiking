package com.jcy.letsgohiking.home.tab2

import com.google.gson.annotations.SerializedName

data class MountainItem(

    @SerializedName("mntnnm") var mntnName: String,
    @SerializedName("mntninfohght") var mntnHeight : Int,
    @SerializedName("mntninfopoflc") var mntnLocation : String,
    @SerializedName("mntnsbttlinfo") var mntnInfo : String,
    @SerializedName("mntnattchimageseq") var mntnImg: String,
    ) {
        constructor() : this("", 0, "", "", "")
    }

