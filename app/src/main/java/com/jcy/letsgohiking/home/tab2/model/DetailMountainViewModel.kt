package com.jcy.letsgohiking.home.tab2.model

import androidx.lifecycle.MutableLiveData
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.home.tab2.MountainItem

class DetailMountainViewModel: BaseViewModel() {
    val liveMountainItem = MutableLiveData<MountainItem>()

}