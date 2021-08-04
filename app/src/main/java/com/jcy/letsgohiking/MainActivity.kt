package com.jcy.letsgohiking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hdh.base.activity.BaseDataBindingActivity
import com.jcy.letsgohiking.databinding.ActivityMainBinding
import com.jcy.letsgohiking.home.tab1.HomeFragment
import com.jcy.letsgohiking.home.tab2.SearchFragment
import com.jcy.letsgohiking.home.tab3.BookmarkFragment
import com.jcy.letsgohiking.home.tab4.LikeFragment
import com.jcy.letsgohiking.home.tab5.ProfileFragment

class MainActivity : BaseDataBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var homeFragment:HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var bookmarkFragment: BookmarkFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var profileFragment: ProfileFragment

    override fun ActivityMainBinding.onBind() {
        vi=this@MainActivity

        changeFragment(HomeFragment.getInstance())
        initNavigation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun initNavigation(){
        binding.bottomNavBar.itemIconTintList =null
        binding.bottomNavBar.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
    private val onNavigationItemSelectedListener= BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.menu_home->{
                homeFragment= HomeFragment.getInstance()
                changeFragment(homeFragment)
            }
            R.id.menu_search->{
                searchFragment = SearchFragment.getInstance()
                changeFragment(searchFragment)
            }
            R.id.menu_bookmark->{
                bookmarkFragment = BookmarkFragment.getInstance()
                changeFragment(bookmarkFragment)
            }
            R.id.menu_profile->{
                profileFragment = ProfileFragment.getInstance()
                changeFragment(profileFragment)
            }
            else->{}
        }
        true
    }
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

    }
    companion object {
        fun getInstance() = MainActivity()
    }
}


