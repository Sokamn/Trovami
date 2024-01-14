package com.sokamn.trovami.data.providers

import com.sokamn.trovami.domain.model.MasterInfo
import com.sokamn.trovami.domain.model.MasterInfo.*
import javax.inject.Inject

class MasterProvider @Inject constructor(){
    fun getMasters() : List<MasterInfo> {
        return listOf(
            Mason,
            Carpenter,
            Electrician,
            TruckFreighter,
            Gas,
            Gardener,
            TruckMoving,
            Nanny,
            Painter,
            Plumber,
            AirService,
            PCTechnician
        )
    }
}