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
                getMountains(it, url, "01") //????????? ?????????
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
                Toast.makeText(requireContext(), "????????? ???????????? ??? ??????????????????.", Toast.LENGTH_SHORT).show()
                showProgress(false)
            }
        }
    }
    fun onClickItem(view: View, area: String) {
        binding.progressBar.isVisible = true
        var areaCode = "-1"
        when (area) {
            "???????????????" ->  areaCode = "01"
            "???????????????" -> areaCode = "02"
            "???????????????" -> areaCode = "03"
            "???????????????" -> areaCode = "04"
            "???????????????" -> areaCode = "05"
            "???????????????" -> areaCode = "06"
            "???????????????" -> areaCode = "07"
            "?????????" -> areaCode = "08"
            "?????????" -> areaCode = "09"
            "????????????" -> areaCode = "10"
            "????????????" -> areaCode = "11"
            "????????????" -> areaCode = "12"
            "????????????" -> areaCode = "13"
            "????????????" -> areaCode = "14"
            "????????????" -> areaCode = "15"
            "?????????" -> areaCode = "16"
            else -> "16"
        }
        Log.e("???????", "${areaCode}??? ??????")
        getMountains(requireActivity(), url, areaCode)
    }

    companion object {
        fun getInstance() = RecommendCourseFragment()
    }

}



