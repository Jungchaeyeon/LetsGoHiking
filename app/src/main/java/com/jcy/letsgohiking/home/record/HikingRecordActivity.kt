package com.jcy.letsgohiking.home.record


import android.app.Activity
import android.os.Bundle
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.SampleToast
import com.jcy.letsgohiking.databinding.ActivityHikingRecordBinding
import com.jcy.letsgohiking.home.record.model.Record
import com.jcy.letsgohiking.home.record.model.RecordViewModel
import com.jcy.letsgohiking.home.tab2.AppDatabase
import com.jcy.letsgohiking.home.tab2.getAppDatabaseRecord
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class HikingRecordActivity : BaseDataBindingActivity<ActivityHikingRecordBinding>(R.layout.activity_hiking_record) {
    private lateinit var db: AppDatabase
    private val viewModel by viewModel<RecordViewModel>()
    private lateinit var mntnName: String

    override fun setupProperties(bundle: Bundle?) {
        super.setupProperties(bundle)
        mntnName = bundle?.getSerializable(ActivityNavigator.KEY_DATA) as String
    }
    override fun ActivityHikingRecordBinding.onBind() {
        vi = this@HikingRecordActivity
        vm = viewModel
        viewModel.bindLifecycle(this@HikingRecordActivity)
        db = getAppDatabaseRecord(this@HikingRecordActivity)
        initViews()
        getRecordData(activity = this@HikingRecordActivity)

    }
    private fun initViews(){
        viewModel.recordTitle.value = mntnName
        viewModel.recordDate.value = nowDate()
    }
    fun getRecordData(activity: Activity){
            viewModel.getRecordData(activity, mntnName)
    }
    fun insertRecord(activity: Activity, record: Record){
        viewModel.insertRecord(activity, record){
            if(it) SampleToast.createToast(activity,"산행 기록이 저장되었습니다:)")?.show()
            else SampleToast.createToast(activity,"산행 기록이 저장되지 않았습니다:)")?.show()
        }
    }
    fun deleteRecord(activity: Activity, recordId: String){
        viewModel.deleteRecord(activity, recordId){
            if(it) SampleToast.createToast(activity,"산행 기록이 삭제되었습니다:)")?.show()
            else SampleToast.createToast(activity,"산행 기록이 삭제되지 않았습니다:)")?.show()
        }
    }
    fun onClickBack(){
        finish()
    }
    fun onClickSave(){
        val content = binding.content.text.toString().trim()
        if(content.isEmpty()) return
        val date = nowDate()
        val record = Record(mntnName,date,content)
        insertRecord(this, record)
    }
    fun nowDate(): String{
        val date = System.currentTimeMillis()
        val formatDate = SimpleDateFormat("yyyy년MM월dd일 HH:mm:ss").format(date)
        return formatDate
    }
}