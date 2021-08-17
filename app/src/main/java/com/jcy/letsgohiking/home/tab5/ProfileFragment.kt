package com.jcy.letsgohiking.home.tab5

import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentProfileBinding
import com.jcy.letsgohiking.ext.getProfileImage
import com.jcy.letsgohiking.repository.local.RepositoryCached
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class ProfileFragment : BaseDataBindingFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val viewModel by viewModel<MoreInfoViewmodel>()
    private val repositoryCached by inject<RepositoryCached>()
    companion object {
        fun getInstance() = ProfileFragment()
    }
    override fun FragmentProfileBinding.onBind() {
        vi = this@ProfileFragment
        vm = viewModel
        viewModel.bindLifecycle(this@ProfileFragment)
        initViews()
    }
    fun onClickLogout(){
        if(repositoryCached.getLoginType() == "K") {
            viewModel.kakaoLogout() { isSuccessful ->
                if (isSuccessful) {
                    ActivityNavigator.with(requireActivity()).login().start()
                }
            }
        }
        else{
            viewModel.googleLogout(){ isSuccessful->
                if(isSuccessful){
                    ActivityNavigator.with(requireActivity()).login().start()
                }
            }
        }
    }
    fun initViews(){
        val uid = repositoryCached.getUserId()
        if(uid !=""){
            binding.profileImage.getProfileImage(repositoryCached.getUserId())
        }
    }
}