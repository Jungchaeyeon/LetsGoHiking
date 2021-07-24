package com.jcy.letsgohiking.home.tab2

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.MainActivity
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentSearchBinding
import com.jcy.letsgohiking.ext.hideKeyboard
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.item_keyword_history.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.thread


class SearchFragment : BaseDataBindingFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var db: AppDatabase
    private lateinit var historyAdapter: KeywordHistoryAdapter

    companion object {
        fun getInstance() = SearchFragment()
    }

    lateinit var viewPagers: ViewPager
    lateinit var tabLayouts: TabLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()
        db = getAppDatabase(requireContext())

        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        initHistoryRecyclerView()

    }

    private fun initHistoryRecyclerView() {
        historyAdapter = KeywordHistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })
        binding.historyRecyclerView.adapter = historyAdapter
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        initSearchEditText()
    }

    private fun initSearchEditText() {
        binding.searchEdt.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                onClickSearch(binding.searchEdt.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.searchEdt.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
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

    fun onClickSearch(_keyword: String) {
        val keyword = _keyword.trim()

        if (keyword != "") {

            hideHistoryView()
            binding.searchEdt.text.clear()
            binding.searchEdt.hideKeyboard()
            binding.historyRecyclerView.isVisible = false

            viewModel.onClickSearch(requireActivity(), keyword) { isSuccessful ->
               // Log.e("viewModelMountainArray", viewModel.mountainArray.toString())
                if(viewModel.mountainArray.isNotEmpty()) {
                    (requireActivity() as MainActivity).supportFragmentManager.fragments.find { it is RecommendCourseFragment }
                        ?.let {
                            (it as RecommendCourseFragment).nextStepSearch(
                                isSuccessful,
                                viewModel.mountainArray
                            )
                        }
                }
                else{
                    Toast.makeText(requireContext(),"해당 검색어에 대한 결과가 없습니다.",Toast.LENGTH_SHORT).show()
                }
                findKeywordInHistory(keyword, callback = { hasKeyword->
                   if(hasKeyword){
                       deleteSearchKeyword(keyword)
                       saveSearchKeyword(keyword)
                   }else{
                       saveSearchKeyword(keyword)
                   }
                })



            }
        }
    }

    private fun findKeywordInHistory(keyword: String, callback: (Boolean) -> Unit) {
        var hasKeyword = false
        Thread{
            val keywords = db.historyDao().find(keyword = keyword)
             if(keywords.isNotEmpty()){
                 hasKeyword = true
                 callback(hasKeyword)
                 return@Thread
             }else{
                 hasKeyword = false
                 callback(hasKeyword)
                 return@Thread
             }
        }.start()

    }
    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword = keyword)
            showHistoryView()
        }.start()
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            requireActivity().runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
                binding.historyRecyclerView.isVisible = true
            }
        }.start()

    }

    private fun hideHistoryView() {
        requireActivity().runOnUiThread {
            binding.historyRecyclerView.isVisible = false
        }
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    override fun FragmentSearchBinding.onBind() {
        vi = this@SearchFragment
        vm = viewModel
        viewModel.bindLifecycle(requireActivity() as MainActivity)
    }


}
