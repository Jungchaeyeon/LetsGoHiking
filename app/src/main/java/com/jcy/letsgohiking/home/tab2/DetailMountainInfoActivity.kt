package com.jcy.letsgohiking.home.tab2

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.ActivityDetailMountainInfoBinding

import com.jcy.letsgohiking.home.tab2.adapter.SearchRecyclerAdapter
import com.jcy.letsgohiking.home.tab2.model.DetailMountainViewModel
import com.jcy.letsgohiking.home.tab2.model.LocationLatLngEntity
import com.jcy.letsgohiking.home.tab2.model.SearchResultEntity
import com.jcy.letsgohiking.home.tab2.search.Poi
import com.jcy.letsgohiking.home.tab2.search.Pois
import com.jcy.letsgohiking.util.RetrofitUtil
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class DetailMountainInfoActivity :
    BaseDataBindingActivity<ActivityDetailMountainInfoBinding>(R.layout.activity_detail_mountain_info),
    OnMapReadyCallback, CoroutineScope{

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var currentSelectMarker: Marker?= null
    private lateinit var map: GoogleMap
    private lateinit var searchResult: SearchResultEntity

    //안드로이드에서 위치정보를 불러올 때 관리해주는 utility 클래스
    private lateinit var locationManager: LocationManager

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
        vm = viewModel
        viewModel.bindLifecycle(this@DetailMountainInfoActivity)
        job = Job()

        initAdapter()
        initViews()
        initData()
        searchKeyword(mntn.mntnName)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
//        Glide.with(applicationContext)
//            .load(viewModel.liveMountainItem.value?.mntnImg)
//            .into(binding.mntnImageView)

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
