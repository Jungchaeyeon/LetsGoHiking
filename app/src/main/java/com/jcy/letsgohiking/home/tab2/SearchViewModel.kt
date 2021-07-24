package com.jcy.letsgohiking.home.tab2

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.MyApplication.Companion.getString
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.repository.ApiRepository
import com.jcy.letsgohiking.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class SearchViewModel(): BaseViewModel(){

    val liveKeyword = MutableLiveData<String>()
    val mountainList = MutableLiveData<ArrayList<MountainItem>>()
    var mountainArray = ArrayList<MountainItem>()

    fun addItem(item: MountainItem) {
        mountainArray.add(item)
        mountainList.postValue(mountainArray)
    }
    fun removeAllItems() {
        mountainArray.clear()
        mountainList.postValue(mountainArray)
    }
    fun onClickSearch(activity: Activity,keyword: String, response: (success: Boolean) -> Unit){

        removeAllItems()
        val url = "http://openapi.forest.go.kr/openapi/service/trailInfoService/getforeststoryservice?mntnNm=${keyword}&ServiceKey=" + getString(
            R.string.getMountains_client_id)
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
                response.invoke(false)
            }

            doc?.documentElement?.normalize()
            Log.d("root element", doc?.documentElement?.nodeName.toString())

            if(doc != null){

                val itemList : NodeList = doc.getElementsByTagName("item")

                for(i in 0..itemList.length){
                    val nNode : Node?= itemList.item(i)

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
                            mountain.mntnImg = mountainImgUrl
                            mountainArray.add(mountain)
                            Log.e("info정보",mountainArray.toString())
                            //addItem(mountain)
                        }
                    }
                }
                activity.runOnUiThread(){
                    response.invoke(true)
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
}