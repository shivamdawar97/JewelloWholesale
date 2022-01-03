package com.s3enterprises.jewellowholesale.party

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.database.dao.PartyDao
import javax.inject.Inject

class PartyRepository @Inject constructor(private val partyDao: PartyDao) {

    private val _parties = MutableLiveData<List<Party>>()

    var parties:LiveData<List<Party>> = partyDao.getAll()

    suspend fun insert(party:Party)  {
        partyDao.insert(party)
    }

    suspend fun update(party: Party) {
        partyDao.update(party)
    }

    suspend fun delete(party: Party) {
        partyDao.delete(party)
    }

}