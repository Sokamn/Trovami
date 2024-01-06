package com.sokamn.trovami.ui.auth.introduction

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sokamn.trovami.R
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivityIntroductionBinding
import com.sokamn.trovami.ui.auth.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {

    companion object{
        fun create(context: Context): Intent =
            Intent(context, IntroductionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

    private lateinit var binding: ActivityIntroductionBinding
    private val introductionViewModel: IntroductionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUI()

    }

    private fun initUI() {
        initDesign()
        initListeners()
        initObservers()
    }

    private fun initDesign() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun initListeners() {
        with(binding) {
            btnLogInAI.setOnClickListener { introductionViewModel.onLoginSelected() }
            btnRegisterNowAI.setOnClickListener { introductionViewModel.onSignUpSelected() }
            imvGoogleAI.setOnClickListener {  }
            imvFacebookAI.setOnClickListener { toast("Facebook será implementado en próximas versiones") }
        }
    }


    private fun initObservers() {
        introductionViewModel.navigateToLogin.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        })

        introductionViewModel.navigateToSignUp.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToSingUp()
            }
        })
    }

    private fun goToSingUp() {
        //startActivity(SignUpActivity.create(this,EMAIL,"IntroductionActivity","","","", ""))
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }
}