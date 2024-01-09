package com.sokamn.trovami.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.sokamn.trovami.R
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.data.source.datastore.DataStoreConstants.USER_KEY_PREFS

class MainActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context, currentUserUid: String): Intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(USER_KEY_PREFS, currentUserUid)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val test = findViewById<Button>(R.id.testbutton)
        test.setOnClickListener {
            toast("uid: " + intent.getStringExtra(USER_KEY_PREFS).toString())
        }
    }
}