package com.sokamn.trovami.ui.auth.verification

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.ex.spanSecondBold
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.data.source.datastore.DataStoreConstants.USER_KEY_PREFS
import com.sokamn.trovami.databinding.ActivityVerificationBinding
import com.sokamn.trovami.ui.MainActivity
import com.sokamn.trovami.ui.auth.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context, currentUserUid: String): Intent =
            Intent(context, VerificationActivity::class.java).apply {
                putExtra(USER_KEY_PREFS, currentUserUid)
            }
    }

    private lateinit var binding: ActivityVerificationBinding

    private val verificationViewModel: VerificationViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        initUI()

    }

    private fun initUI() {
        setUIComponents()
        initListeners()
        initObservers()
    }

    private fun setUIComponents() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun initObservers() {
        var tries = 4

        verificationViewModel.navigateToMain.observe(this) {
            it.getContentIfNotHandled()?.let { currentUserUid ->
                goToMain(currentUserUid)
            }
        }

        verificationViewModel.showContinueButton.observe(this) {
            it.getContentIfNotHandled()?.let { isEnabled ->
                binding.btnConfirmVerification.isEnabled = isEnabled
            }
        }

        verificationViewModel.sendEmail.observe(this) {
            it.getContentIfNotHandled()?.let {
                if (tries in 1..4){
                    verificationViewModel.sendEmailVerification()
                    tries--
                    toast("Hemos enviado un correo de verificación, le quedan $tries intentos", Toast.LENGTH_LONG)
                }else{
                    toast("No te quedan más intentos para enviar un correo de verificación, intentalo más tarde", Toast.LENGTH_LONG)
                }
            }
        }

        verificationViewModel.navigateToBack.observe(this) {
            it.getContentIfNotHandled()?.let {
                goBack()
            }
        }

        verificationViewModel.emailVerified.observe(this, Observer {
            it.getContentIfNotHandled()?.let { email ->
                binding.txvInstructionsAV.spanSecondBold(this, "Hemos enviado un email a\n",email,"\nPor favor, verifica tu correo electrónico.")

            }
        })

    }

    private fun initListeners() {
        binding.btnConfirmVerification.setOnClickListener { verificationViewModel.onGoToMainSelected() }
        binding.txvResendEmail.setOnClickListener { verificationViewModel.onSendEmail() }
        binding.imvBackAV.setOnClickListener { verificationViewModel.onGoToBackSelected() }
    }



    private fun goBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun goToMain(currentUserUid: String) {
        startActivity(MainActivity.create(this, currentUserUid))
    }


}