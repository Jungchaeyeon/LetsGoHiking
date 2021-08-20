package com.jcy.letsgohiking.home.record.model

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.home.tab2.AppDatabase
import com.jcy.letsgohiking.home.tab2.getAppDatabaseRecord
import com.jcy.letsgohiking.util.Log

class RecordViewModel : BaseViewModel() {

    var recordDate = MutableLiveData<String>()
    var recordContent = MutableLiveData<String>()
    var recordTitle = MutableLiveData<String>()
    var hikingDate = MutableLiveData<String>()
    var hikingWith = MutableLiveData<String>()
    var hikingImg = MutableLiveData<String>()
    val recordMutableList = MutableLiveData<List<Record>>()

    private lateinit var db: AppDatabase


    fun insertRecord(activity: Activity, record: Record, respon: (Boolean) -> Unit) {
        db = getAppDatabaseRecord(activity)
        val list = db.recordDao().getRecordData()

        if (list.isEmpty()) {
            db.recordDao().insertRecord(record)
            respon.invoke(true)
        } else {
            val recordItem : Record?= db.recordDao().find(record.mntnName)
            if(recordItem!=null){
                db.recordDao().updateRecord(record)
                respon.invoke(true)
            }else{
                db.recordDao().insertRecord(record)
                respon.invoke(true)
                }
            }
        }



    fun getAllRecordData(activity: Activity, respon: (Boolean) -> Unit) {
        db = getAppDatabaseRecord(activity)
        try {
            Thread {
                val recordD = db.recordDao().getRecordData()

                activity.runOnUiThread {
                    recordMutableList.value = recordD
                    respon.invoke(true)
                }

            }.start()
        } catch (e: Exception) {
            Log.e("error get Record Data", e.toString())
            respon.invoke(false)
        }
    }

    fun getRecordData(activity: Activity, mntnName: String, respon: (Boolean) -> Unit) {
        db = getAppDatabaseRecord(activity)
        try {
            Thread {
                val recordDB: List<Record> = db.recordDao().getRecordData()
                activity.runOnUiThread {
                    recordDB.forEach { record ->
                        if (record.mntnName == mntnName) {
                            recordDate.value = record.rid
                            hikingDate.value = record.hikingDate
                            hikingWith.value = record.hikingWith
                            hikingImg.value = record.hikingImg
                            recordContent.value = record.content.trim()

                        }
                    }
                    respon.invoke(true)
                }

            }.start()
        } catch (e: Exception) {
            Log.e("error get Record Data", e.toString())
            respon.invoke(false)
        }
    }

    fun deleteRecord(activity: Activity, recordId: String, respon: (Boolean) -> Unit) {
        db = getAppDatabaseRecord(activity)
        try {
            Thread {
                Runnable {
                    val recordDB = db.recordDao().delete(recordId)
                    recordDate.value = ""
                    recordContent.value = ""

                    activity.runOnUiThread {
                        respon.invoke(true)
                    }
                }
            }.start()
        } catch (e: Exception) {
            respon.invoke(false)
        }

    }

}