package com.sokamn.trovami.ui.auth.recover_password

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.ex.loseFocusAfterActionDone
import com.sokamn.trovami.core.ex.onTextChanged
import com.sokamn.trovami.core.ex.show
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivityRecoverPasswordBinding
import com.sokamn.trovami.ui.auth.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecoverPasswordActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, RecoverPasswordActivity::class.java)
    }

    private lateinit var binding: ActivityRecoverPasswordBinding
    private val passwordRecoveryViewModel: RecoverPasswordViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        setUIComponents()
        initListeners()
        initObservers()
    }

    private fun setUIComponents() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun initObservers() {
        passwordRecoveryViewModel.showSuccessDialog.observe(this){
            it.getContentIfNotHandled()?.let {
                showSuccessDialog()
            }
        }

        passwordRecoveryViewModel.navigateToLogin.observe(this){
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }

        passwordRecoveryViewModel.showErrorDialog.observe(this){
            it.getContentIfNotHandled()?.let {
                showErrorDialog()
            }
        }

        passwordRecoveryViewModel.showInputError.observe(this){
            it.getContentIfNotHandled()?.let { emailViewState ->
                if (emailViewState) null else getString(R.string.signin_error_mail)
            }
        }
    }

    private fun initListeners() {
        with(binding){
            txpEmailAPR.loseFocusAfterActionDone(scrollViewAPR)
            txpEmailAPR.onTextChanged {
                if (passwordRecoveryViewModel.isValidOrEmptyEmail(email = binding.txpEmailAPR.text.toString()))
                    tilEmailAPR.error = null
                else
                    tilEmailAPR.error = getString(R.string.signin_error_mail)
            }


            btnSendRecoverPetitionAPR.setOnClickListener {
                sendPasswordResetPetition()
            }

            imvBackAPR.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun sendPasswordResetPetition() {
        passwordRecoveryViewModel.sendPasswordPetition(email = binding.txpEmailAPR.text.toString())
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_title),
            description = getString(R.string.recover_password_error),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                sendPasswordResetPetition()
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun showSuccessDialog() {
        ErrorDialog.create(
            title = getString(R.string.recovery_pass),
            description = getString(R.string.recover_password_step),
            positiveAction = ErrorDialog.Action(getString(R.string.accept)) {
                passwordRecoveryViewModel.onGoToLogin()
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToLogin(){
        finish()
        startActivity(LoginActivity.create(this))
    }
}