package com.jcy.letsgohiking.home.login.model

data class User(
    var userId: String ="",
    var userNickname: String="",
    var profileImageUrl: String="",
    var userHikingClass: Int=0
)
