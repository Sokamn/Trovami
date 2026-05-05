package com.sokamn.trovami.core.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.sokamn.trovami.R
import com.sokamn.trovami.databinding.DialogMasterBinding
import com.sokamn.trovami.domain.model.Master
import com.sokamn.trovami.presentation.signup.SignUpActivity
import com.sokamn.trovami.util.AppConstants

class MasterDialog : DialogFragment() {

    private var masterSelected = 0
    private var masterList = mutableListOf<Master>()
    private var isSuccessful = false
    private lateinit var imvMaster: ImageView
    private lateinit var signUpActivity: SignUpActivity

    companion object {
        fun create(
            master: Int,
            masterList: MutableList<Master>,
            imvMaster: ImageView,
            activity: SignUpActivity
        ): MasterDialog = MasterDialog().apply {
            this.masterSelected = master
            this.masterList = masterList
            this.imvMaster = imvMaster
            this.signUpActivity = activity
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
        if (!isSuccessful)
            signUpActivity.onJobClickFunction(masterSelected, imvMaster)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogMasterBinding.inflate(requireActivity().layoutInflater)
        with(binding){
            txpDescriptionPCM.setText("")
            btnConfirmPCM.setOnClickListener{
                if(txpDescriptionPCM.text.isNotEmpty()){
                    val master = Master.MasterProvider.masterList.first{ master ->  master.id == masterSelected }
                    master.description = txpDescriptionPCM.text.toString()
                    masterList.add(master)
                    isSuccessful = true
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
                AppConstants.PAINTER ->{
                    txvTitlePCM.text = getString(R.string.painter)
                }
                AppConstants.CARPENTER ->{
                    txvTitlePCM.text = getString(R.string.carpenter)
                }
                AppConstants.AIRSERVICE ->{
                    txvTitlePCM.text = getString(R.string.airService)
                }
                AppConstants.GARDENER ->{
                    txvTitlePCM.text = getString(R.string.gardener)
                }
                AppConstants.TRUCKMOVING ->{
                    txvTitlePCM.text = getString(R.string.truckMoving)
                }
                AppConstants.TRUCKFREIGHTER ->{
                    txvTitlePCM.text = getString(R.string.truckFreight)
                }
                AppConstants.MASON ->{
                    txvTitlePCM.text = getString(R.string.mason)
                }
                AppConstants.NANNY ->{
                    txvTitlePCM.text = getString(R.string.nanny)
                }
                AppConstants.GAS ->{
                    txvTitlePCM.text = getString(R.string.gas)
                }
                AppConstants.PCTECHNICIAN ->{
                    txvTitlePCM.text = getString(R.string.pcTechnician)
                }
                AppConstants.PLUMBER ->{
                    txvTitlePCM.text = getString(R.string.plumber)
                }
                AppConstants.ELECTRICIAN ->{
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