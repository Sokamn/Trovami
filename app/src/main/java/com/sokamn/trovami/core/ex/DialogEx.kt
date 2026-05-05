package com.sokamn.trovami.core.ex

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.sokamn.trovami.core.dialog.DialogFragmentLauncher

fun DialogFragment.show(launcher: DialogFragmentLauncher, activity: FragmentActivity) {
    launcher.show(this, activity)
}
