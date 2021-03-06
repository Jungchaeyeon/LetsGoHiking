package com.jcy.letsgohiking.home.tab2.review

import android.app.AlertDialog
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.SampleToast
import com.jcy.letsgohiking.databinding.ActivityMountainReviewBinding
import com.jcy.letsgohiking.home.tab2.adapter.MountainReviewAdapter
import com.jcy.letsgohiking.home.tab2.model.Review
import com.jcy.letsgohiking.repository.local.RepositoryCached
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.activity_hiking_record.*
import kotlinx.android.synthetic.main.activity_mountain_review.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class MountainReviewActivity : BaseDataBindingActivity<ActivityMountainReviewBinding>(R.layout.activity_mountain_review){
    private val viewModel by viewModel<ReviewViewModel>()
    private val repositoryCached by inject<RepositoryCached>()
    private lateinit var reviewAdapter: MountainReviewAdapter
    private lateinit var mntnName:String
    private lateinit var firestoreDB: FirebaseFirestore
    private lateinit var reviewList : ArrayList<Review>
    private lateinit var reviewContent : String

    override fun ActivityMountainReviewBinding.onBind() {
        vi = this@MountainReviewActivity
        vm = viewModel
        viewModel.bindLifecycle(this@MountainReviewActivity)

        reviewList = ArrayList()
        initAdapters()
        initViews()
        getMountainReviews()
    }

    override fun setupProperties(bundle: Bundle?) {
        super.setupProperties(bundle)
        mntnName = bundle?.getSerializable(ActivityNavigator.KEY_DATA) as String
    }
    private fun initViews(){
        reviewContent = getReviewFromEditText()
    }
    private fun initAdapters(){
        reviewAdapter = MountainReviewAdapter()
        binding.reviewRecyclerView.adapter = reviewAdapter

    }
    fun getMountainReviews(){
        firestoreDB = FirebaseFirestore.getInstance()
        viewModel.getMountainReview(mntnName){
            if(it){
                reviewList = viewModel.reviewList
                if(reviewList.size >0) {
                    binding.noReviewNotice.isVisible = false
                    reviewAdapter.submitList(reviewList) }
                    reviewAdapter.notifyDataSetChanged()

                reviewList.map{ myReview->
                     if(myReview.userId == repositoryCached.getUserId()) {
                         binding.myReview.text = myReview.review
                         binding.removeReviewBtn.isVisible = true
                     }
                }

                }else{
                    binding.noReviewNotice.isVisible = true
                }
            }
    }
    fun onClickRemoveMyReview()= with(binding){
        alertDeleteMyReview{
            val review = Review( repositoryCached.getUserName(),repositoryCached.getUserId(),reviewContent)
            viewModel.deleteMountainReview(mntnName, review.writer){ isSuccessful ->
                if(isSuccessful) SampleToast.createToast(this@MountainReviewActivity, "내 리뷰가 삭제되었습니다.")?.show()
                else SampleToast.createToast(this@MountainReviewActivity, "리뷰 삭제에 실패했습니다.")?.show()
            }
            myReview.text ="등록된 리뷰가 없습니다"
            removeReviewBtn.isGone = true
        }
    }
    private fun alertDeleteMyReview(afterAction: () -> Unit){
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setTitle(getString(R.string.delete_my_review))
            .setMessage(getString(R.string.delete_my_review_message))
            .setPositiveButton(getString(R.string.confirm)){dialog, _ ->
                afterAction()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)){dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()

    }
    fun onClickAddReview(){
        reviewContent = getReviewFromEditText()
        if(reviewContent.isEmpty()) return
        val review = Review( repositoryCached.getUserName(),repositoryCached.getUserId(),reviewContent)
            viewModel.deleteMountainReview(mntnName,review.writer){isSuccessful->
                if(isSuccessful){
                    viewModel.setMountainReview(mntnName,review){isSuccessful->
                        if(isSuccessful){
                            SampleToast.createToast(this, "리뷰가 등록되었습니다:)")?.show()
                            binding.reviewEditText.text.clear()
                            binding.myReview.text = reviewContent
                            removeReviewBtn.isVisible = true
                        }
                        else{
                            SampleToast.createToast(this, "리뷰가 등록되지 않았습니다.")?.show()
                        }
                    }
                }else{
                    Log.e("deleteError", "리뷰 아이템 삭제에 실패했습니다.")
                }
            }
        }
    private fun getReviewFromEditText(): String{
        return binding.reviewEditText.text.toString().trim()
    }

}