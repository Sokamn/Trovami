package com.sokamn.trovami.core.ex

import android.app.Activity
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.sokamn.trovami.R

fun TextView.spanSecondBold(
    activity: Activity,
    first: String,
    secondSpanned: String,
    third: String
) {
    val completedText = SpannableStringBuilder("$first$secondSpanned$third")
    val bold = ResourcesCompat.getFont(activity, R.font.bold)

    completedText.setSpan(
        CustomTypefaceSpan(bold!!),
        first.length,
        (first + secondSpanned).length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    text = completedText
}

class CustomTypefaceSpan(private val newType: Typeface) : TypefaceSpan("") {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
    }
}