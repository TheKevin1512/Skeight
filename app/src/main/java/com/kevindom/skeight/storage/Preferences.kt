package com.kevindom.skeight.storage

import android.content.Context

class Preferences(context: Context) {

    private companion object {
        const val NAME = "Skeight Preferences"

        const val KEY_REGISTRATION_TOKEN = "registrationToken"
    }

    private val storage = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun saveRegistrationToken(registrationToken: String) = storage.edit().putString(KEY_REGISTRATION_TOKEN, registrationToken).apply()

    fun getRegistrationToken(): String = storage.getString(KEY_REGISTRATION_TOKEN, null)
}