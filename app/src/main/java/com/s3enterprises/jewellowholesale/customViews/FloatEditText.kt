package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.s3enterprises.jewellowholesale.Utils


class FloatEditText:AppCompatEditText {

    val floatValue:Float
    get() = getFloat()
    private val ic by lazy { onCreateInputConnection(EditorInfo()) }
    constructor(context: Context) : super(context) {
        inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL + InputType.TYPE_NUMBER_FLAG_SIGNED
        initiateSetup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initiateSetup() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initiateSetup()
    }

    private fun initiateSetup() {
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) return
        showSoftInputOnFocus = false
        setOnFocusChangeListener { _, b ->
           if(b) Handler(Looper.getMainLooper()).postDelayed({
               Utils.INPUT_CONNECTION = ic
            },100)
            else Utils.INPUT_CONNECTION = null
        }
    }

    private fun getFloat():Float{
        val value = text.toString()
        return if (value.isBlank() || value == "-" || value == "." || value == "-.") 0f else value.toFloat()
    }


}