package com.hdh.base.activity

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.hdh.base.R

abstract class BaseDataBindingActivity<B : ViewDataBinding>(
    private val layoutId: Int,
    private val isPopup: Boolean = false
) : BaseActivity(layoutId) {

    protected lateinit var binding: B

    abstract fun B.onBind()

    override fun setContentView() {
        binding = DataBindingUtil.setContentView(this, layoutId)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
            if (isPopup) {
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            }
        }
    }

    override fun setupView() {
        binding.run {
            lifecycleOwner = this@BaseDataBindingActivity
            onBind()
        }
    }

    override fun finish() {
        super.finish()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
            if (isPopup) {
                overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)
            }
        }
    }
}