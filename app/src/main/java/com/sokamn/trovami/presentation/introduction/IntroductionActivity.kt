package com.sokamn.trovami.presentation.introduction

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sokamn.trovami.databinding.ActivityIntroductionBinding
import com.sokamn.trovami.presentation.MainActivity
import com.sokamn.trovami.presentation.login.LoginActivity
import com.sokamn.trovami.presentation.signup.SignUpActivity
import com.sokamn.trovami.presentation.verification.VerificationActivity
import com.sokamn.trovami.util.AppConstants.Companion.EMAIL
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.util.AdvancedTlsX509TrustManager.Verification

@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {

    companion object{
        fun create(context: Context): Intent =
            Intent(context, IntroductionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
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
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun initListeners() {
        with(binding) {
            btnLogInAI.setOnClickListener { introductionViewModel.onLoginSelected() }
            btnRegisterNowAI.setOnClickListener { introductionViewModel.onSignUpSelected() }
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

        introductionViewModel.navigateToVerification.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToVerification()
            }
        })
    }

    private fun goToVerification() {
        startActivity(VerificationActivity.create(this))
    }

    private fun goToSingUp() {
        startActivity(SignUpActivity.create(this,EMAIL,"IntroductionActivity"))
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }
}