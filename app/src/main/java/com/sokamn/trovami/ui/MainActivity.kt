package com.sokamn.trovami.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sokamn.trovami.R
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.data.source.datastore.DataStoreConstants.USER_KEY_PREFS

class MainActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context, currentUserUid: String): Intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(USER_KEY_PREFS, currentUserUid)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun initUI() {
        setUIComponents()
        initListeners()
    }

    private fun setUIComponents() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvContainerAM) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initListeners() {
        findViewById<BottomNavigationView>(R.id.bottomNav).apply {
            homeSelected(this)
            navController.let { navController ->
                NavigationUI.setupWithNavController(
                    this,
                    navController
                )
                navController.addOnDestinationChangedListener(){ _, dest, _ ->
                    when(dest.id){
                        R.id.homeFragment-> homeSelected(this)
                        //R.id.searchFragment-> homeSelected(this)
                        R.id.collectionFragment-> savedSelected(this)
                        //R.id.masterSavedFragment-> savedSelected(this)
                        R.id.messageFragment-> messageSelected(this)
                        R.id.profileFragment-> profileSelected(this)
                        //R.id.makeJobFragment-> makeJobSelected(this)
                    }
                    /*aux = if(dest.id == R.id.masterSavedFragment){
                        1
                    }else{
                        0
                    }*/
                }
                setOnItemReselectedListener {
                    val selectedMenuItemNavGraph =
                        navController.graph.findNode(it.itemId) as NavGraph
                    selectedMenuItemNavGraph.let { menuGraph ->
                        /*if(aux == 1){
                            findNavController(R.id.fcvContainerAM).navigate(MasterSavedFragmentDirections.actionMasterSavedFragmentToSavedFragment())
                        }*/
                        navController.popBackStack(menuGraph.startDestinationId, false)
                    }
                }
            }
        }
    }

    private fun profileSelected(bottomNav: BottomNavigationView) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.bg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun messageSelected(bottomNav: BottomNavigationView) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.tertiaryColor)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    private fun savedSelected(bottomNav: BottomNavigationView) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.bg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }


    private fun homeSelected(bottomNav: BottomNavigationView) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.bg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun makeJobSelected(bottomNav: BottomNavigationView) {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}