package com.example.scoreup.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox

class SUCheckBoxRegular(context: Context, attributeSet: AttributeSet): AppCompatCheckBox(context, attributeSet) {
    init{
        applyFont()
    }

    private fun applyFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Raleway-Regular.ttf")
        setTypeface(typeface)
    }
}