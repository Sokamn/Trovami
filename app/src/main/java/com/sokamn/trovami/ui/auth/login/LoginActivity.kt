package com.sokamn.trovami.ui.auth.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.ex.dismissKeyboard
import com.sokamn.trovami.core.ex.loseFocusAfterAction
import com.sokamn.trovami.core.ex.onTextChanged
import com.sokamn.trovami.core.ex.show
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivityLoginBinding
import com.sokamn.trovami.domain.model.UserLogin
import com.sokamn.trovami.ui.MainActivity
import com.sokamn.trovami.utils.AppConstants
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
            it.getContentIfNotHandled()?.let {
                //goToSignUp(this.loginMethod, this.lastActivity,this.nName,this.gMail,this.profilePicture,this.currentUser)
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
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
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

            txpPasswordAL.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE, scrollViewAL)
            txpPasswordAL.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) scrollViewAL.smoothScrollTo(view.left, view.bottom+150)
                onFieldChanged(hasFocus)
            }
            txpPasswordAL.onTextChanged { onFieldChanged() }

            txvRecoveryPassAL.setOnClickListener { loginViewModel.onForgotPasswordSelected() }

            txvRegisterNowAL.setOnClickListener { loginViewModel.onSignUpSelected(AppConstants.EMAIL, "LoginActivity",this@LoginActivity) }

            //imvFacebookAL.setOnClickListener { loginViewModel.onSignInSelected(FACEBOOK, "LoginActivity") }

            crdGoogleAL.setOnClickListener { loginViewModel.onSignUpSelected(AppConstants.GOOGLE, "LoginActivity",this@LoginActivity) }
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

    private fun showErrorDialog(userLogin: UserLogin) {
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

    private fun goToRecoveryPassword() {
        //startActivity(PasswordRecoveryActivity.create(this))
    }

    private fun goToSignUp(
        loginMethod: Int,
        lastActivity: String,
        nName: String,
        gMail: String,
        profilePicture: String,
        currentUser: String
    ) {
        //startActivity(SignUpActivity.create(this,loginMethod,lastActivity,nName,gMail,profilePicture, currentUser))
    }

    private fun goToMain(currentUser: String) {
        startActivity(MainActivity.create(this, currentUser))
    }

    private fun goToVerify(currentUser: String) {
        //startActivity(VerificationActivity.create(this, currentUser))
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                if (account != null) {
                    loginViewModel.onGoogleSelected(task.result)
                } else {
                    toast("Ocurrió un error inesperado. Por favor, intentelo más tarde...")
                }
            }else{
                toast("Ocurrió un error inesperado. Por favor, intentelo más tarde...")
            }
        }
    }
}

