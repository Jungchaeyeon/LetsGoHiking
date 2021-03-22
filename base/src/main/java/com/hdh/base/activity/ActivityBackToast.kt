package com.hdh.base.activity

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object ActivityBackToast {
    private var showToast = false

    fun showToast(isTaskRoot: Boolean): Boolean {
        val ret = isTaskRoot && !showToast

        if (ret) {
            showToast = true
            Observable.just(1)
                .delay(2, TimeUnit.SECONDS)
                .subscribe { showToast = false }
        }

        return ret
    }
}