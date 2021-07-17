package com.jcy.letsgohiking.model

data class ErrorResponse(
    val code: Int,
    val name: String,
    val message: String,
)