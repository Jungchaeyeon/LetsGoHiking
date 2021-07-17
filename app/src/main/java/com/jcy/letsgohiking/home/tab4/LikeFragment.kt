package com.jcy.letsgohiking.home.tab4

import android.os.Bundle
import android.view.View
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentLikeBinding
import java.util.*


class LikeFragment : BaseDataBindingFragment<FragmentLikeBinding>(R.layout.fragment_like) {

    companion object {
        fun getInstance() = LikeFragment()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun FragmentLikeBinding.onBind() {
        vi = this@LikeFragment
    }
}