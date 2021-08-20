package com.jcy.letsgohiking.home.record


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.SampleToast
import com.jcy.letsgohiking.databinding.ActivityHikingRecordBinding
import com.jcy.letsgohiking.home.record.model.Record
import com.jcy.letsgohiking.home.record.model.RecordViewModel
import com.jcy.letsgohiking.home.tab2.AppDatabase
import com.jcy.letsgohiking.home.tab2.getAppDatabaseRecord
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.activity_hiking_record.*
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class HikingRecordActivity : BaseDataBindingActivity<ActivityHikingRecordBinding>(R.layout.activity_hiking_record) {
    private lateinit var db: AppDatabase
    private val viewModel by viewModel<RecordViewModel>()
    private lateinit var mntnName: String
    private lateinit var hikingDate: String
    private var hikingImg : String? = null

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
            viewModel.getRecordData(activity, mntnName){
                if(it){
                    runOnUiThread {
                        hikingImg = viewModel.hikingImg.value
                        val loadedHikingDate = viewModel.hikingDate.value
                        if(hikingImg.isNullOrEmpty()) {
                            binding.hikingImg.isVisible = false
                        } else{
                            binding.hikingImg.setImageURI(hikingImg?.toUri())
                            binding.hikingImg.isVisible = true
                        }
                        if(loadedHikingDate.isNullOrEmpty()){
                            hikingDate = "날짜를 선택해주세요."
                            viewModel.hikingDate.value = hikingDate
                        }
                    }
                }
                else{
                    Log.e("setHikingImageError", "이미지를 불러들어오는 데 실패했습니다.")
                }
            }

    }
    fun startContentProvider(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 3030)
    }
    fun insertRecord(activity: Activity, record: Record){
        viewModel.insertRecord(activity, record){
            runOnUiThread {
                if(it) SampleToast.createToast(activity,"산행 기록이 저장되었습니다:)")?.show()
                else SampleToast.createToast(activity,"산행 기록이 저장되지 않았습니다:)")?.show()
            }
        }
    }
    fun deleteRecord(activity: Activity, recordId: String){
        viewModel.deleteRecord(activity, recordId){
            runOnUiThread {
                if(it) SampleToast.createToast(activity,"산행 기록이 삭제되었습니다:)")?.show()
                else SampleToast.createToast(activity,"산행 기록이 삭제되지 않았습니다:)")?.show()
            }
        }
    }
    fun onClickBack(){
        finish()
    }
    fun onClickSave(){
        val content = binding.content.text.toString().trim()
        if(content.isEmpty()) {
            Snackbar.make(this.contentLayout ,"내용을 작성해주세요.",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(mntnName.isEmpty()) return
        val whoHikingWith = binding.hikingWith.text.toString().trim()
        val writingTime = nowDate()
        val mntnName = viewModel.recordTitle.value.toString()
        Log.e("mntnName", mntnName)
        hikingDate = viewModel.hikingDate.value.toString()
        val record = Record(mntnName,writingTime,hikingDate,whoHikingWith,hikingImg ?:"",content)
        insertRecord(this, record)
    }
    fun onClickDatePicker(){
        DatePickerBottomsheetFragment.getInstance()
            .setOnClickOk { date ->
                viewModel.hikingDate.value = date
                hikingDate = date
            }.show(supportFragmentManager)
    }
    fun onClickGetImage(){
        getPermission()
    }
    fun nowDate(): String{
        val date = System.currentTimeMillis()
        val formatDate = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss").format(date)
        return formatDate
    }
    private fun getPermission(){
        when{
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            ->{
                startContentProvider()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)->{
                showPermissionContextPopup()
            }
            else->{
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1010)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1010 ->{
                if(grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    startContentProvider()
                }
                else{
                    Toast.makeText(this, "권한을 거부하셨습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK){
            return
        }
        when(requestCode){
            2020 ->{
                val uri = data?.data
                if(uri != null){
                    binding.hikingImg.setImageURI(uri)
                    hikingImg = uri.toString()
                    binding.hikingImg.isVisible = true
                }else{
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            3030 ->{
                val uri = data?.data
                if(uri != null){
                    Log.e("uri",uri.toString())
                    binding.hikingImg.setImageURI(uri)
                    hikingImg = uri.toString()
                    binding.hikingImg.isVisible = true
                }
                else{
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else->{
            Toast.makeText(this, "not 2020 사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionContextPopup(){
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1010)
            }
            .create()
            .show()
    }

}