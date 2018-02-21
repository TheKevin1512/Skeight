package com.kevindom.skeight.wrapper

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ValueEventListenerWrapper : ValueEventListener {

    private companion object {
        const val TAG = "ValueEventWrapper"
    }

    private var callbackCancelled: ((DatabaseError) -> Unit)? = null
    private var callbackDataChanged: ((DataSnapshot) -> Unit)? = null

    fun onDataChange(callbackDataChanged: ((DataSnapshot) -> Unit)? = null) {
        this.callbackDataChanged = callbackDataChanged
    }

    fun onCancelled(callbackCancelled: ((DatabaseError) -> Unit)? = null) {
        this.callbackCancelled = callbackCancelled
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "Something went wrong", error.toException())
        this.callbackCancelled?.invoke(error)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        this.callbackDataChanged?.invoke(snapshot)
    }
}

inline fun observeValueEvent(wrap: ValueEventListenerWrapper.() -> Unit): ValueEventListenerWrapper {
    val wrapper = ValueEventListenerWrapper()
    wrapper.wrap()
    return wrapper
}