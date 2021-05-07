package com.s3enterprises.jewellowholesale.customViews

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import android.R
import android.graphics.Canvas

import android.graphics.Paint
import android.view.ActionMode
import java.lang.RuntimeException


class OTPView : AppCompatEditText {

    private var mSpace = 24f
    private var mNumChars = 4
    private var mLineSpacing = 8f
    private val mMaxLength = 6
    private var mLineStroke = 2f
    private lateinit var mLinesPaint: Paint
    private var mClickListener : OnClickListener? = null


    constructor(context: Context) : super(context)
    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet) {
        init(context,attributeSet)
    }
    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr:Int) : super(context,attributeSet,defStyleAttr){
        init(context,attributeSet)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val multi = context.resources.displayMetrics.density
        mLineStroke *= multi
        mLinesPaint = Paint(paint)
        mLinesPaint.strokeWidth = mLineStroke
        //mLinesPaint.color = color.
        setBackgroundResource(0)
        mSpace *= multi //convert to pixels for our density
        mLineSpacing *= multi //convert to pixels for our density
        mNumChars = mMaxLength
        super.setOnClickListener { v -> // When tapped, move cursor to end of text.
            setSelection(text!!.length)
            if (mClickListener != null) mClickListener?.onClick(v)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas?) {
        val availableWidth = width - paddingRight - paddingLeft
        val mCharSize = (if(mSpace<0) availableWidth/(mNumChars * 2 -1)
        else (availableWidth - (mSpace * (mNumChars-1)))/mNumChars).toFloat()
        var startX = paddingLeft.toFloat()
        val bottom = (height - paddingBottom).toFloat()
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(text,0,textLength,textWidths)
        for(i in 0 until mNumChars){
            canvas?.drawLine(startX,bottom,startX+mCharSize,bottom,mLinesPaint)
            if(text!!.length > i){
                val middle = startX + (mCharSize/2)
                canvas?.drawText(text.toString(), i, (i+1),
                    middle-textWidths[0]/2,
                    bottom-mLineSpacing,
                    paint)
            }
            startX += if(mSpace<0) mCharSize * 2 else mCharSize + mSpace
        }
    }

}