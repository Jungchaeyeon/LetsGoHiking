package com.jcy.letsgohiking.home.tab1

import androidx.lifecycle.MutableLiveData
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.repository.local.RepositoryCached
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    val repositoryCached: RepositoryCached
): BaseViewModel() {
    val liveYearList =  MutableLiveData<String>().apply { value = today() }
    val liveName = MutableLiveData<String>().apply { value = "${repositoryCached.getUserName()}님의 등산기록" }
    val liveClass = MutableLiveData<String>().apply { value = "등급 : 등린이" }
    fun today(): String {
        val today = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy년")
        return df.format(today)
    }
    //todo 날씨 api 받아오기

}