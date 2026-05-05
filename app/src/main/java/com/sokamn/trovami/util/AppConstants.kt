package com.sokamn.trovami.util

import java.util.regex.Pattern

class AppConstants {
    companion object{
        val BAD_WORDS = arrayListOf("sorete","imbecil","tarado","pelotudo","pajero","pajera","pelotuda","tarada","puto","puta","concha","culo","poronga","verga","pito","pene" + "nigga" , "trola" , "trolo" , "caca" , "down" , "mierda" , "nazi" , "hitler" , "estupido" , "coger" , "cojer" , "pendejo " , "pendeja" , "porno" , "orto" , "sexo" , "pinche" , "pinchi" , "cojo" , "cabrón" , "cabrona" , "mames" , "pendejos" , "pendejas" , "chinga" , "mamadas" , "pendejadas" , "mama huevo" , "pete" , "wueon" , "xuxa" , "weon" , "weonado" , "weona" , "coño" , "aguevoniado" , "guevon" , "pajuo" , "marica", "monda" , "marrana" , "marrano" ,"monda" , "pijudo" , "hijueputa" , "cotopla" , "pichurria" , "picha" , "mother fucker" , "fuck" , "ass" , "orgy" , "bitch" , "suck" , "my balls" , "slut " , "whore" , "hoe" , "chupamela" , "culito" , "cojida" , "cojiendo" , "zoofilia" , "putito" , "reputo" , "free viagra" , "taradito", "taradita" , "pelotudito" , "pelotudita" , "pelotuditos", "pelotuditas" , "putita" , "poronguita" , "verguita" , "pitito" , "trolito" , "trolita" , "caquita" , "estupidito" , "estupidita" , "pendejito" , "pendejita" , "putitos" , "putitas" , "poronguitas" , "porongotas" , "porongota" , "porongon" , "verguitas", "vergotas" , "vergota" , "pititos" , "pitotes" , "pitote" , "trolitos" , "trolitas" , "caquitas" , "cacotas" , "estupiditos" , "estupiditas" , "pendejitos" , "pendejitas" , "feto" , "cigoto" , "caka" , "kaka" , "kk" , "joto" , "jota" , "kaco" , "kago" , "kojo" , "kulo" , "mamo" , "meaas" , "mion" , "mula" , "pedo" , "qulo" , "buey" , "caco" , "cago" , "cako" , "coja" , "coji" , "guey" , "kaca" , "kaga" , "koge" , "mame" , "mear" , "meon" , "moco")
        val PASSWORD_REGEX = Pattern.compile("^" + "(?=.*[A-Z])" + "(?=.*[0-9])" + ".{6,}" + "$")
        const val PASO1 = 0
        const val PASO2 = 1
        const val PASO3 = 2
        const val PASO4 = 3
        const val MASON = 0
        const val CARPENTER = 1
        const val ELECTRICIAN = 2
        const val TRUCKFREIGHTER = 3
        const val GAS = 4
        const val GARDENER = 5
        const val TRUCKMOVING = 6
        const val NANNY = 7
        const val PAINTER = 8
        const val PLUMBER = 9
        const val AIRSERVICE = 10
        const val PCTECHNICIAN = 11
        const val EMAIL = 12
        const val GOOGLE = 13
        const val FACEBOOK = 14
        const val SELECTED = 77
        const val UNSELECTED = 88
        const val MIN_SIGNUP_LENGTH = 6
        const val USER_REFERENCE = "users"
        const val SAVE_REFERENCE = "saves"
        const val CHAT_REFERENCE = "chats"
        const val PRESENCE_REFERENCE = "presence"
    }
}