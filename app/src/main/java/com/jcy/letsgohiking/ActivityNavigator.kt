package com.jcy.letsgohiking
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.jcy.letsgohiking.home.login.LoginActivity
import com.jcy.letsgohiking.home.record.HikingRecordActivity
import com.jcy.letsgohiking.home.tab2.DetailMountainInfoActivity
import com.jcy.letsgohiking.home.tab2.model.MountainItem
import com.jcy.letsgohiking.home.tab2.review.MountainReviewActivity
import java.util.*

/**
 * UI component 이외의 곳 (ViewModel, Service, 등) 에서 사용하지 않도록 주의할 것.
 * context 에 대한 reference 가 풀리지 않아 memory leak 이 발생할 수 있음.
 */
class ActivityNavigator private constructor(private val context: Context) {

    companion object {
        const val KEY_DATA = "data"

        @JvmStatic
        fun with(context: Context): ActivityNavigator = ActivityNavigator(context)

        @JvmStatic
        fun with(fragment: Fragment): ActivityNavigator = with(fragment.context ?: fragment.requireActivity())
    }

    val stack: ArrayList<Intent> = ArrayList()

    fun main(isClear: Boolean = true) = MyIntent(MainActivity::class.java, isClear)
    fun login() = MyIntent(LoginActivity::class.java)
    fun hikingrecord(mntnName: String) = MyIntent(HikingRecordActivity::class.java).apply { putExtra(KEY_DATA,mntnName) }
    fun todetailmountainpage(mntnModel: MountainItem) = MyIntent(DetailMountainInfoActivity::class.java).apply { putExtra(KEY_DATA,mntnModel) }
    fun review(mntnName: String) = MyIntent(MountainReviewActivity::class.java).apply { putExtra(KEY_DATA,mntnName) }



    inner class MyIntent : Intent {

        constructor(cls: Class<*>?, clear: Boolean = false) : super(context, cls) {
            addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            addFlags(FLAG_ACTIVITY_SINGLE_TOP)

            if (clear) {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                addFlags(FLAG_ACTIVITY_CLEAR_TASK)
            }
        }

        constructor(action: String?, uri: Uri?) : super(action, uri)

        fun add(): ActivityNavigator {
            stack.add(this)
            return this@ActivityNavigator
        }

        fun start() {
            stack.add(this)
            context.startActivities(stack.toTypedArray())
        }

        fun startForResult(requestCode: Int) {
            if (context !is Activity) {
                throw IllegalArgumentException("startActivityForResult 는 context 가 activity 일 경우에만 사용할 수 있습니다.")
            }

            context.startActivityForResult(this, requestCode)
        }

    }
}