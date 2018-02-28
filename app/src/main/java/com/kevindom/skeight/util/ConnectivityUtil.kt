package com.kevindom.skeight.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by kevindom on 26/02/2018.
 */
object ConnectivityUtil {

    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}