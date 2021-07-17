package com.jcy.letsgohiking.home.tab5

import android.os.Bundle
import android.view.View
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import com.jcy.letsgohiking.databinding.FragmentProfileBinding
import com.jcy.letsgohiking.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class ProfileFragment : BaseDataBindingFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    companion object {
        fun getInstance() = ProfileFragment()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun FragmentProfileBinding.onBind() {
        vi = this@ProfileFragment
    }
}