package com.sokamn.trovami.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sokamn.trovami.R
import com.sokamn.trovami.databinding.ActivitySplashBinding
import com.sokamn.trovami.presentation.MainActivity
import com.sokamn.trovami.presentation.introduction.IntroductionActivity
import com.sokamn.trovami.presentation.introduction.IntroductionViewModel
import com.sokamn.trovami.presentation.verification.VerificationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        initObservers()
    }

    private fun initObservers() {
        splashViewModel.navigateToMain.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToMain()
            }
        })

        splashViewModel.navigateToVerification.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToVerification()
            }
        })

        splashViewModel.navigateToIntroduction.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToIntroduction()
            }
        })
    }

    private fun goToMain() {
        finish()
        startActivity(MainActivity.create(this))
    }

    private fun goToIntroduction(){
        finish()
        startActivity(IntroductionActivity.create(this))
    }

    private fun goToVerification() {
        finish()
        startActivity(VerificationActivity.create(this))
    }
}