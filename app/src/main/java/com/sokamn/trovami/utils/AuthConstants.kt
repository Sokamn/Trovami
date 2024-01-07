package com.sokamn.trovami.utils

import java.util.regex.Pattern

object AuthConstants {
    val PASSWORD_REGEX = Pattern.compile("^" + "(?=.*[A-Z])" + "(?=.*[0-9])" + ".{6,}" + "$")
    const val CLIENT_ID = "250852353586-ocuoefabfk8fl5luokcpq0h1uksa60ta.apps.googleusercontent.com"
    const val PASO1 = 0
    const val PASO2 = 1
    const val PASO3 = 2
    const val PASO4 = 3
    const val EMAIL = 12
    const val GOOGLE = 13
    const val SELECTED = 77
    const val UNSELECTED = 88
    const val MIN_TEXT_CONTENT = 5
    const val CURRENT_USER_UID_KEY_EXTRA = "current_user"
    const val PROFILE_PICTURE_KEY_EXTRA = "profilePicture"
    const val GMAIL_KEY_EXTRA = "gMail"
    const val NAME_KEY_EXTRA = "nName"
    const val LOGIN_METHOD_KEY_EXTRA = "loginMethod"
    const val LAST_ACTIVITY_KEY_EXTRA = "lastActivity"
}