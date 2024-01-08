package com.sokamn.trovami.ui.auth.verification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sokamn.trovami.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
    }
}