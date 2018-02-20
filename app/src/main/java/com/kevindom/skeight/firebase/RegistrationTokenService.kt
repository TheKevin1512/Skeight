package com.kevindom.skeight.firebase

import android.util.Log
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import com.kevindom.skeight.storage.Preferences

class RegistrationTokenService : FirebaseInstanceIdService() {

    private companion object {
        const val TAG = "Token Service"
    }

    private val preferences: Preferences by lazy { appKodein().instance<Preferences>() }

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)
        preferences.saveRegistrationToken(refreshedToken)
    }
}