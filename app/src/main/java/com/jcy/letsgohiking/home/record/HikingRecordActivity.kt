package com.jcy.letsgohiking.home.record


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
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
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*

class HikingRecordActivity : BaseDataBindingActivity<ActivityHikingRecordBinding>(R.layout.activity_hiking_record) {
    private lateinit var db: AppDatabase
    private val viewModel by viewModel<RecordViewModel>()
    private lateinit var mntnName: String
    private lateinit var hikingDate: String
//    private var hikingImg : String? = null
    private var hikingImgBitmap : Bitmap? = null

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
                        hikingImgBitmap = viewModel.hikingImgBitmap.value
                        val loadedHikingDate = viewModel.hikingDate.value

                        hikingImgBitmap?.let {
                            binding.hikingImg.setImageBitmap(hikingImgBitmap)
                            binding.hikingImg.isVisible = true
                        }

                        if(loadedHikingDate.isNullOrEmpty()){
                            hikingDate = ""
                            viewModel.hikingDate.value = "날짜를 선택해주세요."
                        }else{
                            hikingDate = viewModel.hikingDate.value.toString()
                        }
                        if(!viewModel.recordContent.value.isNullOrEmpty()){
                            binding.deleteBtn.isVisible = true
                        }
                    }
                }
                else{
                    Log.e("setHikingImageError", "이미지를 불러들어오는 데 실패했습니다.")
                }
            }

    }
    @SuppressLint("WrongConstant")
    fun startContentProvider(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, 3030)
    }
    fun insertRecord(activity: Activity, record: Record){
        viewModel.insertRecord(activity, record){
            runOnUiThread {
                if(it) SampleToast.createToast(activity,"산행 기록이 저장되었습니다:)")?.show()
                else SampleToast.createToast(activity,"산행 기록이 저장되지 않았습니다:)")?.show()
                binding.content.clearFocus()
            }
        }
    }
    fun deleteRecord(activity: Activity, recordId: String){
        BasicDialogFragment.getInstance()
            .setTitle("삭제")
            .setContent("기록을 삭제합니다.")
            .setOnClickOk {
                viewModel.deleteRecord(activity, recordId){
                    if(it) {
                        SampleToast.createToast(activity,"산행 기록이 삭제되었습니다:)")?.show()
                        onBackPressed()
                    }
                    else SampleToast.createToast(activity,"산행 기록이 삭제되지 않았습니다:)")?.show()

                }
            }.show(this.supportFragmentManager)
    }
    fun onClickBack(){
        finish()
    }
    fun onClickDelete(){
        deleteRecord(this,mntnName)
    }
    fun onClickSave(){
        val content = binding.content.text.toString().trim()
        if(content.isEmpty()) {
            Snackbar.make(this.contentLayout ,"내용을 작성해주세요.",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(hikingDate.isEmpty()){
            Snackbar.make(this.contentLayout ,"날짜를 선택해주세요.",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(mntnName.isEmpty()) return
        val whoHikingWith = binding.hikingWith.text.toString().trim()
        val writingTime = nowDate()
        val mntnName = viewModel.recordTitle.value.toString()
        hikingDate = viewModel.hikingDate.value.toString()
        hikingImgBitmap?.let {
            val record = Record(mntnName,writingTime,hikingDate.substring(6,10),hikingDate,whoHikingWith,hikingImgBitmap,content)
            insertRecord(this, record)
        }
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
    fun getImageAndPermission( uri: Uri){
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        val bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
        }else{
            MediaStore.Images.Media.getBitmap(contentResolver,uri)
        }

        binding.hikingImg.setImageBitmap(bitmap)
        hikingImgBitmap = bitmap
        binding.hikingImg.isVisible = true
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
                    getImageAndPermission(uri)
                }else{
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            3030 ->{
                val uri = data?.data
                if(uri != null){
                    getImageAndPermission(uri)
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