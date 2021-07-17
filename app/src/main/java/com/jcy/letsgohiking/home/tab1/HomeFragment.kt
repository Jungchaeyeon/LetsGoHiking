package com.jcy.letsgohiking.home.tab1

import android.os.Bundle
import android.view.View
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import java.util.*


class HomeFragment : BaseDataBindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    companion object {
        fun getInstance() = HomeFragment()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun FragmentHomeBinding.onBind() {
        vi = this@HomeFragment
    }
}