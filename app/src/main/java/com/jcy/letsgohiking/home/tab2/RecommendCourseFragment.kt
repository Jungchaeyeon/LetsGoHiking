package com.jcy.letsgohiking.home.tab2

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.api.MountainService
import com.jcy.letsgohiking.dao.MountainDTO
import com.jcy.letsgohiking.databinding.FragmentRecommendCourseBinding
import com.jcy.letsgohiking.util.Log
import okhttp3.OkHttpClient
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jaxb.JaxbConverterFactory
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class RecommendCourseFragment: BaseDataBindingFragment<FragmentRecommendCourseBinding> (R.layout.fragment_recommend_course) {

    private lateinit var mountainService: MountainService
    private val adapter: MountainAdapter = MountainAdapter()
    private lateinit var mountain: Mountain
    override fun FragmentRecommendCourseBinding.onBind() {
        binding.lv.layoutManager = LinearLayoutManager(requireContext())
        binding.lv.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = "http://openapi.forest.go.kr/openapi/service/trailInfoService/getforeststoryservice?&ServiceKey=" + getString(R.string.getMountains_client_id)
        getMountains(url)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
                        Log.d("mntnName", getTagValue("mntnnm",eElement).toString())
                        Log.d("mntnInfo", getTagValue("mntnsbttlinfo",eElement).toString())
                        Log.d("mntnheight", getTagValue("mntninfohght",eElement).toString())
                        Log.d("mntnlocation", getTagValue("mntninfopoflc",eElement).toString())
                    }
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


