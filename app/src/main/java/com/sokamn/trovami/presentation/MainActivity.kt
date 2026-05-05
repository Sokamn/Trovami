package com.sokamn.trovami.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sokamn.trovami.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcvContainerAM) as NavHostFragment
        navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottomNav).apply {
            homeSelected(this)
            navController.let { navController ->
                NavigationUI.setupWithNavController(
                    this,
                    navController
                )
                setOnItemSelectedListener { item ->
                    NavigationUI.onNavDestinationSelected(item, navController)
                    when(item.itemId){
                        R.id.home_nav_graph-> homeSelected(this)
                        R.id.saved_nav_graph-> savedSelected(this)
                        R.id.message_nav_graph-> messageSelected(this)
                        R.id.profile_nav_graph-> profileSelected(this)
                    }
                    true
                }
                setOnItemReselectedListener {
                    val selectedMenuItemNavGraph =
                        navController.graph.findNode(it.itemId) as NavGraph
                    selectedMenuItemNavGraph.let { menuGraph ->
                        navController.popBackStack(menuGraph.startDestinationId, false)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    
    private fun profileSelected(bottomNav: BottomNavigationView) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.bg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        bottomNav.menu.findItem(R.id.home_nav_graph).icon = getDrawable(R.drawable.home_unselected)
        bottomNav.menu.findItem(R.id.saved_nav_graph).icon = getDrawable(R.drawable.saved_unselected)
        bottomNav.menu.findItem(R.id.message_nav_graph).icon = getDrawable(R.drawable.message_unselected)
        bottomNav.menu.findItem(R.id.profile_nav_graph).icon = getDrawable(R.drawable.profile_selected)
    }

    private fun messageSelected(bottomNav: BottomNavigationView) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.tertiaryColor)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        bottomNav.menu.findItem(R.id.home_nav_graph).icon = getDrawable(R.drawable.home_unselected)
        bottomNav.menu.findItem(R.id.saved_nav_graph).icon = getDrawable(R.drawable.saved_unselected)
        bottomNav.menu.findItem(R.id.message_nav_graph).icon = getDrawable(R.drawable.message_selected)
        bottomNav.menu.findItem(R.id.profile_nav_graph).icon = getDrawable(R.drawable.profile_unselected)
    }

    private fun savedSelected(bottomNav: BottomNavigationView) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.bg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        bottomNav.menu.findItem(R.id.home_nav_graph).icon = getDrawable(R.drawable.home_unselected)
        bottomNav.menu.findItem(R.id.saved_nav_graph).icon = getDrawable(R.drawable.saved_selected)
        bottomNav.menu.findItem(R.id.message_nav_graph).icon = getDrawable(R.drawable.message_unselected)
        bottomNav.menu.findItem(R.id.profile_nav_graph).icon = getDrawable(R.drawable.profile_unselected)
    }

    private fun homeSelected(bottomNav: BottomNavigationView) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.bg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        bottomNav.menu.findItem(R.id.home_nav_graph).icon = getDrawable(R.drawable.home_selected)
        bottomNav.menu.findItem(R.id.saved_nav_graph).icon = getDrawable(R.drawable.saved_unselected)
        bottomNav.menu.findItem(R.id.message_nav_graph).icon = getDrawable(R.drawable.message_unselected)
        bottomNav.menu.findItem(R.id.profile_nav_graph).icon = getDrawable(R.drawable.profile_unselected)
    }
}