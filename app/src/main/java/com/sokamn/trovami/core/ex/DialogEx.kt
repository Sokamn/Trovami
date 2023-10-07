package com.sokamn.trovami.core.ex

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sokamn.trovami.core.bottomsheetdialog.BottomSheetDialogFragmentLauncher
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher

fun DialogFragment.show(launcher: DialogFragmentLauncher, activity: FragmentActivity) {
    launcher.show(this, activity)
}
fun BottomSheetDialogFragment.show(launcher: BottomSheetDialogFragmentLauncher, activity: FragmentActivity) {
    launcher.show(this, activity)
}
