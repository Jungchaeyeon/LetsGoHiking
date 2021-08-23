package com.jcy.letsgohiking.home.tab2

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.hdh.base.viewmodel.BaseViewModel
import com.jcy.letsgohiking.MyApplication.Companion.getString
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.home.tab2.model.Area
import com.jcy.letsgohiking.home.tab2.model.MountainItem
import com.jcy.letsgohiking.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.IOException
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.collections.ArrayList

class SearchViewModel(): BaseViewModel(){

    val liveKeyword = MutableLiveData<String>()
    val mountainList = MutableLiveData<ArrayList<MountainItem>>()
    var mountainArray = ArrayList<MountainItem>()

    var mountainList0to500 = ArrayList<MountainItem>()
    var mountainList500to1000 = ArrayList<MountainItem>()
    var mountainList1000to1500 = ArrayList<MountainItem>()

    fun addItem(item: MountainItem) {
        mountainArray.add(item)
        mountainList.postValue(mountainArray)
    }
    fun removeAllItems() {
        mountainArray.clear()
        mountainList.postValue(mountainArray)
    }
    val liveAreaKeywordList = MutableLiveData<List<Area>>()

    fun requestAreaKeywordData() {
        liveAreaKeywordList.postValue(listOf(
           Area("서울특별시"),
            Area("부산광역시"),
            Area("대구광역시"),
            Area("인천광역시"),
            Area("광주광역시"),
            Area("대전광역시"),
            Area("울산광역시"),
            Area("경기도"),
            Area("강원도"),
            Area("충청북도"),
            Area("충청남도"),
            Area("전라북도"),
            Area("전라남도"),
            Area("경상북도"),
            Area("경상남도"),
            Area("제주도")

        ))
    }
    fun getMountainsByDifficulty(activity: Activity, url: String, response: (success: Boolean) -> Unit){
        val mntncode = 10.toString()
        Log.e("mntncode 확인", mntncode)
        var localUrl= url+ "&mntnInfoThmCd=${mntncode}"

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
                doc = dBuilder?.parse(localUrl)
            }catch (e : IOException){
                e.printStackTrace()
            }

            doc?.documentElement?.normalize()
            Log.d("root element", doc?.documentElement?.nodeName.toString())

