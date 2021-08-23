package com.jcy.letsgohiking.home.tab2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hdh.base.activity.BaseDataBindingActivity
import com.hdh.base.recycler.BaseDataBindingRecyclerViewAdapter
import com.jcy.letsgohiking.*
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.ActivityDetailMountainInfoBinding
import com.jcy.letsgohiking.databinding.ItemMntnImageBinding
import com.jcy.letsgohiking.ext.loadUrl
import com.jcy.letsgohiking.home.tab2.adapter.MountainReviewAdapter
import com.jcy.letsgohiking.home.tab2.adapter.SearchRecyclerAdapter
import com.jcy.letsgohiking.home.tab2.model.*
import com.jcy.letsgohiking.home.tab2.search.Poi
import com.jcy.letsgohiking.home.tab2.search.Pois
import com.jcy.letsgohiking.util.RetrofitUtil
import kotlinx.android.synthetic.main.activity_detail_mountain_info.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class DetailMountainInfoActivity :
    BaseDataBindingActivity<ActivityDetailMountainInfoBinding>(R.layout.activity_detail_mountain_info),
    OnMapReadyCallback, CoroutineScope{

    private lateinit var db: AppDatabase
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var currentSelectMarker: Marker?= null
    private lateinit var map: GoogleMap
    private lateinit var searchResult: SearchResultEntity
    private var isBookmarked = false
    private val viewModel by viewModel<DetailMountainViewModel>()
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var reviewAdapter: MountainReviewAdapter
    private lateinit var mntn: MountainItem
    private lateinit var reviewList : ArrayList<Review>
    private lateinit var firebaseDB : DatabaseReference

    private val locationRecyclerVeiw: RecyclerView by lazy{
        findViewById(R.id.locationListRv)
    }
    companion object{
        const val CAMERA_ZOOM_LEVEL = 13f
    }
    
    override fun setupProperties(bundle: Bundle?) {
        super.setupProperties(bundle)

         mntn = bundle?.getSerializable(ActivityNavigator.KEY_DATA) as MountainItem
         viewModel.liveMountainItem.value= bundle?.getSerializable(ActivityNavigator.KEY_DATA) as MountainItem
    }
    override fun ActivityDetailMountainInfoBinding.onBind() {
        vi = this@DetailMountainInfoActivity
        vm = viewModel
        viewModel.bindLifecycle(this@DetailMountainInfoActivity)
        db = getAppDatabaseMntn(applicationContext)
        job = Job()
        reviewList = ArrayList()

        initAdapter()
        initViews()
        initData()
        getMountainImages(mntn.mntnName)
        getMountainReviews(mntn.mntnName)
        searchKeyword(mntn.mntnName)

    }
    private fun initAdapter(){
        adapter = SearchRecyclerAdapter()
        locationRecyclerVeiw.adapter = adapter
        reviewAdapter = MountainReviewAdapter()
        binding.previewRecyclerView.adapter = reviewAdapter
    }
    fun getMountainReviews(mntnName: String){
        firebaseDB =  Firebase.database.reference
        viewModel.getMountainReview(mntnName){
            if(it){
                reviewList = viewModel.reviewList
                if(reviewList.size >0) binding.noReviewNotice.isVisible = false
                reviewAdapter.submitList(reviewList.takeLast(3))
                reviewAdapter.notifyDataSetChanged()
            }
            else{
                binding.noReviewNotice.isVisible = true
            }
        }
    }
    private fun getMountainImages(mntnName: String){
        viewModel.getMountainImages(mntnName){ isSuccessful ->
            if(isSuccessful){
                Log.e("mountainList 데이터확인", viewModel.liveMountainImageItems.value.toString())

                binding.mntnImageList.run {
                    adapter = BaseDataBindingRecyclerViewAdapter<NaverSearchImage>()
                        .setItemViewType { item, position, isLast ->
                            if(position ==0) 0 else 0
                        }
                        .addViewType(
                            BaseDataBindingRecyclerViewAdapter.MultiViewType<NaverSearchImage, ItemMntnImageBinding>(R.layout.item_mntn_image) {
                                vi = this@DetailMountainInfoActivity
                                item = it
                                this.imgView.loadUrl(it.imgUrl)
                            })
                }
            }
            else{}
        }
        }

    private fun searchKeyword(keyword: String){
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO){
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keyword
                    )
                    if(response.isSuccessful){
                        val body = response.body()
                        withContext(Dispatchers.Main){
                            Log.e("response",body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private fun setData(pois: Pois){
        val dataList = pois.poi.map{
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAdress= makeMainAddress(it) ,
                locationLatLng= LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList){
            //  Toast.makeText(this,"빌딩 이름: ${it.name} 주소: ${it.fullAdress}", Toast.LENGTH_SHORT).show()
            searchResult = it
            setupGoogleMap()
            locationRecyclerVeiw.isVisible = false
        }
    }
    private fun setupGoogleMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun makeMainAddress(poi: Poi): String =
        if(poi.secondNo?.trim().isNullOrEmpty()){
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " "+
                    (poi.detailAddrName?.trim() ?: "") + " "+
                    poi.firstNo?.trim()
        }else{
            (poi.upperAddrName?.trim() ?: "") + " "+
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
    private fun initViews(){

        if(mntn.top100reson.trim().isNotEmpty()){
            binding.mntnTop100ReasonViewGroup.isVisible = true
        }
        if(mntn.courseInfo.trim().isNotEmpty()){
            binding.mntnHikingPointViewGroup.isVisible = true
        }

        if(mntn.mntnDetailInfo.trim().isEmpty()){
            binding.mntnDetailInfoValue.text ="산 정보가 없습니다."
        }
        setBookmarkBtn()
    }
    private fun setBookmarkBtn(){
        Thread{
           val getItem: MountainItem? = db.mountainDao().findMountain(mntn.mntnId)
            this.runOnUiThread {
                isBookmarked = getItem?.isBookmarked ?: false
                Log.e("isBookmarked", isBookmarked.toString())
                binding.bookmarkBtn.isSelected = isBookmarked //Bookmark버튼 isBookmarked 에 따른 UI처리
            }
        }.start()
    }
    fun onClickBookmark(view: View){
        view.isSelected = !view.isSelected
        if(view.isSelected) {
            if(mntn.mntnName =="") return
            mntn.isBookmarked = true
            Thread(Runnable {
                db.mountainDao().insertMountain(mntn)
            }).start()
            SampleToast.createToast(this, "북마크에 저장되었습니다.")?.show()
        }
        else{
            mntn.isBookmarked = false
            Thread(Runnable {
                db.mountainDao().delete(mntn)
            }).start()
            SampleToast.createToast(this, "북마크에서 삭제되었습니다.")?.show()
        }
    }
    fun onClickRecord(){
       ActivityNavigator.with(this).hikingrecord(mntn.mntnName).start()
    }

    fun onClickShowMoreReview(){
        ActivityNavigator.with(this).review(mntn.mntnName).start()
    }
    fun onClickThumbnail(item: NaverSearchImage){
        //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.originLink)))
    }
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentSelectMarker = setupMarker(searchResult)
    }
    private fun setupMarker(searchResult: SearchResultEntity): Marker{
        val positionLatLng = LatLng(
            searchResult.locationLatLng.latitude.toDouble(), searchResult.locationLatLng.longitude.toDouble()
        )
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAdress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)
    }
    private fun initData(){
        adapter.notifyDataSetChanged()
    }
}
