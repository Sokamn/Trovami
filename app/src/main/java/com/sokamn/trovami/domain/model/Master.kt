package com.sokamn.trovami.domain.model

import com.sokamn.trovami.R
import com.sokamn.trovami.util.AppConstants.Companion.AIRSERVICE
import com.sokamn.trovami.util.AppConstants.Companion.CARPENTER
import com.sokamn.trovami.util.AppConstants.Companion.ELECTRICIAN
import com.sokamn.trovami.util.AppConstants.Companion.GARDENER
import com.sokamn.trovami.util.AppConstants.Companion.GAS
import com.sokamn.trovami.util.AppConstants.Companion.MASON
import com.sokamn.trovami.util.AppConstants.Companion.NANNY
import com.sokamn.trovami.util.AppConstants.Companion.PAINTER
import com.sokamn.trovami.util.AppConstants.Companion.PCTECHNICIAN
import com.sokamn.trovami.util.AppConstants.Companion.PLUMBER
import com.sokamn.trovami.util.AppConstants.Companion.TRUCKFREIGHTER
import com.sokamn.trovami.util.AppConstants.Companion.TRUCKMOVING

data class Master (var id: Int = 0, var name: String = "", var imageProfile: Int = 0, var imageOutlined: Int = 0, var description: String = "", var isSelected: Boolean = false) {

    class MasterProvider(){
        companion object{
            val masterList = MasterList()

            private fun MasterList(): MutableList<Master> {
                return mutableListOf(
                    Master(MASON, "Albañilería", R.drawable.profile_mason, R.drawable.ic_mason,""),
                    Master(CARPENTER,"Carpintería", R.drawable.profile_carpenter, R.drawable.ic_carpenter,""),
                    Master(ELECTRICIAN, "Electricista", R.drawable.profile_electricist, R.drawable.ic_electrician,""),
                    Master(TRUCKFREIGHTER, "Flete", R.drawable.profile_freighter, R.drawable.ic_truck_freight,""),
                    Master(GAS, "Gasista", R.drawable.profile_gas, R.drawable.ic_gas,""),
                    Master(GARDENER, "Jardinería", R.drawable.profile_gardener, R.drawable.ic_gardener,""),
                    Master(TRUCKMOVING, "Mudanza", R.drawable.profile_moving, R.drawable.ic_truck_moving,""),
                    Master(NANNY, "Niñera", R.drawable.profile_nanny, R.drawable.ic_nanny,""),
                    Master(PAINTER, "Pinturería", R.drawable.profile_painter, R.drawable.ic_painter,""),
                    Master(PLUMBER, "Plomería", R.drawable.profile_plumbing, R.drawable.ic_plumber,""),
                    Master(AIRSERVICE, "Servicio de Aire", R.drawable.profile_air_service, R.drawable.ic_air_service,""),
                    Master(PCTECHNICIAN, "Servicio Técnico PC", R.drawable.profile_carpenter, R.drawable.ic_pc_technician,"")
                )
            }
        }
    }
}