            if(doc != null){

                val itemList : NodeList = doc.getElementsByTagName("item")
                val mntnsList = ArrayList<MountainItem>()

                for(i in 0..itemList.length){
                    val nNode :Node?= itemList.item(i)

                    if(nNode?.nodeType== Node.ELEMENT_NODE){

                        val eElement: Element = nNode as Element
                        val mountain : MountainItem = MountainItem()
                        val mntnName =getTagValue("mntnnm",eElement)
                        val mountainImgUrl = getTagValue("mntnattchimageseq", eElement)
                        val mountainHeight = getTagValue("mntninfohght",eElement).trim()
                        var mntnDetailInfo = getTagValue("mntninfodtlinfocont", eElement).trim()
                        var top100reson = getTagValue("hndfmsmtnslctnrson",eElement).trim()
                        var courseInfo = getTagValue("crcmrsghtnginfoetcdscrt", eElement).trim()

                        if(mountainImgUrl.isNotEmpty() && mountainHeight.isNotEmpty()){
                            if(mountainImgUrl == "http://www.forest.go.kr/newkfsweb/cmm/fms/getImage.do?fileSn=1&atchFileId="){
                                continue
                            }
                            when(mntnName){
                                "관산","봉제산","구룡산","견두산","경수산","구수산","곧은봉","백두산","기마봉",
                                "도장산"-> continue
                            }
                            mountain.mntnId = getTagValue("mntnid", eElement).toLong()
                            mountain.mntnName =mntnName
                            mountain.mntnHeight = mountainHeight.toInt()
                            mountain.mntnLocation = getTagValue("mntninfopoflc",eElement)
                            mountain.mntnImg = mountainImgUrl
                            mountain.mntnInfo = getTagValue("mntnsbttlinfo",eElement)
                            mountain.courseInfo = filterString(courseInfo)
                            mountain.top100reson = filterString(top100reson)
                            mountain.mntnDetailInfo = filterString(mntnDetailInfo)

                            if(mountain.mntnDetailInfo.isEmpty()) mountain.mntnDetailInfo ="산 정보가 없습니다."

                            mntnsList.add(mountain)
                        }
                    }
                }

                mntnsList.shuffle()
                mntnsList.forEach { mountain->
                    var mntnheight = mountain.mntnHeight
                    when(mntnheight){
                        in 0.. 500 -> mountainList0to500.add(mountain)
                        in 501 ..1000-> mountainList500to1000.add(mountain)
                        in 1001 .. 1500 -> mountainList1000to1500.add(mountain)
                        else -> mountainList1000to1500.add(mountain)
                    }
                }

                mountainList0to500 = mountainList0to500.filter {
                    it.courseInfo.trim() !="&amp;nbsp;" && it.courseInfo.trim() != "&amp;#160;"//산행포인트 정보가 있는 아이템만
                } as ArrayList<MountainItem>

                mountainList500to1000 = mountainList500to1000.filter {
                    it.courseInfo.trim() !="&amp;nbsp;" && it.courseInfo.trim() != "&amp;#160;"//산행포인트 정보가 있는 아이템만
                } as ArrayList<MountainItem>

                mountainList1000to1500 = mountainList1000to1500.filter {
                    it.courseInfo.trim() !="&amp;nbsp;" && it.courseInfo.trim() != "&amp;#160;"//산행포인트 정보가 있는 아이템만
                } as ArrayList<MountainItem>

                activity.runOnUiThread(){
                    response.invoke(true)
                }
                return@Thread
            }
        }.start()
    }
    fun getMountains(activity: Activity, url: String, area: String, response: (success: Boolean) -> Unit){

        removeAllItems()
        var localUrl= url+ "&mntnInfoAraCd=${area}"
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
                doc = dBuilder?.parse(localUrl)
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
                        val mntnDetailInfo = getTagValue("mntninfodtlinfocont", eElement).trim()
                        var top100reson = getTagValue("hndfmsmtnslctnrson",eElement).trim()
                        var courseInfo = getTagValue("crcmrsghtnginfoetcdscrt", eElement).trim()
                        val mntnName = getTagValue("mntnnm",eElement).trim()

                            //예외처리-----------------------------------------------------------------------------
                            if(mountainImgUrl.isNotEmpty() && mountainHeight.isNotEmpty()) {
                                if (mountainImgUrl == "http://www.forest.go.kr/newkfsweb/cmm/fms/getImage.do?fileSn=1&atchFileId=") {
                                    continue
                                }
                            }
                            if(mountainHeight=="") continue
                            when(mntnName){
                                "관산","봉제산","구룡산","견두산","경수산" -> continue
                            }
                            //예외처리-----------------------------------------------------------------------------

                            mountain.mntnId = getTagValue("mntnid", eElement).toLong()
                            mountain.mntnName = mntnName
                            mountain.mntnHeight = mountainHeight.toInt()
                            mountain.mntnLocation = getTagValue("mntninfopoflc",eElement)
                            mountain.mntnImg = mountainImgUrl
                            mountain.mntnInfo = getTagValue("mntnsbttlinfo",eElement)
                            mountain.courseInfo = filterString(courseInfo)
                            mountain.top100reson = filterString(top100reson)
                            mountain.mntnDetailInfo =filterString(mntnDetailInfo)

                            addItem(mountain)
                        }
                    }
                }
                mountainList.value?.shuffle()
                activity.runOnUiThread(){
                    response.invoke(true)
                }
                return@Thread
            }.start()
    }
    fun filterString(_string: String): String{
        val pattern = "[a-z|A-Z|&;/#@%^*()]".toRegex()
        val patternBr = "[<br>||<>]".toRegex()
        val firstPass = pattern.replace(_string,"")
        return patternBr.replace(firstPass,"\n")
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
                        val mountain  = MountainItem()
                        val mountainImgUrl = getTagValue("mntnattchimageseq", eElement)
                        val mountainHeight = getTagValue("mntninfohght",eElement).trim()

                        if(mountainImgUrl.isNotEmpty() && mountainHeight.isNotEmpty()){
                            if(mountainImgUrl == "http://www.forest.go.kr/newkfsweb/cmm/fms/getImage.do?fileSn=1&atchFileId="){
                                continue
                            }
                            mountain.mntnId = getTagValue("mntnid", eElement).toLong()
                            mountain.mntnName = getTagValue("mntnnm",eElement)
                            mountain.mntnHeight = mountainHeight.toInt()
                            mountain.mntnLocation = getTagValue("mntninfopoflc",eElement)
                            mountain.mntnInfo = getTagValue("mntnsbttlinfo",eElement)
                            mountain.mntnImg = mountainImgUrl

                            addItem(mountain)

                        }
                    }
                }
                activity.runOnUiThread(){
                    Log.e("mountainArray", mountainArray.toString())
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
    /* -------------산정보주제코드	산정보주제명----------
          01	계곡  -07
          02	단풍  -09
          03	억새  -09
          04	바다  -08
          05	문화유적 -03
          06	일출/일몰
          07	가족산행
          08	바위
          09	봄꽃  - 05
          10	조망
          11	설경    -01 12
 -----------------------------------------------   */

    private fun getMountainCode(): String{
        val nowMonth = String.format("%02d",(Calendar.getInstance().get(Calendar.MONTH)+1))
        var codeList = arrayOf("10")
        when(nowMonth){
            "12","01","02" -> {
                codeList = arrayOf("10","11","07","05","06")
            }
            "03","04","05" -> {
                codeList = arrayOf("05","06","07","08","09","10")
            }
            "06","07","08" -> {
                codeList = arrayOf("01","04","05","06","07","10")
            }
            "09","10","11" ->{
                codeList = arrayOf("02","03","06","07","08","10")
            }
        }
        return codeList.random().toString()
    }
}