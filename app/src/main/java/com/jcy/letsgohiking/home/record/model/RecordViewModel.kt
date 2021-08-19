package com.jcy.letsgohiking.home.record.model

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.home.tab2.AppDatabase
import com.jcy.letsgohiking.home.tab2.getAppDatabaseRecord
import com.jcy.letsgohiking.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecordViewModel: BaseViewModel() {

    var recordDate = MutableLiveData<String>()
    var recordContent = MutableLiveData<String>()
    var recordTitle = MutableLiveData<String>()
    var recordImgMutableList = MutableLiveData<ArrayList<String>>()
    var recordImgList = ArrayList<String>()

    private lateinit var db: AppDatabase


    fun setRecordImage(array: ArrayList<String>){
        recordImgMutableList.postValue(array)
    }
    fun insertRecord(activity: Activity,record: Record,  respon: (Boolean) -> Unit){
        db =getAppDatabaseRecord(activity)
        try {
            Thread{
                db.recordDao().insertRecord(record)
                activity.runOnUiThread {
                    respon.invoke(true)
                }
            }.start()
        }catch (e: Exception){
            respon.invoke(false)
        }
    }
    fun getRecordData(activity: Activity, mntnName: String){
        db =getAppDatabaseRecord(activity)
        try {
            Thread{
                val recordDB :List<Record> = db.recordDao().getRecordData()
                activity.runOnUiThread {
                    recordDB.forEach { record ->
                        if(record.mntnName == mntnName){
                            recordDate.value = record.rid
                            recordContent.value = record.content.trim()
                        }
                    }
                }
               // recordImgList = recordDB.contentImgUrl
               // setRecordImage(recordImgList)

            }.start()
        }catch (e: Exception){
            Log.e("error get Record Data", e.toString())
        }
    }
    fun deleteRecord(activity: Activity,recordId: String, respon: (Boolean) -> Unit){
        db =getAppDatabaseRecord(activity)
        try {
            Thread{ Runnable {
                val recordDB =db.recordDao().delete(recordId)
                recordDate.value = ""
                recordContent.value =""
               // recordImgList.clear()
               // setRecordImage(recordImgList)
                activity.runOnUiThread {
                    respon.invoke(true)
                }
            }}.start()
        }catch (e : Exception){
            respon.invoke(false)
        }

    }

}