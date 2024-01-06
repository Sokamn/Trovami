package com.sokamn.trovami.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sokamn.trovami.R
import com.sokamn.trovami.databinding.ActivitySplashBinding
import com.sokamn.trovami.ui.MainActivity
import com.sokamn.trovami.ui.auth.introduction.IntroductionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var currentUser = ""
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        setUIComponents()
        initObservers()
    }

    private fun setUIComponents() {
        window.statusBarColor = getColor(R.color.primaryColor)
        setUpVideoView()
    }

    private fun setUpVideoView() {
        val videoPath = "android.resource://$packageName/raw/splash_screen"
        with(binding){
            vvwSplash.setOnPreparedListener { mediaPlayer ->
                val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
                val screenRatio = vvwSplash.width / vvwSplash.height.toFloat()
                val scaleX = videoRatio / screenRatio
                if (scaleX >= 1f) {
                    vvwSplash.scaleX = scaleX
                } else {
                    vvwSplash.scaleY = 1f / scaleX
                }
            }
            vvwSplash.setVideoPath(videoPath)
            vvwSplash.setOnCompletionListener {

            }
            vvwSplash.start()
        }
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

        splashViewModel.currentUser.observe(this){ userUID ->
            this.currentUser = userUID
        }
    }

    private fun goToMain() {
        finish()
        startActivity(MainActivity.create(this, currentUser))
    }

    private fun goToIntroduction(){
        finish()
        startActivity(IntroductionActivity.create(this))
    }

    private fun goToVerification() {
        //finish()
        //startActivity(VerificationActivity.create(this,currentUser))
    }
}