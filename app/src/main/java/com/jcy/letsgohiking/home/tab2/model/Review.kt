package com.jcy.letsgohiking.home.tab2.model


data class Review(
    var writer: String ="",
    var userId: String ="",
    var review: String=""
){
    constructor(): this("","")
}
