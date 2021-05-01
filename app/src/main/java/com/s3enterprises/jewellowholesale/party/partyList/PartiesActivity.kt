package com.s3enterprises.jewellowholesale.party.partyList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.databinding.ActivityPartiesBinding
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.party.addParty.AddParty
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import kotlinx.coroutines.launch

class PartiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parties)

        setUpRecyclerView()

    }

    private fun setUpRecyclerView() = lifecycleScope.launch {
        binding.isLoading = true
        val parties = PartyRepository.getParties()
        binding.isLoading = false
        binding.partyRecycler.adapter = PartiesAdapter(parties)
        binding.partyRecycler.layoutManager = LinearLayoutManager(this@PartiesActivity)
        binding.searchField.onTextChanged {
            (binding.partyRecycler.adapter as PartiesAdapter).filter.filter(it)
        }
    }

    fun addParty(v: View){
        startActivity(Intent(this,AddParty::class.java))
    }
}