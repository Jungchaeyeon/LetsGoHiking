package com.jcy.letsgohiking.home.tab2

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentSearchBinding
import java.util.*


class SearchFragment : BaseDataBindingFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    companion object {
        fun getInstance() = SearchFragment()
    }

    override fun onResume() {
        super.onResume()

    }
    lateinit var viewPagers: ViewPager
    lateinit var tabLayouts: TabLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()

        binding.tabLayout.setOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun setUpViewPager() {
        viewPagers = binding.pager
        tabLayouts = binding.tabLayout

        var adapter = DetailPagerAdapter(fragmentManager!!)
        adapter.addFragment(RecommendCourseFragment.getInstance(), "코스 추천")
        adapter.addFragment(DifficultyCourseFragment.getInstance(), "난이도 별 코스")

        binding.pager.adapter = adapter
        binding.tabLayout.setupWithViewPager(viewPagers)
    }

    override fun FragmentSearchBinding.onBind() {
        vi = this@SearchFragment
    }

}
