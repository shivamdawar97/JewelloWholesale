package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.s3enterprises.jewellowholesale.Utils.setOwnFocusListener

class FloatEditText:AppCompatEditText {

    val floatValue:Float
    get() = getFloat()

    constructor(context: Context) : super(context) {
        inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL + InputType.TYPE_NUMBER_FLAG_SIGNED
        initiateSetup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initiateSetup() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initiateSetup()
    }

    private fun initiateSetup() {
       setOwnFocusListener()
    }

    private fun getFloat():Float{
        val value = text.toString()
        return if (value.isBlank() || value == "-" || value == ".") 0f else value.toFloat()
    }


}