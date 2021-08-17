package com.jcy.letsgohiking.home.tab2.model

import com.google.gson.annotations.SerializedName

data class ResultSearchImage(
    @SerializedName("lastBuildDate") var lastBuildDate: String ="",
    @SerializedName("total") var total: Int = 0,
    @SerializedName("start") var start: Int = 0,
    @SerializedName("display") var display: Int = 0,
    @SerializedName("items") var items: List<Item>
)
data class Item(
    @SerializedName("title") var title: String ="",
    @SerializedName("link") var link: String ="",
    @SerializedName("thumbnail") var thumbnail: String ="",
    @SerializedName("sizeheight") var sizeheight: String ="",
    @SerializedName("sizewidth") var sizewidth: String =""
)
