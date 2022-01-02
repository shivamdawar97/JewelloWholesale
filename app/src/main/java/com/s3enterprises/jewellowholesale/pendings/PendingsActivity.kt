package com.s3enterprises.jewellowholesale.pendings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.generatedAdapter
import com.s3enterprises.jewellowholesale.database.Converters
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent
import org.w3c.dom.Text

class PendingsActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendings)
        title = "Pending Bills"
        preferences = getSharedPreferences("jewello_wholesale", Context.MODE_PRIVATE)
        val converter = Converters()
        val listInString = preferences.getString("pending","")
        val list = if(listInString.isNullOrBlank()) ArrayList() else converter.fromStringToBills(listInString)
        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter =  generatedAdapter(list as List<Bill>,R.layout.card_bill_record){
            p,v,i->
            v.findViewById<TextView>(R.id.bill_no).text = (i+1).toString()
            v.findViewById<TextView>(R.id.party_name).text = p.partyName
            v.findViewById<TextView>(R.id.total_amount).text = p.cashDu.toString()
            v.findViewById<TextView>(R.id.date_view).text = Utils.getDate(p.date)
            v.setOnClickListener {
                list.remove(p)
                val stringOfList = converter.fromListToString(list)
                preferences.edit().putString("pending",stringOfList).apply()
                RxBus.publish(RxEvent.PreviousBillSelected(p))
                finish()
            }
        }
    }
}