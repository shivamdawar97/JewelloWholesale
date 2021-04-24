package com.s3enterprises.jewellowholesale.party.addParty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ActivityAddPartyBinding
import com.s3enterprises.jewellowholesale.party.PartyRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddParty : AppCompatActivity() {

    private lateinit var binding: ActivityAddPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_party)
        binding.isLoading = false

        setUpClickListener()
    }

    private fun setUpClickListener() = with(binding){
        addParty.setOnClickListener {
            if(partyName.isNullOrBlank() || partyNumber.isNullOrBlank() || partyAddress.isNullOrBlank())
                Toast.makeText(this@AddParty,R.string.please_fill,Toast.LENGTH_LONG).show()
            else lifecycleScope.launch{
                val party = Party(name=partyName!!,phoneNumber=partyNumber!!,from=partyAddress!!)
                isLoading = true
                PartyRepository.insert(party)
                isLoading = false
                Toast.makeText(this@AddParty,"Party added ${party.name}",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}