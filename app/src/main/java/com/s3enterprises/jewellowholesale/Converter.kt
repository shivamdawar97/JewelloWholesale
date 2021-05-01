package com.s3enterprises.jewellowholesale

import android.widget.EditText
import androidx.databinding.InverseMethod

object Converter {
    @InverseMethod("stringToFloat")
    @JvmStatic fun floatToString(value:Float):String{
        return value.toString()
    }
    @JvmStatic fun stringToFloat(value:String):Float{
        return value.toFloat()
    }
}