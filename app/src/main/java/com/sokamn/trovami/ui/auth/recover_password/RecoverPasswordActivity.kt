package com.sokamn.trovami.ui.auth.recover_password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sokamn.trovami.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
    }
}