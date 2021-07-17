package com.jcy.letsgohiking.home.tab3

import android.os.Bundle
import android.view.View
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentBookmarkBinding
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import com.jcy.letsgohiking.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class BookmarkFragment : BaseDataBindingFragment<FragmentBookmarkBinding>(R.layout.fragment_bookmark) {

    companion object {
        fun getInstance() = BookmarkFragment()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun FragmentBookmarkBinding.onBind() {
        vi = this@BookmarkFragment
    }
}