package com.s3enterprises.jewellowholesale.party.partyList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.databinding.ActivityPartiesBinding
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.party.addParty.AddParty
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PartiesActivity : AppCompatActivity() {

    @Inject
    lateinit var partiesRepository: PartyRepository
    private lateinit var binding: ActivityPartiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parties)
        title = "Parties"
        binding.isLoading = true
        setUpRecyclerView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpRecyclerView()  = partiesRepository.parties.observeForever { parties ->
            binding.isLoading = false
            binding.partyRecycler.adapter = PartiesAdapter(parties!!)
            binding.partyRecycler.layoutManager = LinearLayoutManager(this@PartiesActivity)
            binding.searchField.onTextChanged {
                (binding.partyRecycler.adapter as PartiesAdapter).filter.filter(it)
            }
    }


    fun addParty(v: View){
        startActivity(Intent(this,AddParty::class.java))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
        {   finish()
            return true
        }
        return false
    }
}