package com.hdh.base.viewmodel

import androidx.lifecycle.*
import com.hdh.base.Disposer
import com.hdh.base.fragment.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel(), LifecycleObserver, Disposer {
    protected val compositeDisposableOnPause = CompositeDisposable()
    protected val compositeDisposableOnDestroy = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreated() {
        //do nothing
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
        //do nothing
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
        //do nothing
    }

    open fun onFocusIn() {
        //do nothing
    }

    open fun onFocusOut() {
        //do nothing
    }

    override fun disposeOnPause(disposable: Disposable) {
        compositeDisposableOnPause.add(disposable)
    }

    override fun disposeOnDestroy(disposable: Disposable) {
        compositeDisposableOnDestroy.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
        compositeDisposableOnPause.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        compositeDisposableOnDestroy.clear()
    }

    fun bindLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    fun bindLifecycle(owner: BaseFragment) {
        owner.lifecycleObservers.clear()
        owner.lifecycleObservers.add {
            when (it) {
                Lifecycle.Event.ON_CREATE -> onCreated()
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_STOP -> onStop()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                else -> {
                }
            }
        }

        owner.hasFocusObservers.add {
            if (it) {
                onFocusIn()
            } else {
                onFocusOut()
            }
        }
    }
}