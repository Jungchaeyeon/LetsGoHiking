package com.jcy.letsgohiking.home.tab2

import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentDifficultyCourseBinding

class DifficultyCourseFragment: BaseDataBindingFragment<FragmentDifficultyCourseBinding>(R.layout.fragment_difficulty_course) {
    override fun FragmentDifficultyCourseBinding.onBind() {

    }
    companion object {
        fun getInstance() = DifficultyCourseFragment()
    }
}