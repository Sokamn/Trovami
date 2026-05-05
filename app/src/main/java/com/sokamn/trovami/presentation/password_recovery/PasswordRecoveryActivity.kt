package com.sokamn.trovami.presentation.password_recovery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivityPasswordRecoveryBinding
import com.sokamn.trovami.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PasswordRecoveryActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, PasswordRecoveryActivity::class.java)
    }

    private lateinit var binding: ActivityPasswordRecoveryBinding
    private val passwordRecoveryViewModel: PasswordRecoveryViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

    }

    private fun initUI() {
        setViewDesign()
        initListeners()
        initObservers()
    }

    private fun setViewDesign() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun initObservers() {
        passwordRecoveryViewModel.passwordSent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { emailSent ->
                if(emailSent){
                    toast("Te hemos enviado un mail con información para que recuperes tu contraseña")
                }else{
                    toast("Ingrese el email de una cuenta valida, o verifica tu conexión a internet")
                }
            }
        })

        passwordRecoveryViewModel.navigateToLogin.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        })
    }

    private fun initListeners() {
        with(binding){
            btnLoginAPR.setOnClickListener {
                val email = binding.txpEmailAPR.text.toString()
                if(email.isNotEmpty()){
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        passwordRecoveryViewModel.sendPasswordLink(email)
                    }else{
                        toast("Por favor, ingresa un email")
                    }
                }else{
                    toast("Primero debes de ingresar el email asociado a tu cuenta")
                }
            }

            imvBackAPR.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun goToLogin(){
        finish()
        startActivity(LoginActivity.create(this))
    }
}