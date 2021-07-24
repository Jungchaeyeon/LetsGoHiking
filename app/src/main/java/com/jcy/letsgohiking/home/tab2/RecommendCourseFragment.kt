package com.jcy.letsgohiking.home.tab2

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentRecommendCourseBinding
import com.jcy.letsgohiking.util.Log
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class RecommendCourseFragment: BaseDataBindingFragment<FragmentRecommendCourseBinding> (R.layout.fragment_recommend_course) {


    private lateinit var adapter : MountainAdapter
    private val viewModel:SearchViewModel by sharedViewModel()
    private var isSearchResult = false

    override fun FragmentRecommendCourseBinding.onBind() {
        vm = viewModel

        adapter = MountainAdapter()
        viewModel.bindLifecycle(requireActivity())
        binding.lv.layoutManager = GridLayoutManager(requireActivity(),2)
        binding.lv.adapter =adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!isSearchResult) {

            val url = "http://openapi.forest.go.kr/openapi/service/trailInfoService/getforeststoryservice?&numOfRows=100&ServiceKey=" + getString(R.string.getMountains_client_id)
            getMountains(url)
        }
    }
     fun nextStepSearch(isSuccess: Boolean, mountainList: ArrayList<MountainItem>){
        if(isSuccess){
            Log.d("isSuccess", mountainList.toString())
            if(isAdded) {

                    adapter.submitList(mountainList.orEmpty())
                    adapter.notifyDataSetChanged()

                }


            //todo 검색 페이지 띄우기
        }else{
            Toast.makeText(requireContext(), "정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getMountains(url: String){
        Thread{
            val dbFactory = DocumentBuilderFactory.newInstance()
            var dBuilder: DocumentBuilder? = null

            try{
                dBuilder = dbFactory.newDocumentBuilder()
            }catch (e : ParserConfigurationException){
                e.printStackTrace()
            }
            var doc : Document? =null
            try{
                doc = dBuilder?.parse(url)
            }catch (e : IOException){
                e.printStackTrace()
            }

            doc?.documentElement?.normalize()
            Log.d("root element", doc?.documentElement?.nodeName.toString())

            if(doc != null){

                val itemList : NodeList = doc.getElementsByTagName("item")

                for(i in 0..itemList.length){
                    val nNode :Node?= itemList.item(i)

                    if(nNode?.nodeType== Node.ELEMENT_NODE){

                        val eElement: Element = nNode as Element
                        val mountain : MountainItem = MountainItem()
                        val mountainImgUrl = getTagValue("mntnattchimageseq", eElement)
                        val mountainHeight = getTagValue("mntninfohght",eElement).trim()

                        if(mountainImgUrl.isNotEmpty() && mountainHeight.isNotEmpty()){
                            if(mountainImgUrl == "http://www.forest.go.kr/newkfsweb/cmm/fms/getImage.do?fileSn=1&atchFileId="){
                                continue
                            }
                            mountain.mntnName = getTagValue("mntnnm",eElement)
                            mountain.mntnHeight = mountainHeight.toInt()
                            mountain.mntnLocation = getTagValue("mntninfopoflc",eElement)
                            mountain.mntnInfo = getTagValue("mntnsbttlinfo",eElement)
                            //Log.e("info정보",mountain.mntnInfo.toString())
                            mountain.mntnImg = mountainImgUrl

                            viewModel.addItem(mountain)
                        }
                    }
                }
                requireActivity().runOnUiThread {
                    adapter.submitList(viewModel.mountainList.value)
                }

                return@Thread
            }
        }.start()
    }


    private fun getTagValue( tag: String, eElement : Element): String {
         val nList = eElement.getElementsByTagName(tag).item(0).childNodes
        val nValue = nList.item(0) as Node
        return nValue.nodeValue
    }

    companion object {
        fun getInstance() = RecommendCourseFragment()
    }
}

