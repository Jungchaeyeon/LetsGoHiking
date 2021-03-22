package com.hdh.base.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import com.hdh.base.Disposer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseDialogFragment(private val layoutId: Int) : DialogFragment(), Disposer {

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

        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }

        return view
    }

    override fun onDestroy() {
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
}