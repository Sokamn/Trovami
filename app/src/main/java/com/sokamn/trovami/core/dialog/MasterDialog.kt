package com.sokamn.trovami.core.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.sokamn.trovami.R
import com.sokamn.trovami.databinding.DialogMasterBinding
import com.sokamn.trovami.domain.model.MasterModel
import com.sokamn.trovami.ui.auth.signin.SignInActivity
import com.sokamn.trovami.utils.MasterConstants.AIRSERVICE
import com.sokamn.trovami.utils.MasterConstants.CARPENTER
import com.sokamn.trovami.utils.MasterConstants.ELECTRICIAN
import com.sokamn.trovami.utils.MasterConstants.GARDENER
import com.sokamn.trovami.utils.MasterConstants.GAS
import com.sokamn.trovami.utils.MasterConstants.MASON
import com.sokamn.trovami.utils.MasterConstants.NANNY
import com.sokamn.trovami.utils.MasterConstants.PAINTER
import com.sokamn.trovami.utils.MasterConstants.PCTECHNICIAN
import com.sokamn.trovami.utils.MasterConstants.PLUMBER
import com.sokamn.trovami.utils.MasterConstants.TRUCKFREIGHTER
import com.sokamn.trovami.utils.MasterConstants.TRUCKMOVING

class MasterDialog : DialogFragment() {

    private var masterSelected = 0
    private var masterList = mutableListOf<MasterModel>()
    private var isSuccessful = false
    private lateinit var imvMaster: ImageView
    private var signUpActivity: SignInActivity? = null
    //private var editProfileActivity: EditProfileActivity? = null

    companion object {
        fun create(
            master: Int,
            masterList: MutableList<MasterModel>,
            imvMaster: ImageView,
            signUpActivity: SignInActivity?,
            //editProfileActivity: EditProfileActivity?
        ): MasterDialog = MasterDialog().apply {
            this.masterSelected = master
            this.masterList = masterList
            this.imvMaster = imvMaster
            this.signUpActivity = signUpActivity
            //this.editProfileActivity = editProfileActivity
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isSuccessful){
            signUpActivity?.onJobClickFunction(masterSelected, imvMaster)
            //editProfileActivity?.onJobClickFunction(masterSelected, imvMaster)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogMasterBinding.inflate(requireActivity().layoutInflater)
        with(binding){
            txpDescriptionPCM.setText("")
            btnConfirmPCM.setOnClickListener{
                if(txpDescriptionPCM.text.isNotEmpty()){
                    val master = MasterModel(masterSelected,txpDescriptionPCM.text.toString())
                    masterList.add(master)
                    isSuccessful = true
                    signUpActivity?.onFieldChanged()
                    //editProfileActivity?.onFieldChanged()
                    dismiss()
                }else{
                    Toast.makeText(context,"Escriba una breve descripción sobre su experiencia en el rubro",
                        Toast.LENGTH_LONG).show()
                }
            }
            btnCancelPCM.setOnClickListener {
                dismiss()
            }
            when(masterSelected){
                PAINTER ->{
                    txvTitlePCM.text = getString(R.string.painter)
                }
                CARPENTER ->{
                    txvTitlePCM.text = getString(R.string.carpenter)
                }
                AIRSERVICE ->{
                    txvTitlePCM.text = getString(R.string.air_service)
                }
                GARDENER ->{
                    txvTitlePCM.text = getString(R.string.gardener)
                }
                TRUCKMOVING ->{
                    txvTitlePCM.text = getString(R.string.truck_moving)
                }
                TRUCKFREIGHTER ->{
                    txvTitlePCM.text = getString(R.string.truck_freight)
                }
                MASON ->{
                    txvTitlePCM.text = getString(R.string.mason)
                }
                NANNY ->{
                    txvTitlePCM.text = getString(R.string.nanny)
                }
                GAS ->{
                    txvTitlePCM.text = getString(R.string.gas)
                }
                PCTECHNICIAN ->{
                    txvTitlePCM.text = getString(R.string.pc_technician)
                }
                PLUMBER ->{
                    txvTitlePCM.text = getString(R.string.plumber)
                }
                ELECTRICIAN ->{
                    txvTitlePCM.text = getString(R.string.electrician)
                }
            }
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(true)
            .create()
    }
}