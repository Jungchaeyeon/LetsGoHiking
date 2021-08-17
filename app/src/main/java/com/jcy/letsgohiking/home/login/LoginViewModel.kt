package com.jcy.letsgohiking.home.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.hdh.base.disposeOnDestroy
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.home.login.model.User
import com.jcy.letsgohiking.repository.ApiRepository
import com.jcy.letsgohiking.repository.local.LocalKey
import com.jcy.letsgohiking.repository.local.RepositoryCached
import com.jcy.letsgohiking.util.Log
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.rx
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers


class LoginViewModel(
    val apiRepository: ApiRepository,
    val repositoryCached: RepositoryCached
) : BaseViewModel() {


    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient? = null

    fun getGoogleLoginClient(activity: Activity): GoogleSignInClient? {
        if (googleSignInClient == null) {
            googleSignInClient = GoogleSignIn.getClient(
                activity, GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            )
        }

        return googleSignInClient
    }

    fun onRequestLoginWithGoogle(
        activity: Activity,
        data: Intent?,
        response: (success: Boolean) -> Unit
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(data).run {
            try {
                getResult(ApiException::class.java)?.let { googleSignInAccount ->
                    Log.e(googleSignInAccount.idToken)
                    auth.signInWithCredential(
                        GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken,
                            null
                        )
                    ).addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            if (auth.currentUser == null || googleSignInAccount.id == null) {
                                response.invoke(false)
                                return@addOnCompleteListener
                            }

                            auth.currentUser?.let {
                                Log.e(googleSignInAccount.id)
                                Log.e(googleSignInAccount.idToken, "AccessToken")
                                Log.e(googleSignInAccount.isExpired.toString(), "AccessToken")

                                val userId = googleSignInAccount.id
                                val userName = googleSignInAccount.displayName ?:""
                                val profileUrl = googleSignInAccount.photoUrl ?:""

                                repositoryCached.setValue(LocalKey.GOOGLETOKEN, googleSignInAccount.idToken?.toString())
                                repositoryCached.setValue(LocalKey.USERID, userId)
                                repositoryCached.setValue(LocalKey.USERNAME, userName)

                                if(userId!=null && userId.isNotEmpty()){
                                    val userModel = User(userId, userName, profileUrl.toString())
                                    saveUser(userModel)
                                }
                                response.invoke(true)
                            }
                        } else {
                            response.invoke(false)
                        }
                    }
                }
            } catch (e: ApiException) {
                response.invoke(false)
                e.printStackTrace()
            }
        }
    }
    fun checkGoogleLoginSession( response: (Boolean) -> Unit){
        if(repositoryCached.getGoogleToken().isNotEmpty()){
            response.invoke(true)
        }else{
            response.invoke(false)
        }
    }
    fun checkKakaoLoginSession(response: (Boolean) -> Unit){
        Thread{
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.rx.accessTokenInfo()
                    .subscribe({ tokenInfo ->
                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                        Log.e(
                            "token Info", "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초"
                        )
                        response.invoke(true)
                    }, { error ->
                        if (error != null) {
                            if (error is KakaoSdkError) {
                                //로그인 필요
                                response.invoke(false)
                            } else {//기타 에러
                                Log.e("error in Login", error.toString())
                            }
                        } else {
                            //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                            response.invoke(true)
                        }
                    }).disposeOnDestroy(this)
            }
            else {
                //로그인 필요
                response.invoke(false)
            }
        }.start()

    }

    fun onClickKakaoLogin(context: Context, response: (Boolean) -> Unit) {
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        Single.just(LoginClient.instance.isKakaoTalkLoginAvailable(context))
            .flatMap { available ->
                if (available) LoginClient.rx.loginWithKakaoTalk(context)
                else LoginClient.rx.loginWithKakaoAccount(context)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->

                Log.e("일단 카카오 쪽 로그인 성공 ${token.accessToken}")
                Log.e("refresh token", token.refreshToken.toString())
                Log.e("refresh token expiration", token.refreshTokenExpiresAt.toString())

                //카카오에서 user정보 가져오기
                getUserInfo(response)

            }, { error ->
                error.printStackTrace()
                response.invoke(false)
            }).disposeOnDestroy(this)

    }
    fun getUserInfo(response: (Boolean) -> Unit){
        UserApiClient.instance.me { user, error ->

            val uid = user?.id
            val uNickname = user?.kakaoAccount?.profile?.nickname ?: ""
            val profileImgUrl = user?.kakaoAccount?.profile?.profileImageUrl ?: ""
            Log.e("회원번호: ${user?.id}")
            Log.e("닉네임: ${user?.kakaoAccount?.profile?.nickname}")
            Log.e("프로필 링크: ${user?.kakaoAccount?.profile?.profileImageUrl}")
            Log.e("썸네일 링크: ${user?.kakaoAccount?.profile?.thumbnailImageUrl}")

            repositoryCached.setValue(LocalKey.USERID, uid)
            repositoryCached.setValue(LocalKey.USERNAME, uNickname)

            val userModel = User(uid.toString(), uNickname, profileImgUrl)
            saveUser(userModel)
        }
        response.invoke(true)
    }
    fun saveUser(user: User){
        val fbFirestore = FirebaseFirestore.getInstance()
        fbFirestore.collection("users").document(user.userId).set(user)
    }

}