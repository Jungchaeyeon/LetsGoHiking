package com.jcy.letsgohiking.home.tab1

import android.os.Bundle
import android.view.View
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import com.jcy.letsgohiking.util.Log
import java.util.*


class HomeFragment : BaseDataBindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    companion object {
        fun getInstance() = HomeFragment()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("오늘달", (Calendar.getInstance().get(Calendar.MONTH)+1).toString())
    }

    override fun FragmentHomeBinding.onBind() {
        vi = this@HomeFragment
    }
}