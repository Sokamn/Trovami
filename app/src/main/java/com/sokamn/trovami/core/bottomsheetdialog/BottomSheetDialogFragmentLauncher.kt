package com.sokamn.trovami.core.bottomsheetdialog

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sokamn.trovami.core.delegate.weak
import javax.inject.Inject

class BottomSheetDialogFragmentLauncher @Inject constructor() : LifecycleObserver {
    private var activity: FragmentActivity? by weak()
    private var dialogFragment: BottomSheetDialogFragment? by weak()

    fun show(dialogFragment: BottomSheetDialogFragment, activity: FragmentActivity) {
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            dialogFragment.show(activity.supportFragmentManager, null)
        } else {
            this.activity = activity
            this.dialogFragment = dialogFragment
            activity.lifecycle.addObserver(this@BottomSheetDialogFragmentLauncher)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onActivityResumed() {
        val activity = activity ?: return
        val dialogFragment = dialogFragment ?: return

        dialogFragment.show(activity.supportFragmentManager, null)
        activity.lifecycle.removeObserver(this)
    }
}