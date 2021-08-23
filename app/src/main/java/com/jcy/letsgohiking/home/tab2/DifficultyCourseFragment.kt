package com.jcy.letsgohiking.home.tab2

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentDifficultyCourseBinding
import com.jcy.letsgohiking.home.tab2.adapter.MountainAdapter
import kotlinx.android.synthetic.main.fragment_difficulty_course.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class DifficultyCourseFragment: BaseDataBindingFragment<FragmentDifficultyCourseBinding>(R.layout.fragment_difficulty_course) {

    private val viewModel:SearchViewModel by sharedViewModel()
    private lateinit var adapter0to500 : MountainAdapter
    private lateinit var adapter500to1000 : MountainAdapter
    private lateinit var adapter1000to1500 : MountainAdapter

    override fun FragmentDifficultyCourseBinding.onBind() {
        vm = viewModel
        viewModel.bindLifecycle(this@DifficultyCourseFragment)

        initProgressBar()
        adapter0to500 = MountainAdapter(callback = { mntnItem->
            context?.let {
                ActivityNavigator.with(it).todetailmountainpage(mntnItem).start()
            }
        })
        adapter500to1000 = MountainAdapter(callback = { mntnItem->
            context?.let {
                ActivityNavigator.with(it).todetailmountainpage(mntnItem).start()
            }
        })
        adapter1000to1500 = MountainAdapter(callback = { mntnItem->
            context?.let {
                ActivityNavigator.with(it).todetailmountainpage(mntnItem).start()
            }
        })

        binding.let {
            hikingByDifficulty0to500.layoutManager = GridLayoutManager(requireActivity(),2)
            hikingByDifficulty0to500.adapter = adapter0to500
            hikingByDifficulty500to1000.layoutManager = GridLayoutManager(requireActivity(),2)
            hikingByDifficulty500to1000.adapter = adapter500to1000
            hikingByDifficulty1000to1500.layoutManager = GridLayoutManager(requireActivity(),2)
            hikingByDifficulty1000to1500.adapter = adapter1000to1500
        }


        //난이도 별 산 recyclerView연결
        val url = "http://openapi.forest.go.kr/openapi/service/trailInfoService/getforeststoryservice?&numOfRows=100&ServiceKey=" + getString(R.string.getMountains_client_id)
        activity?.let {
            getMountainsByDifficulty(it,url)
            showProgress(true)
        }
    }
    private fun initProgressBar(){
        binding.progressBar.setAnimation(R.raw.progressbar_bg)
        showProgress(true)
    }
    private fun showProgress(isShow: Boolean){
        if(isShow) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }
    private fun getMountainsByDifficulty(activity: Activity, url: String){
        viewModel.getMountainsByDifficulty(activity, url){ isSuccessful ->
            if(isSuccessful){
                adapter0to500.submitList(viewModel.mountainList0to500)
                adapter500to1000.submitList(viewModel.mountainList500to1000)
                adapter1000to1500.submitList(viewModel.mountainList1000to1500)
                showProgress(false)
            }
            else{
                Toast.makeText(requireContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                showProgress(false)
            }
        }
    }
    companion object {
        fun getInstance() = DifficultyCourseFragment()
    }
}