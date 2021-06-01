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
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    val FIRESTORE by lazy { Firebase.firestore
        .collection("business_username")
    }
    val KEY_VALUES by lazy {
        Firebase.firestore
            .collection("business_username").document("key_values")
    }
    private val DATE_FORMAT = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.US)
    private val DATE_FORMAT_FOR_HEADING = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private var printerName = ""
    var bhav = 5832
    var INPUT_CONNECTION: InputConnection? =null

    object CurrentDate{
        private val date = Date()
        val year = DateFormat.format("yyyy", date) as String
        val month = DateFormat.format("MMM", date) as String
        val monthAsInt = DateFormat.format("MM", date) as Int
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

    fun Float.toMannerString() = if(this == 0f) "" else this.toString()
    fun Int.toMannerString() = if(this == 0) "" else this.toString()

    fun Float.roundOff(place:Int) = "%.${place}f".format(this).toFloat()

    fun <T> AppCompatActivity.startActivity(cls: Class<T>) =
        this.startActivity(Intent(this, cls))

    fun Date.getFormattedDate() = DATE_FORMAT.format(this)

    @JvmStatic
    fun getDate(date: Date): String = DATE_FORMAT_FOR_HEADING.format(date)

    @JvmStatic
    fun getDateStringFromLong(date: Long): String = Date(date).getFormattedDate()

    fun animationListener(listener: () -> Unit): Animation.AnimationListener {
        return object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                listener.invoke()
            }
        }
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun stringToFloat(value: String) = if (value.isNullOrBlank()) 0f else value.toFloat()
    fun stringToInt(value: String?) = if (value.isNullOrBlank()) 0 else value.toInt()

    @InverseMethod("stringToLong")
    @JvmStatic
    fun longToString(value: Long) =
        value.toString()

    @JvmStatic
    fun stringToLong(value: String) =
        if (value.isNotBlank()) value.toString().toLong() else 0L

    fun updatePrinterName(name: String, sharedPreferences: SharedPreferences) {
        printerName = name
        sharedPreferences.edit().putString("printer_name", name).apply()
    }

    class GeneralViewHolder<T>(
        private val mView: View,
        private val onBind: (T, View) -> Unit
    ) : RecyclerView.ViewHolder(mView) {
        fun populate(item: T) = onBind(item, mView)
    }

    fun <T> generatedAdapter(list: List<T>, resource: Int, onBind: (T, View) -> Unit) =
        object : RecyclerView.Adapter<GeneralViewHolder<T>>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GeneralViewHolder(
                    LayoutInflater.from(parent.context).inflate(resource, parent, false), onBind
                )

            override fun onBindViewHolder(holder: GeneralViewHolder<T>, position: Int) =
                holder.populate(list[position])

            override fun getItemCount() = list.size
        }


}