package com.jcy.letsgohiking.home.tab2

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdh.base.fragment.BaseDataBindingFragment
import com.hdh.base.recycler.BaseDataBindingRecyclerViewAdapter
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentRecommendCourseBinding
import com.jcy.letsgohiking.databinding.ItemAreaKeywordBinding
import com.jcy.letsgohiking.home.tab2.adapter.MountainAdapter
import com.jcy.letsgohiking.home.tab2.model.Area
import com.jcy.letsgohiking.home.tab2.model.MountainItem
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RecommendCourseFragment :
    BaseDataBindingFragment<FragmentRecommendCourseBinding>(R.layout.fragment_recommend_course) {


    private lateinit var adapter: MountainAdapter
    private val viewModel: SearchViewModel by sharedViewModel()
    private var isSearchResult = false
    private var url = ""
    private var itemAreaBinding: ItemAreaKeywordBinding? = null

    override fun FragmentRecommendCourseBinding.onBind() {
        vm = viewModel

        initAdapter()
        initProgressBar()
        initAreaRecyclerView()

        binding.rvAreakeyword.run {
            adapter = BaseDataBindingRecyclerViewAdapter<Area>()
                .setItemViewType { item, position, isLast ->
                    // if (position == 0) 0 else 1
                    if(position ==0) 0 else 0

                }
                .addViewType(
                    BaseDataBindingRecyclerViewAdapter.MultiViewType<Area, ItemAreaKeywordBinding>(R.layout.item_area_keyword) {
                        vi = this@RecommendCourseFragment
                        item = it
                        itemAreaBinding = this
                    })
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.requestAreaKeywordData()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSearchResult) {

            url =
                "http://openapi.forest.go.kr/openapi/service/trailInfoService/getforeststoryservice?&numOfRows=50&ServiceKey=" + getString(
                    R.string.getMountains_client_id
                )
            activity?.let {
                getMountains(it, url, "01") //서울로 초기화
                showProgress(true)
            }
        }
    }
    private fun initAreaRecyclerView(){


        binding.rvAreakeyword.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if(e.action == MotionEvent.ACTION_MOVE){}
                else{
                    var child = rv.findChildViewUnder(e.x, e.y)
                    if(child != null){
                        var position = rv.getChildAdapterPosition(child)
                        Log.e("position", position.toString())
                        var view = rv.layoutManager?.findViewByPosition(position)
                        view?.isSelected = true
                        for(i in 0..rv.adapter!!.itemCount){
                            var otherView = rv.layoutManager?.findViewByPosition(i)
                            if(otherView != view){
                                otherView?.isSelected = false
                            }
                            else{

                            }
                        }
                    }
                }

                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        }
        )
    }

    private fun initProgressBar() {
        binding.progressBar.setAnimation(R.raw.progressbar_bg)
        showProgress(true)
    }

    private fun showProgress(isShow: Boolean) {
        binding.progressBar.isVisible = isShow
    }

    private fun initAdapter() {
        adapter = MountainAdapter(callback = { mntnItem ->
            context?.let {
                ActivityNavigator.with(it).todetailmountainpage(mntnItem).start()
            }
        })
        viewModel.bindLifecycle(requireActivity())
        binding.lv.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.lv.adapter = adapter
    }

    private fun getMountains(activity: Activity, url: String, area: String) {
        viewModel.getMountains(activity, url, area) { isSuccess ->
            if (isSuccess) {
                adapter.submitList(viewModel.mountainList.value)
                adapter.notifyDataSetChanged()
                showProgress(false)
            } else {
                Toast.makeText(requireContext(), "정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                showProgress(false)
            }
        }
    }
    fun onClickItem(view: View, area: String) {
        binding.progressBar.isVisible = true
        var areaCode = "-1"
        when (area) {
            "서울특별시" ->  areaCode = "01"
            "부산광역시" -> areaCode = "02"
            "대구광역시" -> areaCode = "03"
            "인천광역시" -> areaCode = "04"
            "광주광역시" -> areaCode = "05"
            "대전광역시" -> areaCode = "06"
            "울산광역시" -> areaCode = "07"
            "경기도" -> areaCode = "08"
            "강원도" -> areaCode = "09"
            "충청북도" -> areaCode = "10"
            "충청남도" -> areaCode = "11"
            "전라북도" -> areaCode = "12"
            "전라남도" -> areaCode = "13"
            "경상북도" -> areaCode = "14"
            "경상남도" -> areaCode = "15"
            "제주도" -> areaCode = "16"
            else -> "16"
        }
        Log.e("클릭?", "${areaCode}로 확인")
        getMountains(requireActivity(), url, areaCode)
    }

    companion object {
        fun getInstance() = RecommendCourseFragment()
    }

}



