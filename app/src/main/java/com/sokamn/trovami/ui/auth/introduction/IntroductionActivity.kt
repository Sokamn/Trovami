package com.sokamn.trovami.ui.auth.introduction

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.ex.show
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivityIntroductionBinding
import com.sokamn.trovami.ui.MainActivity
import com.sokamn.trovami.ui.auth.login.LoginActivity
import com.sokamn.trovami.ui.auth.login.LoginViewState
import com.sokamn.trovami.ui.auth.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, IntroductionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

    private lateinit var binding: ActivityIntroductionBinding
    private val introductionViewModel: IntroductionViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun initListeners() {
        with(binding) {
            btnLogInAI.setOnClickListener { introductionViewModel.onLoginSelected() }
            btnRegisterNowAI.setOnClickListener { introductionViewModel.onEmailSignInSelected() }
            imvGoogleAI.setOnClickListener { introductionViewModel.onGoogleSignInSelected(this@IntroductionActivity) }
            imvFacebookAI.setOnClickListener { toast("Facebook será implementado en próximas versiones") }
        }
    }


    private fun initObservers() {
        introductionViewModel.navigateToLogin.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        })

        introductionViewModel.navigateToMain.observe(this, Observer {
            it.getContentIfNotHandled()?.let { currentUserUid ->
                goToMain(currentUserUid)
            }
        })

        introductionViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let { params ->
                goToSignIn( // ULTRA NEGRADA MÁXIMA CORREGIR CON OBJETO EN ALGUN MOMENTO
                    loginMethod = params[0].toInt(),
                    lastActivity = params[1],
                    nName = params[2],
                    gMail = params[3],
                    userUid = params[4]
                )
            }
        }

        introductionViewModel.googleClient.observe(this) { client ->
            launcher.launch(client.signInIntent)
        }

        introductionViewModel.showNetworkErrorDialog.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                showNetworkErrorDialog()
            }
        })

        lifecycleScope.launchWhenStarted {
            introductionViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun updateUI(viewState: LoginViewState) {
        binding.pgbProgressIntroduction.isVisible = viewState.isLoading
    }

    private fun showNetworkErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_title),
            description = getString(R.string.signin_network_error_description),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                introductionViewModel.onGoogleSignInSelected(this@IntroductionActivity)
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun goToMain(currentUserUid: String) {
        startActivity(MainActivity.create(this, currentUserUid))
    }

    private fun goToSignIn(
        loginMethod: Int,
        lastActivity: String,
        nName: String,
        gMail: String,
        userUid: String
    ) {
        startActivity(SignInActivity.create(this, loginMethod, lastActivity, nName, gMail, userUid))
    }

    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account = task.result
                    if (account != null) {
                        introductionViewModel.googleSignIn(task.result)
                    } else {
                        toast("Ocurrió un error inesperado. Por favor, intentelo más tarde...")
                    }
                } else {
                    toast("Ocurrió un error inesperado. Por favor, intentelo más tarde...")
                }
            }
        }
}