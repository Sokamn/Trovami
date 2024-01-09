package com.sokamn.trovami.ui.auth.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.ex.dismissKeyboard
import com.sokamn.trovami.core.ex.loseFocusAfterActionDone
import com.sokamn.trovami.core.ex.onTextChanged
import com.sokamn.trovami.core.ex.show
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivityLoginBinding
import com.sokamn.trovami.domain.model.UserLogin
import com.sokamn.trovami.ui.MainActivity
import com.sokamn.trovami.ui.auth.signin.SignInActivity
import com.sokamn.trovami.ui.auth.verification.VerificationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }
    private lateinit var binding: ActivityLoginBinding

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
        setUIComponents()
        initListeners()
        initObservers()
    }

    private fun setUIComponents() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun initObservers() {

        loginViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let { params->
                goToSignIn( // ULTRA NEGRADA MÁXIMA CORREGIR CON OBJETO EN ALGUN MOMENTO
                    loginMethod = params[0].toInt(),
                    lastActivity = params[1],
                    nName = params[2],
                    gMail = params[3]
                )
            }
        }

        loginViewModel.navigateToMain.observe(this){
            it.getContentIfNotHandled()?.let{ currentUserUid ->
                goToMain(currentUserUid)
            }
        }

        loginViewModel.navigateToForgotPassword.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToRecoveryPassword()
            }
        }

        loginViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {currentUserUid ->
                goToVerify(currentUserUid)
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showCantFindErrorDialog(userLogin)
        }

        loginViewModel.showNetworkErrorDialog.observe(this) {
            it.getContentIfNotHandled()?.let{
                showNetworkErrorDialog()
            }
        }

        loginViewModel.googleClient.observe(this) { client ->
            launcher.launch(client.signInIntent)
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun initListeners() {
        with(binding){

            txpEmailAL.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) scrollViewAL.smoothScrollTo(view.left, view.bottom)
            }
            txpEmailAL.onTextChanged { onFieldChanged() }

            txpPasswordAL.loseFocusAfterActionDone(scrollViewAL)
            txpPasswordAL.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) scrollViewAL.smoothScrollTo(view.left, view.bottom+150)
                onFieldChanged(hasFocus)
            }
            txpPasswordAL.onTextChanged { onFieldChanged() }

            txvRecoveryPassAL.setOnClickListener { loginViewModel.onForgotPasswordSelected() }

            txvRegisterNowAL.setOnClickListener { loginViewModel.onEmailSignInSelected() }

            //imvFacebookAL.setOnClickListener { loginViewModel.onSignInSelected(FACEBOOK, "LoginActivity") }

            crdGoogleAL.setOnClickListener { loginViewModel.onGoogleSignInSelected(this@LoginActivity) }
            crdFacebookAL.setOnClickListener { toast("Facebook será implementado en próximas versiones") }

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
            tilEmailAL.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            tilPasswordAL.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
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

    private fun showCantFindErrorDialog(userLogin: UserLogin) {
        ErrorDialog.create(
            title = getString(R.string.login_error_dialog_title),
            description = getString(R.string.login_error_dialog_body),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                loginViewModel.onLoginSelected(
                    userLogin.email,
                    userLogin.password
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun showNetworkErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_title),
            description = getString(R.string.signin_network_error_description),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                loginViewModel.onGoogleSignInSelected(this@LoginActivity)
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToRecoveryPassword() {
        //startActivity(PasswordRecoveryActivity.create(this))
    }

    private fun goToSignIn(
        loginMethod: Int,
        lastActivity: String,
        nName: String,
        gMail: String
    ) {
        startActivity(SignInActivity.create(this,loginMethod,lastActivity,nName,gMail))
    }

    private fun goToMain(currentUser: String) {
        startActivity(MainActivity.create(this, currentUser))
    }

    private fun goToVerify(currentUser: String) {
        startActivity(VerificationActivity.create(this, currentUser))
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                if (account != null) {
                    loginViewModel.googleSignIn(task.result)
                } else {
                    toast("Ocurrió un error inesperado. Por favor, intentelo más tarde...")
                }
            }else{
                toast("Ocurrió un error inesperado. Por favor, intentelo más tarde...")
            }
        }
    }
}

