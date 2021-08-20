package com.jcy.letsgohiking.home.tab1

import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import com.jcy.letsgohiking.ext.getProfileImage
import com.jcy.letsgohiking.home.record.adapter.RecordsAdapter
import com.jcy.letsgohiking.home.record.model.Record
import com.jcy.letsgohiking.home.record.model.RecordViewModel
import com.jcy.letsgohiking.repository.local.RepositoryCached
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class HomeFragment : BaseDataBindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel by viewModel<HomeViewModel>()
    private val viewModelRecord by viewModel<RecordViewModel>()
    private val repositoryCached by inject<RepositoryCached>()
    private lateinit var mntnRecordList : List<Record>
    private lateinit var adapter : RecordsAdapter


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
    fun initViews(){
        val uid  =  repositoryCached.getUserId()
        if(uid !=""){
            binding.userProfileImage.getProfileImage(repositoryCached.getUserId())
        }
        viewModelRecord.getAllRecordData(requireActivity()){
            if(it){
                Log.e("data확인 메인", viewModelRecord.recordMutableList.value.toString())
                if(viewModelRecord.recordMutableList.value != null){
                    mntnRecordList = viewModelRecord.recordMutableList.value!!
                    adapter.submitList(mntnRecordList)
                }
            }
        }
    }
    private fun initAdapters(){
        adapter = RecordsAdapter {
            ActivityNavigator.with(this).hikingrecord(it.mntnName).start()
        }
        binding.hikingRecordRv.adapter = adapter
    }
}