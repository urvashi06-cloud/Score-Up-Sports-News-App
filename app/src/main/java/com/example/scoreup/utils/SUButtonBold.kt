package com.example.scoreup.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class SUButtonBold(context: Context, attributeSet: AttributeSet): AppCompatButton(context, attributeSet) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Raleway-Bold.ttf")
        setTypeface(typeface)
    }
}