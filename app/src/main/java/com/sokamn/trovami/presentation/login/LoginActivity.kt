package com.sokamn.trovami.presentation.login

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.ex.*
import com.sokamn.trovami.databinding.ActivityLoginBinding
import com.sokamn.trovami.domain.model.UserLogin
import com.sokamn.trovami.presentation.MainActivity
import com.sokamn.trovami.presentation.password_recovery.PasswordRecoveryActivity
import com.sokamn.trovami.presentation.signup.SignUpActivity
import com.sokamn.trovami.presentation.verification.VerificationActivity
import com.sokamn.trovami.util.AppConstants.Companion.EMAIL
import com.sokamn.trovami.util.AppConstants.Companion.FACEBOOK
import com.sokamn.trovami.util.AppConstants.Companion.GOOGLE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }

    private lateinit var binding: ActivityLoginBinding
    private var loginMethod = 0
    private var lastActivity = ""
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        loginViewModel.navigateToDetails.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToMain()
            }
        }

        loginViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSignUp(this.loginMethod, this.lastActivity)
            }
        }

        loginViewModel.loginMethod.observe(this){ loginMethod ->
            this.loginMethod = loginMethod
        }

        loginViewModel.lastActivity.observe(this){ lastActivity ->
            this.lastActivity = lastActivity
        }

        loginViewModel.navigateToForgotPassword.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToRecoveryPassword()
            }
        }

        loginViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerify()
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun initListeners() {
        with(binding){

            txpEmailAL.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpEmailAL.onTextChanged { onFieldChanged() }

            txpPasswordAL.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
            txpPasswordAL.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpPasswordAL.onTextChanged { onFieldChanged() }

            txvRecoveryPassAL.setOnClickListener { loginViewModel.onForgotPasswordSelected() }

            txvRegisterNowAL.setOnClickListener { loginViewModel.onSignInSelected(EMAIL, "LoginActivity") }

            //imvFacebookAL.setOnClickListener { loginViewModel.onSignInSelected(FACEBOOK, "LoginActivity") }

            //imvGoogleAL.setOnClickListener { loginViewModel.onSignInSelected(GOOGLE, "LoginActivity") }

            btnLoginAL.setOnClickListener {
                it.dismissKeyboard()
                loginViewModel.onLoginSelected(
                    txpEmailAL.text.toString(),
                    txpPasswordAL.text.toString()
                )
            }
            txvRecoveryPassAL.setOnClickListener{ goToRecoveryPassword() }

            imvBackAL.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun updateUI(viewState: LoginViewState) {
        with(binding) {
            pgbProgressLogin.isVisible = viewState.isLoading
            if (viewState.isValidEmail) null else toast("No hemos encontrado una cuenta asociada a este email")
            if (viewState.isValidPassword) null else toast("La contraseña debe contener al menos 6 caracteres")
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            loginViewModel.onFieldsChanged(
                email = binding.txpEmailAL.text.toString(),
                password = binding.txpPasswordAL.text.toString()
            )
        }
    }

    private fun showErrorDialog(userLogin: UserLogin) {
        ErrorDialog.create(
            title = getString(R.string.saveChanges),
            description = getString(R.string.saveChanges),
            negativeAction = ErrorDialog.Action(getString(R.string.saveChanges)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.saveChanges)) {
                loginViewModel.onLoginSelected(
                    userLogin.email,
                    userLogin.password
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToRecoveryPassword() {
        startActivity(PasswordRecoveryActivity.create(this))
    }

    private fun goToSignUp(loginMethod: Int, lastActivity: String) {
        startActivity(SignUpActivity.create(this,loginMethod,lastActivity))
    }

    private fun goToMain() {
        startActivity(MainActivity.create(this))
        finish()
    }

    private fun goToVerify() {
        startActivity(VerificationActivity.create(this))
    }
}