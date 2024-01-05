package com.sokamn.trovami.domain.model

import com.sokamn.trovami.R

sealed class MasterInfo(val name: Int, val imageProfile: Int, val imageOutlined: Int, val isSelected: Boolean){
    data object Mason: MasterInfo(R.string.mason,R.drawable.profile_mason, R.drawable.ic_mason, false)
    data object Carpenter: MasterInfo(R.string.carpenter, R.drawable.profile_carpenter, R.drawable.ic_carpenter, false)
    data object Electrician: MasterInfo(R.string.electrician, R.drawable.profile_electrician, R.drawable.ic_electrician, false)
    data object TruckFreighter: MasterInfo(R.string.truck_freight, R.drawable.profile_freighter, R.drawable.ic_truck_freight, false)
    data object Gas: MasterInfo(R.string.gas, R.drawable.profile_gas, R.drawable.ic_gas,false)
    data object Gardener: MasterInfo(R.string.gardener, R.drawable.profile_gardener, R.drawable.ic_gardener,false)
    data object TruckMoving: MasterInfo(R.string.truck_moving, R.drawable.profile_moving, R.drawable.ic_truck_moving,false)
    data object Nanny: MasterInfo(R.string.nanny, R.drawable.profile_nanny, R.drawable.ic_nanny,false)
    data object Painter: MasterInfo(R.string.painter, R.drawable.profile_painter, R.drawable.ic_painter,false)
    data object Plumber: MasterInfo(R.string.plumber, R.drawable.profile_plumbing, R.drawable.ic_plumber,false)
    data object AirService: MasterInfo(R.string.air_service, R.drawable.profile_air_service, R.drawable.ic_air_service,false)
    data object PCTechnician: MasterInfo(R.string.pc_technician, R.drawable.profile_pc_technician, R.drawable.ic_pc_technician,false)
}