package com.jcy.letsgohiking.home.tab1

import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentHomeBinding
import com.jcy.letsgohiking.ext.getProfileImage
import com.jcy.letsgohiking.repository.local.RepositoryCached
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class HomeFragment : BaseDataBindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel by viewModel<HomeViewModel>()
    private val repositoryCached by inject<RepositoryCached>()
    companion object {
        fun getInstance() = HomeFragment()
    }

    override fun FragmentHomeBinding.onBind() {
        vi = this@HomeFragment
        vm = viewModel
        viewModel.bindLifecycle(requireActivity())
        initViews()
    }
    fun initViews(){
        val uid  =  repositoryCached.getUserId()
        if(uid !=""){
            binding.userProfileImage.getProfileImage(repositoryCached.getUserId())
        }
    }
}