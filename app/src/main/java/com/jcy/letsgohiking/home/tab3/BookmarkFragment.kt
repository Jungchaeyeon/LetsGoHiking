package com.jcy.letsgohiking.home.tab3

import android.os.Bundle
import com.hdh.base.fragment.BaseDataBindingFragment
import com.jcy.letsgohiking.ActivityNavigator
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentBookmarkBinding
import com.jcy.letsgohiking.home.tab2.AppDatabase
import com.jcy.letsgohiking.home.tab2.adapter.MountainBookmarkAdapter
import com.jcy.letsgohiking.home.tab2.getAppDatabaseMntn
import com.jcy.letsgohiking.util.Log
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class BookmarkFragment : BaseDataBindingFragment<FragmentBookmarkBinding>(R.layout.fragment_bookmark) {

    private lateinit var adapter: MountainBookmarkAdapter
    private lateinit var db: AppDatabase
    private lateinit var mBinding: FragmentBookmarkBinding

    companion object {
        fun getInstance() = BookmarkFragment()
    }

    override fun onResume() {
        super.onResume()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = FragmentBookmarkBinding.inflate(layoutInflater)
        db = getAppDatabaseMntn(requireContext())
    }
    private fun initAdapter() {
        adapter = MountainBookmarkAdapter(callback = { mntnItem ->
            context?.let {
                ActivityNavigator.with(it).todetailmountainpage(mntnItem).start()
            }
        })
        binding.bookmarkMntnList.adapter = adapter
    }
    private fun initDB(){
        Thread(Runnable {
           val bookmarkItems = db.mountainDao().getAll()
            requireActivity().runOnUiThread{
                adapter.submitList(bookmarkItems)
                adapter.notifyDataSetChanged()
                Log.e("bookmarkItems", bookmarkItems.toString())
            }
        }).start()
    }
    override fun FragmentBookmarkBinding.onBind() {
        vi = this@BookmarkFragment
        initAdapter()
        initDB()
    }
}