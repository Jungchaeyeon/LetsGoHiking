package com.jcy.letsgohiking.home.tab1

import androidx.core.view.isVisible
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import com.jcy.letsgohiking.ext.getProfileImage
import com.jcy.letsgohiking.home.record.DatePickerBottomsheetFragment
import com.jcy.letsgohiking.home.record.adapter.RecordsAdapter
import com.jcy.letsgohiking.home.record.model.Record
import com.jcy.letsgohiking.home.record.model.RecordViewModel
import com.jcy.letsgohiking.repository.local.LocalKey
import com.jcy.letsgohiking.repository.local.RepositoryCached
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class HomeFragment : BaseDataBindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel by viewModel<HomeViewModel>()
    private val viewModelRecord by viewModel<RecordViewModel>()
    private val repositoryCached by inject<RepositoryCached>()
    private lateinit var mntnRecordList: List<Record>
    private lateinit var adapter: RecordsAdapter
    private val calendar: Calendar = Calendar.getInstance()


    companion object {
        fun getInstance() = HomeFragment()
    }

    override fun FragmentHomeBinding.onBind() {
        vi = this@HomeFragment
        vm = viewModel
        viewModel.bindLifecycle(requireActivity())
        viewModelRecord.bindLifecycle(requireActivity())
        initViews()
        initAdapters()
    }

     fun initViews() {
        val uid = repositoryCached.getUserId()
        val uClass = repositoryCached.getUserClass()
        viewModel.liveClass.value = "등급 : "+ setHikingClass(uClass) //등급
        if (uid != "") { //프로필 이미지 가져오기
            binding.userProfileImage.getProfileImage(repositoryCached.getUserId())
        }
        viewModelRecord.getYearMatchRecordData(requireActivity(), calendar.get(Calendar.YEAR).toString()) {
            if (it) {
                nextStepAdapterInit()
            }
        }
    }
    private fun nextStepAdapterInit(){
        val recordList = viewModelRecord.recordMutableList.value
        if ( recordList!= null) {
            if (recordList!!.isNotEmpty()) {
                binding.noRecordNotice.isVisible = false
                if(recordList.size in 5..15) { //등급 세팅
                    viewModel.liveClass.value = "등급 : "+ setHikingClass(1)
                    repositoryCached.setValue(LocalKey.USERCLASS,1)
                }
                else if(recordList.size>15) {
                    viewModel.liveClass.value = "등급 : "+ setHikingClass(2)
                    repositoryCached.setValue(LocalKey.USERCLASS,2)
                }
            }
            mntnRecordList = viewModelRecord.recordMutableList.value!!
            adapter.submitList(mntnRecordList.reversed())
        }
    }

    private fun initAdapters() {
        adapter = RecordsAdapter {
            ActivityNavigator.with(this).hikingrecord(it.mntnName).start()
        }
        binding.hikingRecordRv.adapter = adapter
    }

    fun onClickYearPicker() {
        YearPickerBottomsheetFragment.getInstance()
            .setOnClickOk { date ->
              viewModel.liveYearList.value = date
                viewModelRecord.getYearMatchRecordData(requireActivity(),date.substring(0,4)){
                    if(it) nextStepAdapterInit()
                }
            }.show(requireActivity().supportFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        initViews()
    }
    private fun setHikingClass(classNum: Int): String{
        return when(classNum){
            0 -> "등린이 (초보)"
            1 -> "등산러"
            2 -> "프로등산러"
            else -> "등린이"
        }
    }
}