package com.kevindom.skeight.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevindom.skeight.model.Room
import com.kevindom.skeight.model.User

class AnalyticsManager(context: Context) {
    private val analytics = FirebaseAnalytics.getInstance(context)

    fun logSelectItem(room: Room) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, room.id)
            putString(FirebaseAnalytics.Param.ITEM_NAME, room.name)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logSelectItem(user: User, selected: Boolean) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, user.id)
            putString(FirebaseAnalytics.Param.ITEM_NAME, user.email)
            putString(FirebaseAnalytics.Param.VALUE, selected.toString())
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logSignIn(userId: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, userId)
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }
}