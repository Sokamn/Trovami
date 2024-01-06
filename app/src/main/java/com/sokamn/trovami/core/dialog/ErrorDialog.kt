package com.sokamn.trovami.core.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.sokamn.trovami.databinding.DialogErrorBinding

class ErrorDialog : DialogFragment() {

    private var title: String = ""
    private var description: String = ""
    private var isDialogCancelable: Boolean = true
    private var positiveAction: Action = Action.Empty
    private var negativeAction: Action = Action.Empty

    companion object {
        fun create(
            title: String = "",
            description: String = "",
            isDialogCancelable: Boolean = true,
            positiveAction: Action = Action.Empty,
            negativeAction: Action = Action.Empty,
        ): ErrorDialog = ErrorDialog().apply {
            this.title = title
            this.description = description
            this.isDialogCancelable = isDialogCancelable
            this.positiveAction = positiveAction
            this.negativeAction = negativeAction
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogErrorBinding.inflate(requireActivity().layoutInflater)

        binding.tvTitleDE.text = title
        binding.tvDescriptionDE.text = description
        if (negativeAction == Action.Empty) {
            binding.btnNegativeDE.isGone = true
        } else {
            binding.btnNegativeDE.text = negativeAction.text
            binding.btnNegativeDE.setOnClickListener { negativeAction.onClickListener(this) }
        }
        binding.btnPositiveDE.text = positiveAction.text
        binding.btnPositiveDE.setOnClickListener { positiveAction.onClickListener(this) }
        isCancelable = isDialogCancelable

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(isDialogCancelable)
            .create()
    }

    data class Action(
        val text: String,
        val onClickListener: (ErrorDialog) -> Unit
    ) {
        companion object {
            val Empty = Action("") {}
        }
    }
}

