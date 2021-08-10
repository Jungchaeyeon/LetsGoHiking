package com.jcy.letsgohiking.ext

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.jcy.letsgohiking.MyApplication
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.prefs.Preferences

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
