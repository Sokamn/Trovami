package com.sokamn.trovami.ui.auth.signin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sokamn.trovami.R
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher
import com.sokamn.trovami.core.dialog.ErrorDialog
import com.sokamn.trovami.core.ex.show
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.databinding.ActivitySignInBinding
import com.sokamn.trovami.ui.MainActivity
import com.sokamn.trovami.ui.auth.login.LoginActivity
import com.sokamn.trovami.utils.AuthConstants.CURRENT_USER_UID_KEY_EXTRA
import com.sokamn.trovami.utils.AuthConstants.GMAIL_KEY_EXTRA
import com.sokamn.trovami.utils.AuthConstants.GOOGLE
import com.sokamn.trovami.utils.AuthConstants.LAST_ACTIVITY_KEY_EXTRA
import com.sokamn.trovami.utils.AuthConstants.LOGIN_METHOD_KEY_EXTRA
import com.sokamn.trovami.utils.AuthConstants.NAME_KEY_EXTRA
import com.sokamn.trovami.utils.AuthConstants.PROFILE_PICTURE_KEY_EXTRA
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context, loginMethod: Int, lastActivity: String, nName: String, gMail: String, profilePicture: String, currentUserUid: String): Intent {
            return Intent(context, SignInActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(LAST_ACTIVITY_KEY_EXTRA, lastActivity)
                putExtra(LOGIN_METHOD_KEY_EXTRA, loginMethod)
                putExtra(NAME_KEY_EXTRA,nName)
                putExtra(GMAIL_KEY_EXTRA,gMail)
                putExtra(PROFILE_PICTURE_KEY_EXTRA,profilePicture)
                putExtra(CURRENT_USER_UID_KEY_EXTRA, currentUserUid)
            }
        }
    }

    private lateinit var binding: ActivitySignInBinding


    private val signInViewModel: SignInViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        setUIComponents()
        initListeners()
        initObservers()
        getDetails()
        setDetails()
    }

    private fun setUIComponents() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun setDetails() {
        when(loginMethod){
            GOOGLE ->{
                if(nName.isNotEmpty()){
                    binding.txpFullNameASU.setText(nName)
                }
                if(gMail.isNotEmpty()){
                    binding.txpEmailASU.setText(gMail)
                    binding.txpEmailASU.inputType = InputType.TYPE_NULL
                    binding.txpEmailASU.isEnabled = false
                    binding.txpEmailASU.isFocusableInTouchMode = false
                }
                binding.txpPasswordASU.isFocusableInTouchMode = false
                binding.txpPasswordASU.isEnabled = false
                binding.txpPasswordASU.isCursorVisible = false
                binding.txpPasswordASU.keyListener = null
                binding.txpRepeatPasswordASU.isFocusableInTouchMode = false
                binding.txpRepeatPasswordASU.isEnabled = false
                binding.txpRepeatPasswordASU.isCursorVisible = false
                binding.txpRepeatPasswordASU.keyListener = null

                binding.txpPasswordASU.setOnClickListener {
                    toast("No es necesario una contraseña ingresando por Google.")
                }

                binding.txpRepeatPasswordASU.setOnClickListener {
                    toast("No es necesario una contraseña ingresando por Google.")
                }
            }
        }
    }

    private fun getDetails() {
        lastActivity = intent.getStringExtra(LAST_ACTIVITY_KEY_EXTRA).toString()
        loginMethod = intent.getIntExtra(LOGIN_METHOD_KEY_EXTRA,12)
        nName = intent.getStringExtra(NAME_KEY_EXTRA).toString()
        gMail = intent.getStringExtra(GMAIL_KEY_EXTRA).toString()
        photoProfile = intent.getStringExtra(PROFILE_PICTURE_KEY_EXTRA).toString()
        currentUser = intent.getStringExtra(CURRENT_USER_UID_KEY_EXTRA).toString()
        





    }

    private fun initObservers() {
        signInViewModel.navigateToVerifyEmail.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerifyEmail()
            }
        }

        signInViewModel.navigateToLogin.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }

        signInViewModel.navigateToMain.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToMain()
            }
        }

        signInViewModel.currentUser.observe(this){ currentUserUID ->
            this.currentUser = currentUserUID
        }

        lifecycleScope.launchWhenStarted {
            signInViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        signInViewModel.postAccountState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    binding.pgbProgressSignUp.visibility = View.GONE

                }
                is Resource.Error -> {
                    binding.pgbProgressSignUp.visibility = View.GONE
                    showErrorDialog()
                }
            }
        }

        signInViewModel.showErrorDialog.observe(this) { showError ->
            if (showError) showErrorDialog()
        }

        signInViewModel.showErrorInputs.observe(this) { showError ->
            if (showError) showErrorInputs()
        }

        signInViewModel.showErrorEmailExists.observe(this) { showError ->
            if (showError) showErrorEmailExists()
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

            txvHaveAccountAR.setOnClickListener { signInViewModel.onLoginSelected() }

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
                    scrollViewASU.fullScroll(ScrollView.FOCUS_UP)
                }
            })

            btnContinueAR.setOnClickListener {
                checkValue(counter)
                counter++
                scrollViewASU.fullScroll(ScrollView.FOCUS_UP)
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
        if(imvMaster.tag == AppConstants.SELECTED){
            imvMaster.tag = AppConstants.UNSELECTED
            when(master){
                AppConstants.PAINTER ->{
                    imvMaster.setImageResource(R.drawable.ic_painter)
                }
                AppConstants.CARPENTER ->{
                    imvMaster.setImageResource(R.drawable.ic_carpenter)
                }
                AppConstants.AIRSERVICE ->{
                    imvMaster.setImageResource(R.drawable.ic_air_service)
                }
                AppConstants.GARDENER ->{
                    imvMaster.setImageResource(R.drawable.ic_gardener)
                }
                AppConstants.TRUCKMOVING ->{
                    imvMaster.setImageResource(R.drawable.ic_truck_moving)
                }
                AppConstants.TRUCKFREIGHTER ->{
                    imvMaster.setImageResource(R.drawable.ic_truck_freight)
                }
                AppConstants.MASON ->{
                    imvMaster.setImageResource(R.drawable.ic_mason)
                }
                AppConstants.NANNY ->{
                    imvMaster.setImageResource(R.drawable.ic_nanny)
                }
                AppConstants.GAS ->{
                    imvMaster.setImageResource(R.drawable.ic_gas)
                }
                AppConstants.PCTECHNICIAN ->{
                    imvMaster.setImageResource(R.drawable.ic_pc_technician)
                }
                AppConstants.PLUMBER ->{
                    imvMaster.setImageResource(R.drawable.ic_plumber)
                }
                AppConstants.ELECTRICIAN ->{
                    imvMaster.setImageResource(R.drawable.ic_electrician)
                }
            }
            masterList.removeIf { it.id == master }
        }else{
            showMasterDialog(master, imvMaster)
            imvMaster.tag = AppConstants.SELECTED
            when(master){
                AppConstants.PAINTER ->{
                    imvMaster.setImageResource(R.drawable.ic_painter_selected)
                }
                AppConstants.CARPENTER ->{
                    imvMaster.setImageResource(R.drawable.ic_carpenter_selected)
                }
                AppConstants.AIRSERVICE ->{
                    imvMaster.setImageResource(R.drawable.ic_air_service_selected)
                }
                AppConstants.GARDENER ->{
                    imvMaster.setImageResource(R.drawable.ic_gardener_selected)
                }
                AppConstants.TRUCKMOVING ->{
                    imvMaster.setImageResource(R.drawable.ic_truck_moving_selected)
                }
                AppConstants.TRUCKFREIGHTER ->{
                    imvMaster.setImageResource(R.drawable.ic_truck_freight_selected)
                }
                AppConstants.MASON ->{
                    imvMaster.setImageResource(R.drawable.ic_mason_selected)
                }
                AppConstants.NANNY ->{
                    imvMaster.setImageResource(R.drawable.ic_nanny_selected)
                }
                AppConstants.GAS ->{
                    imvMaster.setImageResource(R.drawable.ic_gas_selected)
                }
                AppConstants.PCTECHNICIAN ->{
                    imvMaster.setImageResource(R.drawable.ic_pc_technician_selected)
                }
                AppConstants.PLUMBER ->{
                    imvMaster.setImageResource(R.drawable.ic_plumber_selected)
                }
                AppConstants.ELECTRICIAN ->{
                    imvMaster.setImageResource(R.drawable.ic_electrician_selected)
                }
            }
        }
    }

    private fun checkValue(count: Int){
        when(count){
            AppConstants.PASO1 -> register2()
            AppConstants.PASO2 -> register3()
            AppConstants.PASO3 -> register4()
            AppConstants.PASO4 -> {
                finishButton()
                counter--
            }
        }
    }

    private fun showMasterDialog(master: Int, imvMaster: ImageView) {
        MasterDialog.create(
            master = master,
            masterList = masterList,
            imvMaster = imvMaster,
            signUpActivity = this,
            editProfileActivity = null
        ).show(dialogLauncher, this)
    }

    private fun showErrorDialog(description: Int) {
        ErrorDialog.create(
            title = getString(R.string.signin_error_title),
            description = getString(description),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                signInViewModel.onSignUpSelected(
                    user,
                    binding.txpRepeatPasswordASU.text.toString(),
                    loginMethod
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun showErrorInputs() {
        ErrorDialog.create(
            title = "Ha ocurrido un error",
            description = "Verifica tus datos ingresados",
            positiveAction = ErrorDialog.Action(getString(R.string.accept)) {
                it.dismiss()
            },
            isDialogCancelable = false
        ).show(dialogLauncher, this)
    }

    private fun showErrorEmailExists() {
        ErrorDialog.create(
            title = "Ha ocurrido un error",
            description = "${binding.txpEmailASU.text} ya fue utilizado en otra cuenta de Trovami\nPor favor, intente con otro correo o inicie sesión con este correo",
            positiveAction = ErrorDialog.Action(getString(R.string.accept)) {
                it.dismiss()
            },
            isDialogCancelable = false
        ).show(dialogLauncher, this)
    }


    private fun finishButton() {
        when(loginMethod){
            AppConstants.EMAIL ->{
                signInViewModel.onSignUpSelected(user,binding.txpRepeatPasswordASU.text.toString(),
                    AppConstants.EMAIL
                )
            }
            AppConstants.GOOGLE ->{
                signInViewModel.onSignUpSelected(user, null, AppConstants.GOOGLE)
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

    internal fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            user = User("",
                fullName = binding.txpFullNameASU.text.toString(),
                "",
                document = binding.txpDocumentASU.text.toString(),
                phoneNumber = binding.txpPhoneASU.text.toString(),
                password = binding.txpPasswordASU.text.toString(),
                email = binding.txpEmailASU.text.toString(),
                defaultAdress = binding.txpAddressASU.text.toString(),
                province = binding.txpProvinceASU.text.toString(),
                municipality = binding.txpMunicipalityASU.text.toString(),
                masterList = masterList)
            signInViewModel.onFieldsChanged(userSignIn = user,
                passwordConfirmation = binding.txpRepeatPasswordASU.text.toString())
        }
    }

    private fun goToVerifyEmail(currentUserUid: String) {
        finish()
        //startActivity(VerificationActivity.create(this, currentUserUid))
    }

    private fun goToLogin(lastActivity: String) {
        if(lastActivity=="IntroductionActivity"){
            startActivity(LoginActivity.create(this))
            finish()
        }else{
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
    }

    private fun goToMain(currentUserUid: String) {
        startActivity(MainActivity.create(this, currentUserUid))
    }
}