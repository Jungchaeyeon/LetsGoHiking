package com.jcy.letsgohiking.ext

import java.util.*

fun <T> List<T>.random() : T {
    val random = Random().nextInt((size))
    return get(random)
}
