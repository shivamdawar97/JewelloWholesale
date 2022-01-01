package com.s3enterprises.jewellowholesale.party.addParty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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

    @Inject
    lateinit var partiesRepository: PartyRepository
    private lateinit var binding: ActivityAddPartyBinding
    private lateinit var party: Party

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_party)

        initiateSetup()
        setUpClickListeners()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = if(!binding.isUpdate!!) "Add Party" else "Update Party"
    }

    private fun initiateSetup() = with(binding) {
        isLoading = false ; isUpdate = false ;
        intent.extras?.let {
            val p = it.get("party") as? Party
            if(p!=null){
                party = p ;isUpdate = true
                partyName = party.name
                partyNumber = party.phoneNumber
                partyAddress = party.from
            }
        }
    }

    private fun setUpClickListeners() = with(binding){
        addParty.setOnClickListener {

            if(partyName.isNullOrBlank() || partyNumber.isNullOrBlank())
                Toast.makeText(this@AddParty,R.string.please_fill,Toast.LENGTH_LONG).show()

            else if(!isUpdate!!) lifecycleScope.launch{
                val newParty = Party(name=partyName!!,phoneNumber=partyNumber!!,from=partyAddress?:"")
                try{
                    isLoading = true
                    partiesRepository.insert(newParty)
                    isLoading = false
                    Toast.makeText(this@AddParty,"Party added ${newParty.name}",Toast.LENGTH_LONG).show()
                    finish()
                }catch (e:Exception){
                    Toast.makeText(this@AddParty,e.message,Toast.LENGTH_LONG).show()
                    isLoading = false
                }
            }

            else lifecycleScope.launch {
                party.name = partyName!! ; party.phoneNumber = partyNumber!! ; party.from = partyAddress?:""
                isLoading = true
                partiesRepository.update(party)
                isLoading = true
                Toast.makeText(this@AddParty,"Party updated ${party.name}",Toast.LENGTH_LONG).show()
                finish()
            }
        }

        deleteParty.setOnClickListener { lifecycleScope.launch {
            isLoading = true
            partiesRepository.delete(party)
            isLoading = false
            Toast.makeText(this@AddParty,"Party Deleted ${party.name}",Toast.LENGTH_LONG).show()
            finish()
        }}
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
        {   finish()
            return true
        }
        return false
    }

}