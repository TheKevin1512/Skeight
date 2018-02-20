package com.kevindom.skeight.auth

import android.app.Activity
import android.support.v4.app.FragmentActivity
import com.firebase.ui.auth.AuthUI

object AuthManager {

    fun signIn(activity: Activity, responseCode: Int) {
        activity.startActivityForResult(
                AuthUI
                        .getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(listOf(
                                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                        ))
                        .build(),
                responseCode
        )
    }

    fun signOut(activity: FragmentActivity, completedListener: (() -> Unit)? = null) {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    completedListener?.invoke()
                }
    }
}