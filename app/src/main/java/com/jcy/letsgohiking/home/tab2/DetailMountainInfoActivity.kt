package com.jcy.letsgohiking.home.tab2

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hdh.base.activity.BaseDataBindingActivity
import com.hdh.base.recycler.BaseDataBindingRecyclerViewAdapter
import com.jcy.letsgohiking.*
import com.jcy.letsgohiking.databinding.ActivityDetailMountainInfoBinding
import com.jcy.letsgohiking.databinding.ItemAreaKeywordBinding
import com.jcy.letsgohiking.databinding.ItemMntnImageBinding
import com.jcy.letsgohiking.ext.loadUrl

import com.jcy.letsgohiking.home.tab2.adapter.SearchRecyclerAdapter
import com.jcy.letsgohiking.home.tab2.model.*
import com.jcy.letsgohiking.home.tab2.search.Poi
import com.jcy.letsgohiking.home.tab2.search.Pois
import com.jcy.letsgohiking.network.api.Api
import com.jcy.letsgohiking.util.RetrofitUtil
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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
    private lateinit var mntn: MountainItem
    private val locationRecyclerVeiw: RecyclerView by lazy{
        findViewById(R.id.locationListRv)
    }
    companion object{
        const val CAMERA_ZOOM_LEVEL = 15f
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

        initAdapter()
        initViews()
        initData()
        getMountainImages(mntn.mntnName)
        searchKeyword(mntn.mntnName)

    }
    private fun getMountainImages(mntnName: String){
        viewModel.getMountainImages(mntnName){ isSuccessful ->
            if(isSuccessful){
                Log.e("mountainList 데이터확인", viewModel.liveMountainImageItems.value.toString())

                binding.mntnImageList.run {
                    adapter = BaseDataBindingRecyclerViewAdapter<String>()
                        .setItemViewType { item, position, isLast ->
                            // if (position == 0) 0 else 1
                            if(position ==0) 0 else 0

                        }
                        .addViewType(
                            BaseDataBindingRecyclerViewAdapter.MultiViewType<String, ItemMntnImageBinding>(R.layout.item_mntn_image) {
                                this.imgView.loadUrl(it)
                            })
                }
            }
            else{

            }
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

        if(mntn.top100reson.trim()!="&amp;nbsp;"){
            binding.mntnTop100ReasonViewGroup.isVisible = true
        }
        if(mntn.courseInfo.trim()!="&amp;nbsp;"){
            binding.mntnHikingPointViewGroup.isVisible = true
        }
        setBookmarkBtn()
//        Glide.with(applicationContext)
//            .load(viewModel.liveMountainItem.value?.mntnImg)
//            .into(binding.mntnImageView)
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
    private fun initAdapter(){
        adapter = SearchRecyclerAdapter()
        locationRecyclerVeiw.adapter = adapter
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
