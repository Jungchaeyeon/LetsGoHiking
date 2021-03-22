package com.hdh.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.hdh.base.Disposer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment(private val layoutId: Int) : Fragment(), Disposer {

    val hasFocusObservers = mutableListOf<(Boolean) -> Unit>()
    val lifecycleObservers = mutableListOf<(Lifecycle.Event) -> Unit>()

    private val compositeDisposableOnPause = CompositeDisposable()
    private val compositeDisposableOnDestroy = CompositeDisposable()

    protected abstract fun setupView(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupProperties(arguments)

        val view = createView(inflater, container)

        setupView(view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runOnResume()
    }

    override fun onDestroy() {
        runOnPause()
        compositeDisposableOnDestroy.clear()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            runOnPause()
        } else {
            runOnResume()
        }
    }

    open fun runOnResume() {

    }

    open fun runOnPause() {
        compositeDisposableOnPause.clear()
    }

    override fun disposeOnPause(disposable: Disposable) {
        compositeDisposableOnPause.add(disposable)
    }

    override fun disposeOnDestroy(disposable: Disposable) {
        compositeDisposableOnDestroy.add(disposable)
    }

    protected open fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(layoutId, container, false)
    }

    protected open fun setupProperties(bundle: Bundle?) {
        //do nothing
    }

    open fun onBackPressed(): Boolean = true

    fun commitAllowingStateLoss(fragmentManager: FragmentManager?, @IdRes containerViewId: Int) {
        fragmentManager?.run {
            beginTransaction().replace(containerViewId, this@BaseFragment)
                .commitAllowingStateLoss()
        }
    }
}