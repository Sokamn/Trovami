package com.sokamn.trovami.utils

import java.util.regex.Pattern

class AppConstants {
    companion object {

        // PASSWORD PATTERN
        val PASSWORD_REGEX = Pattern.compile("^" + "(?=.*[A-Z])" + "(?=.*[0-9])" + ".{6,}" + "$")

        // AUTH
        const val PASO1 = 0
        const val PASO2 = 1
        const val PASO3 = 2
        const val PASO4 = 3
        const val EMAIL = 12
        const val GOOGLE = 13
        const val SELECTED = 77
        const val UNSELECTED = 88
        const val MIN_TEXT_CONTENT = 5
        const val CLIENT_ID =
            "250852353586-ocuoefabfk8fl5luokcpq0h1uksa60ta.apps.googleusercontent.com"
        const val USER_KEY_PREFS = "current_user"


        // JOBS
        const val INIT_JOB_WORD = "---"

        // PAGING POSTS
        const val PAGE_SIZE = 4

        // MASTERS
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

    }
}