package com.sokamn.trovami.domain.model

import com.sokamn.trovami.R

sealed class MasterInfo(val id: Int, val name: Int, val imageProfile: Int, val imageOutlined: Int, var isSelected: Boolean){
    data object Mason: MasterInfo(0,R.string.mason,R.drawable.profile_mason, R.drawable.ic_mason, false)
    data object Carpenter: MasterInfo(1,R.string.carpenter, R.drawable.profile_carpenter, R.drawable.ic_carpenter, false)
    data object Electrician: MasterInfo(2,R.string.electrician, R.drawable.profile_electrician, R.drawable.ic_electrician, false)
    data object TruckFreighter: MasterInfo(3,R.string.truck_freight, R.drawable.profile_freighter, R.drawable.ic_truck_freight, false)
    data object Gas: MasterInfo(4,R.string.gas, R.drawable.profile_gas, R.drawable.ic_gas,false)
    data object Gardener: MasterInfo(5,R.string.gardener, R.drawable.profile_gardener, R.drawable.ic_gardener,false)
    data object TruckMoving: MasterInfo(6,R.string.truck_moving, R.drawable.profile_moving, R.drawable.ic_truck_moving,false)
    data object Nanny: MasterInfo(7,R.string.nanny, R.drawable.profile_nanny, R.drawable.ic_nanny,false)
    data object Painter: MasterInfo(8,R.string.painter, R.drawable.profile_painter, R.drawable.ic_painter,false)
    data object Plumber: MasterInfo(9,R.string.plumber, R.drawable.profile_plumbing, R.drawable.ic_plumber,false)
    data object AirService: MasterInfo(10,R.string.air_service, R.drawable.profile_air_service, R.drawable.ic_air_service,false)
    data object PCTechnician: MasterInfo(11,R.string.pc_technician, R.drawable.profile_pc_technician, R.drawable.ic_pc_technician,false)
}