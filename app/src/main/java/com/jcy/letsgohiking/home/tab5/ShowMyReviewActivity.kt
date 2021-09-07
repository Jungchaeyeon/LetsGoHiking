package com.jcy.letsgohiking.home.tab5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.ActivityShowMyReviewBinding
import com.jcy.letsgohiking.home.tab2.ReviewViewModel
import com.jcy.letsgohiking.home.tab2.adapter.MountainReviewAdapter
import com.jcy.letsgohiking.home.tab2.model.Review
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

class ShowMyReviewActivity : BaseDataBindingActivity<ActivityShowMyReviewBinding>(R.layout.activity_show_my_review) {

    private val viewModel by viewModel<ReviewViewModel>()
    private lateinit var reviewList : ArrayList<Review>
    private lateinit var reviewAdapter: MountainReviewAdapter
    private lateinit var firestoreDB: FirebaseFirestore

    override fun ActivityShowMyReviewBinding.onBind() {
        viewModel.bindLifecycle(this@ShowMyReviewActivity)
        initAdapter()
        initView()
    }
    private fun initAdapter(){
        reviewAdapter = MountainReviewAdapter()
        binding.showMyReviewRv.adapter = reviewAdapter
    }
    private fun initView(){

    }
}