package com.hdh.base.activity

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hdh.base.Disposer
import com.hdh.base.R
import com.hdh.base.fragment.BaseDialogFragment
import com.hdh.base.fragment.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BaseActivity(private val layoutId: Int) : AppCompatActivity(),
    Disposer {

    private val compositeDisposableOnPause = CompositeDisposable()
    private val compositeDisposableOnDestroy = CompositeDisposable()
    protected var backToastBlock = false

    protected abstract fun setupView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {       //O의 경우 orientation 설정하면 강제종료됨
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setupProperties(intent?.extras)
        setContentView()
        setupView()
    }

    protected open fun setupProperties(bundle: Bundle?) {
        //do nothing
    }

    protected open fun setContentView() {
        setContentView(layoutId)
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    protected fun hideKeyboard(view: View? = currentFocus) {
        view?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                it.windowToken,
                0
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    protected fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            editText,
            0
        )
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.fragments.filter { it.isVisible }.forEach {
            when (it) {
                is BaseFragment -> it.runOnResume()
                is BaseDialogFragment -> it.runOnResume()
                else -> {
                }
            }
        }
    }

    override fun onPause() {
        compositeDisposableOnPause.clear()

        supportFragmentManager.fragments.forEach {
            when (it) {
                is BaseFragment -> it.runOnPause()
                is BaseDialogFragment -> it.runOnPause()
                else -> {
                }
            }
        }
        super.onPause()
    }

    override fun onDestroy() {
        compositeDisposableOnDestroy.clear()
        super.onDestroy()
    }

    override fun disposeOnPause(disposable: Disposable) {
        compositeDisposableOnPause.add(disposable)
    }

    override fun disposeOnDestroy(disposable: Disposable) {
        compositeDisposableOnDestroy.add(disposable)
    }

    override fun onBackPressed() {
        if (!backToastBlock && ActivityBackToast.showToast(isTaskRoot)) {
            Toast.makeText(this, getString(R.string.backCloseToast), Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }
}