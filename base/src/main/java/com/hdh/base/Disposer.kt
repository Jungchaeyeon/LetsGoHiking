package com.hdh.base

import io.reactivex.disposables.Disposable

interface Disposer {
    fun disposeOnPause(disposable: Disposable)
    fun disposeOnDestroy(disposable: Disposable)
}

fun Disposable.disposeOnPause(disposer: Disposer): Disposable {
    disposer.disposeOnPause(this)

    return this
}

fun Disposable.disposeOnDestroy(disposer: Disposer): Disposable {
    disposer.disposeOnDestroy(this)

    return this
}