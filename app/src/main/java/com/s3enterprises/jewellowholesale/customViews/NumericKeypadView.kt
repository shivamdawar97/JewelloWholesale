package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.INPUT_CONNECTION

class NumericKeypadView : ConstraintLayout, View.OnClickListener {

    private val buttons = Array<Button?>(14){null}
    private val rsIds = arrayOf(R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
                R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,R.id.button_decimal,
        R.id.button_cancel,R.id.button_negative,R.id.button_delete
    )

    private val keyValue = SparseArray<String>()

    constructor(context: Context) : super(context) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflateLayout(context)
    }

   /* constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,defStyleRes:Int) : super(context, attrs, defStyleAttr,defStyleRes) {
        inflateLayout(context)
    }*/

    private fun inflateLayout(context: Context) {
        inflate(context, R.layout.numeric_keypad_view,this)
        rsIds.forEachIndexed { index, id ->
            val button = findViewById<Button>(id)
            button.setOnClickListener(this)
            buttons[index] = button
        }

        with(keyValue){
            put(rsIds[0],"0");put(rsIds[1],"1");put(rsIds[2],"2")
            put(rsIds[3],"3");put(rsIds[4],"4");put(rsIds[5],"5")
            put(rsIds[6],"6");put(rsIds[7],"7");put(rsIds[8],"8")
            put(rsIds[9],"9");put(rsIds[10],".");put(rsIds[12],"-")
        }

    }

    override fun onClick(view: View) {
        INPUT_CONNECTION?.also { ic ->

            when(view.id){
                rsIds[10] -> { //Decimal
                    val currentText = ic.getExtractedText(ExtractedTextRequest(),0).text
                    if(!currentText.contains("."))
                        ic.commitText(keyValue.get(view.id),1)
                }
                rsIds[11] -> { //cancel
                    val selectedText = ic.getSelectedText(0)

                    if(TextUtils.isEmpty(selectedText))
                        ic.deleteSurroundingText(1,0)
                    else
                        ic.commitText("",1)
                }
                rsIds[12] -> { //negative
                    val length = ic.getExtractedText(ExtractedTextRequest(),0).text.length
                    if(length == 0) ic.commitText(keyValue.get(view.id),1)
                }
                rsIds[13] -> { //delete
                    val currentText = ic.getExtractedText(ExtractedTextRequest(),0).text
                    val beforeCursorText = ic.getTextBeforeCursor(currentText.length,0)
                    val afterCursorText = ic.getTextAfterCursor(currentText.length,0)
                    ic.deleteSurroundingText(beforeCursorText.length,afterCursorText.length)
                }

                else -> ic.commitText(keyValue.get(view.id),1)

            }
        }

    }

}