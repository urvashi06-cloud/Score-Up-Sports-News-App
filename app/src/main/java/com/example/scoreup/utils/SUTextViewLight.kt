package com.example.scoreup.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class SUTextViewLight(context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Raleway-Light.ttf")
        setTypeface(typeface)
    }
}