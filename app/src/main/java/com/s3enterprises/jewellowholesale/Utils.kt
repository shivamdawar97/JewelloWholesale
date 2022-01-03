package com.s3enterprises.jewellowholesale

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.InverseMethod
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    private val DATE_FORMAT = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.US)
    private val DATE_FORMAT_FOR_HEADING = SimpleDateFormat("dd MMM yyyy", Locale.US)
    var printerName = ""
    var bhav = 5832
    var INPUT_CONNECTION: InputConnection? =null
    val rupeesFormat = DecimalFormat("##,##,###")

    object CurrentDate{
        private val date = Date()
        val year = DateFormat.format("yyyy", date) as String
        val month = DateFormat.format("MMM", date) as String
        //val monthAsInt = DateFormat.format("MM", date) as String
        val day = DateFormat.format("dd", date) as String
    }

    fun TextView.onTextChanged(listener: (CharSequence) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener.invoke(s!!)
            }
        })
    }

    fun EditText.getTextToInt() = this.text.toString().let {
        if (it.isNotBlank()) it.toInt() else 0
    }

    fun EditText.getTextToLong() = this.text.toString().let {
        if (it.isNotBlank()) it.toLong() else 0
    }

    fun Float.roundOff(place:Int) = "%.${place}f".format(this).toFloat()

    @JvmStatic
    fun getDate(date: Date): String = DATE_FORMAT_FOR_HEADING.format(date)

    @JvmStatic
    fun getDate(date: Long): String = DATE_FORMAT.format(Date(date))

    @JvmStatic
    fun getRupeesFormatted(amount:Int):String {
        val once = amount%10
        val oAmount = if(once>7) amount+10-once else amount-once
        return rupeesFormat.format(oAmount)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun stringToFloat(value: String) = try {
        if (value.isNullOrBlank()) 0f else value.toFloat()
    } catch (e:NumberFormatException) {
        0f
    }
    fun stringToInt(value: String?) = if (value.isNullOrBlank()) 0 else value.toInt()

    fun updatePrinterName(name: String, sharedPreferences: SharedPreferences) {
        printerName = name
        sharedPreferences.edit().putString("printer_name", name).apply()
    }

    fun atEndOfDay(date: Date) = with(Calendar.getInstance()) {
        time = date
        set(Calendar.HOUR_OF_DAY, 23);set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59);set(Calendar.MILLISECOND, 999)
        time
    }

    fun atStartOfDay(date: Date) = with(Calendar.getInstance()) {
        time = date
        set(Calendar.HOUR_OF_DAY, 0);set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0);set(Calendar.MILLISECOND, 0)
        time
    }

    class GeneralViewHolder<T>(
        private val mView: View,
        private val onBind: (T, View,Int) -> Unit
    ) : RecyclerView.ViewHolder(mView) {
        fun populate(item: T,position:Int) = onBind(item, mView,position)
    }

    fun <T> generatedAdapter(list: List<T>, resource: Int, onBind: (T,View,Int) -> Unit) =
        object : RecyclerView.Adapter<GeneralViewHolder<T>>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GeneralViewHolder(
                    LayoutInflater.from(parent.context).inflate(resource, parent, false), onBind
                )

            override fun onBindViewHolder(holder: GeneralViewHolder<T>, position: Int) =
                holder.populate(list[position],position)

            override fun getItemCount() = list.size
        }


}