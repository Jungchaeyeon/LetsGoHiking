package com.jcy.letsgohiking.home.tab5

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdh.base.disposeOnDestroy
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.repository.local.LocalKey
import com.jcy.letsgohiking.repository.local.RepositoryCached
import com.jcy.letsgohiking.util.Log
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MoreInfoViewmodel( val repositoryCached: RepositoryCached)
    : BaseViewModel() {
    val liveName = MutableLiveData<String>().apply { value = repositoryCached.getUserName() }

    fun kakaoLogout (response:(Boolean) -> Unit){
        Thread{
            UserApiClient.rx.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("로그아웃 성공. SDK에서 토큰 삭제 됨")
                    repositoryCached.setValue(LocalKey.USERID, "")
                    repositoryCached.setValue(LocalKey.USERNAME, "")
                    response.invoke(true)
                }, { error ->
                    Log.e("로그아웃 실패. SDK에서 토큰 삭제 됨", error.toString())
                    response.invoke(false)
                }).disposeOnDestroy(this)
        }.start()
    }
    fun googleLogout(response:(Boolean) -> Unit){
        FirebaseAuth.getInstance().signOut().apply {
            response.invoke(true)
        }
        repositoryCached.setValue(LocalKey.GOOGLETOKEN,"")
        repositoryCached.setValue(LocalKey.USERID,"")
        repositoryCached.setValue(LocalKey.USERNAME,"")
    }

}