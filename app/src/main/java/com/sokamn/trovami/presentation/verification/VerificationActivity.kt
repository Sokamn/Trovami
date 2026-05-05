package com.sokamn.trovami.presentation.verification

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.ex.spanEmail
import com.sokamn.trovami.databinding.ActivityVerificationBinding
import com.sokamn.trovami.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, VerificationActivity::class.java)
    }

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    private lateinit var binding: ActivityVerificationBinding
    private val verificationViewModel: VerificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        verificationViewModel.navigateToMainWithVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToMain()
            }
        }

        verificationViewModel.showContinueButton.observe(this) {
            it.getContentIfNotHandled()?.let {
                binding.btnLoginAV.isVisible = true
            }
        }

        verificationViewModel.sendEmail.observe(this) {
            it.getContentIfNotHandled()?.let {
                verificationViewModel.sendEmailVerification()
            }
        }

        verificationViewModel.emailVerified.observe(this) {
            binding.txvInstructionsAV.text = spanEmail( "Hemos enviado un email a\n",it,"\nPor favor, verifica tu correo electrónico.")
        }
    }

    private fun goToMain() {
        startActivity(MainActivity.create(this))
    }

    private fun initListeners() {
        binding.btnLoginAV.setOnClickListener { verificationViewModel.onGoToMainSelected() }
        binding.txvRegisterNowAV.setOnClickListener { verificationViewModel.onSendEmail() }
    }
}