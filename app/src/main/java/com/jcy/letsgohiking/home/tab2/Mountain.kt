package com.jcy.letsgohiking.home.tab2

import com.google.gson.annotations.SerializedName

//data class Mountain(
//        @SerializedName("mntnnm") var mntnName: String,
//        @SerializedName("mntninfohght") var mntnHeight : Int,
//        @SerializedName("mntninfopoflc") var mntnLocation : String,
//        @SerializedName("mntnsbttlinfo") var mntnInfo : String,
//        @SerializedName("mntnattchimageseq") var mntnImg: String,
//    ) {
//        constructor() : this("", 0, "", "", "")
//    }


data class Mountain(
    val resonse: Response

    ){
    data class Response(
    val script: String,
    val header: Header,
    val body: Body
    )

    data class Header(
        val resultCode: Int,
        val resultMsg: String,
    )

    data class Body(
        val items: Items
    )

    data class Items(
        val item: List<MountainItem>
    )
}


