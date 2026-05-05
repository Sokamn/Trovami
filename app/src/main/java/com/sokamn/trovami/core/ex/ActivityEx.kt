package com.sokamn.trovami.core.ex

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.sokamn.trovami.R

fun Activity.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

fun spanEmail(
    first: String,
    secondSpanned: String,
    third: String
): SpannableStringBuilder {
    val completedText = SpannableStringBuilder("$first$secondSpanned$third")

    completedText.apply {
        setSpan(
            StyleSpan(Typeface.BOLD),
            first.length,
            (first + secondSpanned).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return completedText
}
