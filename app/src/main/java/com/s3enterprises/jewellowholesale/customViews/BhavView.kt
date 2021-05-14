package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.bhav
import com.s3enterprises.jewellowholesale.Utils.getTextToInt
import com.s3enterprises.jewellowholesale.Utils.setOwnFocusListener
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BhavView : LinearLayout {

    private val bhavField by lazy { findViewById<EditText>(R.id.bhav_field) }
    private val bhavEdit by lazy { findViewById<AppCompatImageView>(R.id.bhav_edit) }
    private val bhavSave by lazy { findViewById<AppCompatImageView>(R.id.bhav_save) }

    constructor(context: Context) : super(context) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflateLayout(context)
    }

    private fun inflateLayout(context: Context) {
        inflate(context, R.layout.view_bhav,this)
        bhavField.setText(bhav.toString())

        bhavEdit.setOnClickListener {
            it.visibility = View.GONE
            bhavSave.visibility = View.VISIBLE
            bhavField.isEnabled = true
        }
        bhavSave.setOnClickListener {
            it.visibility = View.GONE
            bhavEdit.visibility = View.VISIBLE
            bhavField.isEnabled = false
            bhav = bhavField.getTextToInt()
            RxBus.publish(RxEvent.BhavUpdated())
        }

    }

}