package com.sokamn.trovami.presentation.signup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.dialog.MasterDialog
import com.sokamn.trovami.core.ex.loseFocusAfterAction
import com.sokamn.trovami.core.ex.onTextChanged
import com.sokamn.trovami.core.ex.show
import com.sokamn.trovami.databinding.ActivitySignUpBinding
import com.sokamn.trovami.domain.model.User
import com.sokamn.trovami.presentation.login.LoginActivity
import com.sokamn.trovami.presentation.verification.VerificationActivity
import com.sokamn.trovami.util.AppConstants.Companion.AIRSERVICE
import com.sokamn.trovami.util.AppConstants.Companion.CARPENTER
import com.sokamn.trovami.util.AppConstants.Companion.ELECTRICIAN
import com.sokamn.trovami.util.AppConstants.Companion.EMAIL
import com.sokamn.trovami.util.AppConstants.Companion.FACEBOOK
import com.sokamn.trovami.util.AppConstants.Companion.GARDENER
import com.sokamn.trovami.util.AppConstants.Companion.GAS
import com.sokamn.trovami.util.AppConstants.Companion.GOOGLE
import com.sokamn.trovami.util.AppConstants.Companion.MASON
import com.sokamn.trovami.util.AppConstants.Companion.NANNY
import com.sokamn.trovami.util.AppConstants.Companion.PAINTER
import com.sokamn.trovami.util.AppConstants.Companion.PASO1
import com.sokamn.trovami.util.AppConstants.Companion.PASO2
import com.sokamn.trovami.util.AppConstants.Companion.PASO3
import com.sokamn.trovami.util.AppConstants.Companion.PASO4
import com.sokamn.trovami.util.AppConstants.Companion.PCTECHNICIAN
import com.sokamn.trovami.util.AppConstants.Companion.PLUMBER
import com.sokamn.trovami.util.AppConstants.Companion.SELECTED
import com.sokamn.trovami.util.AppConstants.Companion.TRUCKFREIGHTER
import com.sokamn.trovami.util.AppConstants.Companion.TRUCKMOVING
import com.sokamn.trovami.util.AppConstants.Companion.UNSELECTED
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {


    companion object {
        fun create(context: Context, loginMethod: Int, lastActivity: String): Intent {
            return Intent(context, SignUpActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("loginMethod", loginMethod)
                putExtra("lastActivity",lastActivity)
            }
        }
    }


    private lateinit var binding: ActivitySignUpBinding
    private var loginMethod = 0
    private var lastActivity = ""
    private var counter = 0
    private var user = User()
    private val signUpViewModel: SignUpViewModel by viewModels()


    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
        getDetails()
    }

    private fun getDetails() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        lastActivity = intent.getStringExtra("lastActivity").toString()
        loginMethod = intent.getIntExtra("loginMethod",0)
    }

    private fun initObservers() {
        signUpViewModel.navigateToVerifyEmail.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerifyEmail()
            }
        }

        signUpViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }

        lifecycleScope.launchWhenStarted {
            signUpViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        signUpViewModel.showErrorDialog.observe(this) { showError ->
            if (showError) showErrorDialog()
        }

        signUpViewModel.showErrorInputs.observe(this) { showError ->
            if (showError) showErrorInputs()
        }
    }

    private fun initListeners() {
        with(binding){

            txpEmailASU.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpEmailASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpEmailASU.onTextChanged { onFieldChanged() }

            txpFullNameASU.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpFullNameASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpFullNameASU.onTextChanged { onFieldChanged() }

            txpDocumentASU.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
            txpDocumentASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpDocumentASU.onTextChanged { onFieldChanged() }

            txpProvinceASU.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpProvinceASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpProvinceASU.onTextChanged { onFieldChanged() }

            txpMunicipalityASU.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpMunicipalityASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpMunicipalityASU.onTextChanged { onFieldChanged() }

            txpAddressASU.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
            txpAddressASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpAddressASU.onTextChanged { onFieldChanged() }

            txpPhoneASU.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpPhoneASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpPhoneASU.onTextChanged { onFieldChanged() }

            txpPasswordASU.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            txpPasswordASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpPasswordASU.onTextChanged { onFieldChanged() }

            txpRepeatPasswordASU.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
            txpRepeatPasswordASU.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            txpRepeatPasswordASU.onTextChanged { onFieldChanged() }

            txvHaveAccountAR.setOnClickListener { signUpViewModel.onLoginSelected() }

            onBackPressedDispatcher.addCallback(this@SignUpActivity, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when(counter){
                        PASO1->{
                            finish()
                            FirebaseAuth.getInstance().signOut()
                        }
                        PASO2->{
                            register1()
                        }
                        PASO3->{
                            register2()
                        }
                        PASO4->{
                            register3()
                            btnContinueAR.text = resources.getText(R.string.continuee)
                        }
                    }
                    counter--
                }
            })

            btnContinueAR.setOnClickListener {
                checkValue(counter)
                counter++
            }
            imvBackASU.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            imvNannyASU.setOnClickListener {
                onJobClickFunction(NANNY,binding.imvNanny)
            }
            imvAirServiceASU.setOnClickListener {
                onJobClickFunction(AIRSERVICE,binding.imvAirService)
            }
            imvElectricianASU.setOnClickListener {
                onJobClickFunction(ELECTRICIAN,binding.imvElectrician)
            }
            imvGardenerASU.setOnClickListener {
                onJobClickFunction(GARDENER,binding.imvGardener)
            }
            imvCarpenterASU.setOnClickListener {
                onJobClickFunction(CARPENTER,binding.imvCarpenter)
            }
            imvPlumberASU.setOnClickListener {
                onJobClickFunction(PLUMBER,binding.imvPlumber)
            }
            imvPainterASU.setOnClickListener {
                onJobClickFunction(PAINTER,binding.imvPainter)
            }
            imvElectricianASU.setOnClickListener {
                onJobClickFunction(ELECTRICIAN,binding.imvElectrician)
            }
            imvTruckFreightASU.setOnClickListener {
                onJobClickFunction(TRUCKFREIGHTER,binding.imvTruckFreight)
            }
            imvTruckMovingASU.setOnClickListener {
                onJobClickFunction(TRUCKMOVING,binding.imvTruckMoving)
            }
            imvMasonASU.setOnClickListener {
                onJobClickFunction(MASON,binding.imvMason)
            }
            imvGasASU.setOnClickListener {
                onJobClickFunction(GAS,binding.imvGas)
            }
            imvPCTechnicianASU.setOnClickListener {
                onJobClickFunction(PCTECHNICIAN,binding.imvPCTechnician)
            }
        }
    }

    private fun updateUI(viewState: SignInViewState) {
        with(binding) {
            pgbProgressSignUp.isVisible = viewState.isLoading
            tilEmailASU.error =
                if (viewState.isValidEmail) null else getString(R.string.signin_error_mail)
            tilFullNameASU.error =
                if (viewState.isValidFullName) null else getString(R.string.signin_error_fullname)
            tilDocumentASU.error =
                if (viewState.isValidDocument) null else getString(R.string.signin_error_document)
            tilProvinceASU.error =
                if (viewState.isValidProvince) null else getString(R.string.signin_error_province)
            tilMunicipalityASU.error =
                if (viewState.isValidMunicipality) null else getString(R.string.signin_error_municipality)
            tilAddressASU.error =
                if (viewState.isValidAddress) null else getString(R.string.signin_error_address)
            tilPhoneASU.error =
                if (viewState.isValidPhone) null else getString(R.string.signin_error_phone)
            tilPasswordASU.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
            tilRepeatPasswordASU.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
        }
    }

    internal fun onJobClickFunction(master: Int, imvMaster: ImageView) {
        if(imvMaster.tag == SELECTED){
            imvMaster.tag = UNSELECTED
            when(master){
                PAINTER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_painter)
                        .into(imvMaster)
                }
                CARPENTER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_carpenter)
                        .into(imvMaster)
                }
                AIRSERVICE ->{
                    Glide.with(this)
                        .load(R.drawable.ic_air_service)
                        .into(imvMaster)
                }
                GARDENER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_gardener)
                        .into(imvMaster)
                }
                TRUCKMOVING ->{
                    Glide.with(this)
                        .load(R.drawable.ic_truck_moving)
                        .into(imvMaster)
                }
                TRUCKFREIGHTER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_truck_freight)
                        .into(imvMaster)
                }
                MASON ->{
                    Glide.with(this)
                        .load(R.drawable.ic_mason)
                        .into(imvMaster)
                }
                NANNY ->{
                    Glide.with(this)
                        .load(R.drawable.ic_nanny)
                        .into(imvMaster)
                }
                GAS ->{
                    Glide.with(this)
                        .load(R.drawable.ic_gas)
                        .into(imvMaster)
                }
                PCTECHNICIAN ->{
                    Glide.with(this)
                        .load(R.drawable.ic_pc_technician)
                        .into(imvMaster)
                }
                PLUMBER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_plumber)
                        .into(imvMaster)
                }
                ELECTRICIAN ->{
                    Glide.with(this)
                        .load(R.drawable.ic_electrician)
                        .into(imvMaster)
                }
            }
            user.masterList.removeIf { it.id == master }
        }else{
            showMasterDialog(master, imvMaster)
            imvMaster.tag = SELECTED
            when(master){
                PAINTER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_painter_selected)
                        .into(imvMaster)
                }
                CARPENTER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_carpenter_selected)
                        .into(imvMaster)
                }
                AIRSERVICE ->{
                    Glide.with(this)
                        .load(R.drawable.ic_air_service_selected)
                        .into(imvMaster)
                }
                GARDENER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_gardener_selected)
                        .into(imvMaster)
                }
                TRUCKMOVING ->{
                    Glide.with(this)
                        .load(R.drawable.ic_truck_moving_selected)
                        .into(imvMaster)
                }
                TRUCKFREIGHTER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_truck_freight_selected)
                        .into(imvMaster)
                }
                MASON ->{
                    Glide.with(this)
                        .load(R.drawable.ic_mason_selected)
                        .into(imvMaster)
                }
                NANNY ->{
                    Glide.with(this)
                        .load(R.drawable.ic_nanny_selected)
                        .into(imvMaster)
                }
                GAS ->{
                    Glide.with(this)
                        .load(R.drawable.ic_gas_selected)
                        .into(imvMaster)
                }
                PCTECHNICIAN ->{
                    Glide.with(this)
                        .load(R.drawable.ic_pc_technician_selected)
                        .into(imvMaster)
                }
                PLUMBER ->{
                    Glide.with(this)
                        .load(R.drawable.ic_plumber_selected)
                        .into(imvMaster)
                }
                ELECTRICIAN ->{
                    Glide.with(this)
                        .load(R.drawable.ic_electrician_selected)
                        .into(imvMaster)
                }
            }

        }
    }

    private fun checkValue(count: Int){
        when(count){
            PASO1 -> register2()
            PASO2 -> register3()
            PASO3 -> register4()
            PASO4 -> {
                finishButton()
                counter--
            }
        }
    }

    private fun showMasterDialog(master: Int, imvMaster: ImageView ) {
        MasterDialog.create(
            master = master,
            masterList = user.masterList,
            imvMaster = imvMaster,
            activity = this
            ).show(dialogLauncher, this)
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.saveChanges),
            description = getString(R.string.saveChanges),
            positiveAction = ErrorDialog.Action(getString(R.string.saveChanges)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun showErrorInputs() {
        ErrorDialog.create(
            title = getString(R.string.saveChanges),
            description = getString(R.string.saveChanges),
            positiveAction = ErrorDialog.Action(getString(R.string.saveChanges)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun finishButton() {
        when(loginMethod){
            EMAIL ->{
                signUpViewModel.onSignUpSelected(user,binding.txpRepeatPasswordASU.text.toString())
            }
            GOOGLE ->{

            }
            FACEBOOK ->{

            }
        }
    }

    private fun register1(){
        with(binding){
            //Paso 1
            txvSubtitleASU.setText(R.string.registerDescr)
            tilEmailASU.visibility = View.VISIBLE
            tilFullNameASU.visibility = View.VISIBLE
            tilDocumentASU.visibility = View.VISIBLE
            binding.imvBGASU.setImageResource(R.drawable.bg_sign_up_s1)
            //Paso 2
            tilProvinceASU.visibility = View.GONE
            tilMunicipalityASU.visibility = View.GONE
            tilAddressASU.visibility = View.GONE
            //Paso 3
            tilPhoneASU.visibility = View.GONE
            tilPasswordASU.visibility = View.GONE
            tilRepeatPasswordASU.visibility = View.GONE
            //Paso 4
            txvPolicyASU.visibility = View.GONE
            imvAirServiceASU.visibility = View.GONE
            imvElectricianASU.visibility = View.GONE
            imvGardenerASU.visibility = View.GONE
            imvCarpenterASU.visibility = View.GONE
            imvPlumberASU.visibility = View.GONE
            imvPainterASU.visibility = View.GONE
            imvElectricianASU.visibility = View.GONE
            imvTruckFreightASU.visibility = View.GONE
            imvTruckMovingASU.visibility = View.GONE
            imvMasonASU.visibility = View.GONE
            imvNannyASU.visibility = View.GONE
            imvGasASU.visibility = View.GONE
            imvPCTechnicianASU.visibility = View.GONE
        }
    }
    private fun register2(){
        with(binding){
            //Paso 1
            txvSubtitleASU.setText(R.string.registerDescr)
            tilEmailASU.visibility = View.INVISIBLE
            tilFullNameASU.visibility = View.INVISIBLE
            tilDocumentASU.visibility = View.INVISIBLE
            //Paso 2
            tilProvinceASU.visibility = View.VISIBLE
            tilMunicipalityASU.visibility = View.VISIBLE
            tilAddressASU.visibility = View.VISIBLE
            binding.imvBGASU.setImageResource(R.drawable.bg_sign_up_s2)
            //Paso 3
            tilPhoneASU.visibility = View.GONE
            tilPasswordASU.visibility = View.GONE
            tilRepeatPasswordASU.visibility = View.GONE
            //Paso 4
            txvPolicyASU.visibility = View.GONE
            imvAirServiceASU.visibility = View.GONE
            imvElectricianASU.visibility = View.GONE
            imvGardenerASU.visibility = View.GONE
            imvCarpenterASU.visibility = View.GONE
            imvPlumberASU.visibility = View.GONE
            imvPainterASU.visibility = View.GONE
            imvElectricianASU.visibility = View.GONE
            imvTruckFreightASU.visibility = View.GONE
            imvTruckMovingASU.visibility = View.GONE
            imvMasonASU.visibility = View.GONE
            imvNannyASU.visibility = View.GONE
            imvGasASU.visibility = View.GONE
            imvPCTechnicianASU.visibility = View.GONE
        }
    }
    private fun register3(){
        with(binding){
            //Paso 1
            txvSubtitleASU.setText(R.string.registerDescr)
            tilEmailASU.visibility = View.INVISIBLE
            tilFullNameASU.visibility = View.INVISIBLE
            tilDocumentASU.visibility = View.INVISIBLE
            //Paso 2
            tilProvinceASU.visibility = View.GONE
            tilMunicipalityASU.visibility = View.GONE
            tilAddressASU.visibility = View.GONE
            //Paso 3
            tilPhoneASU.visibility = View.VISIBLE
            tilPasswordASU.visibility = View.VISIBLE
            tilRepeatPasswordASU.visibility = View.VISIBLE
            binding.imvBGASU.setImageResource(R.drawable.bg_sign_up_s3)
            //Paso 4
            txvPolicyASU.visibility = View.GONE
            imvAirServiceASU.visibility = View.GONE
            imvElectricianASU.visibility = View.GONE
            imvGardenerASU.visibility = View.GONE
            imvCarpenterASU.visibility = View.GONE
            imvPlumberASU.visibility = View.GONE
            imvPainterASU.visibility = View.GONE
            imvElectricianASU.visibility = View.GONE
            imvTruckFreightASU.visibility = View.GONE
            imvTruckMovingASU.visibility = View.GONE
            imvMasonASU.visibility = View.GONE
            imvNannyASU.visibility = View.GONE
            imvGasASU.visibility = View.GONE
            imvPCTechnicianASU.visibility = View.GONE
        }
    }
    private fun register4(){
        with(binding){
            //Paso 1
            tilEmailASU.visibility = View.INVISIBLE
            tilFullNameASU.visibility = View.INVISIBLE
            tilDocumentASU.visibility = View.INVISIBLE
            //Paso 2
            tilProvinceASU.visibility = View.GONE
            tilMunicipalityASU.visibility = View.GONE
            tilAddressASU.visibility = View.GONE
            //Paso 3
            tilPhoneASU.visibility = View.GONE
            tilPasswordASU.visibility = View.GONE
            tilRepeatPasswordASU.visibility = View.GONE
            //Paso 4
            txvSubtitleASU.setText(R.string.selectMasters)
            txvPolicyASU.visibility = View.GONE
            imvAirServiceASU.visibility = View.VISIBLE
            imvElectricianASU.visibility = View.VISIBLE
            imvGardenerASU.visibility = View.VISIBLE
            imvCarpenterASU.visibility = View.VISIBLE
            imvPlumberASU.visibility = View.VISIBLE
            imvPainterASU.visibility = View.VISIBLE
            imvElectricianASU.visibility = View.VISIBLE
            imvTruckFreightASU.visibility = View.VISIBLE
            imvTruckMovingASU.visibility = View.VISIBLE
            imvMasonASU.visibility = View.VISIBLE
            imvNannyASU.visibility = View.VISIBLE
            imvGasASU.visibility = View.VISIBLE
            imvPCTechnicianASU.visibility = View.VISIBLE
            binding.imvBGASU.setImageResource(R.drawable.bg_sign_up_s4)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            signUpViewModel.onFieldsChanged(User("",
                fullName = binding.txpFullNameASU.text.toString(),
                "",
                document = binding.txpDocumentASU.text.toString(),
                phoneNumber = binding.txpPhoneASU.text.toString(),
                password = binding.txpPasswordASU.text.toString(),
                email = binding.txpEmailASU.text.toString(),
                defaultAdress = binding.txpAddressASU.text.toString(),
                province = binding.txpProvinceASU.text.toString(),
                municipality = binding.txpMunicipalityASU.text.toString()),
                passwordConfirmation = binding.txpRepeatPasswordASU.text.toString())
        }
    }

    private fun goToVerifyEmail() {
        startActivity(VerificationActivity.create(this))
    }

    private fun goToLogin() {
        if(lastActivity=="IntroductionActivity"){
            startActivity(LoginActivity.create(this))
            finish()
        }else{
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
    }
}