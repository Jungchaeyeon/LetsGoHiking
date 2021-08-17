package com.jcy.letsgohiking.home.login

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.ActivityLoginBinding
import com.jcy.letsgohiking.repository.local.LocalKey
import com.jcy.letsgohiking.repository.local.RepositoryCached
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseDataBindingActivity<ActivityLoginBinding>(R.layout.activity_login)  {

    private val viewModel by viewModel<LoginViewModel>()
    private val repositoryCached by inject<RepositoryCached>()
    private lateinit var functions: FirebaseFunctions
    private val requestGoogleAuth = 9001

    @RequiresApi(Build.VERSION_CODES.P)
    override fun ActivityLoginBinding.onBind() {
        vi= this@LoginActivity
        viewModel.bindLifecycle(this@LoginActivity)

        functions = Firebase.functions
        initViews()
        checkLoginSession()

    }

    fun initViews(){
        binding.lottieLogin.setAnimation(R.raw.login_ani)
    }
    fun checkLoginSession(){
        if(repositoryCached.getLoginType()=="K"){
            viewModel.checkKakaoLoginSession(){ isAvailable->
                if(isAvailable){ //토큰 유효
                    ActivityNavigator.with(this).main().start()
                }
            }
        }
        else{
            viewModel.checkGoogleLoginSession(){isAvailable->
                if(isAvailable){//토큰 유효
                    ActivityNavigator.with(this).main().start()
                }
            }
        }
    }
    fun onClickKakaoLogin(){
        viewModel.onClickKakaoLogin(this) {
            if(it){
                ActivityNavigator.with(this).main().start()
            }
        }
        repositoryCached.setValue(LocalKey.LOGINTYPE, "K")
    }
    fun onClickGoogleLogin(){
            startActivityForResult(
                viewModel.getGoogleLoginClient(this)?.signInIntent,
                requestGoogleAuth
            )
        repositoryCached.setValue(LocalKey.LOGINTYPE, "G")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestGoogleAuth) {
           viewModel.onRequestLoginWithGoogle(this,data){
                if(it){
                    ActivityNavigator.with(this).main().start()
                }
           }
           }
    }
}