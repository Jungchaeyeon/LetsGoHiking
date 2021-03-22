package com.jcy.trashclassification.ext

import android.widget.Toast
import com.jcy.trashclassification.MyApplication
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun String.showLongToastSafe() {
    Single.just(this).observeOn(AndroidSchedulers.mainThread()).subscribe { _ ->
        Toast.makeText(
            MyApplication.globalApplicationContext, this, Toast.LENGTH_LONG
        ).show()
    }
}

fun String.showShortToastSafe() {
    Single.just(this).observeOn(AndroidSchedulers.mainThread()).subscribe { _ ->
        Toast.makeText(
            MyApplication.globalApplicationContext, this, Toast.LENGTH_SHORT
        ).show()
    }
}
