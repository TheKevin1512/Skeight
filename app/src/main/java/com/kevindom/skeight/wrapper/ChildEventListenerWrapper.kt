package com.kevindom.skeight.wrapper

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChildEventListenerWrapper : ChildEventListener {

    private companion object {
        const val TAG = "ChildEventWrapper"
    }
    
    private var callbackMoved: ((DataSnapshot, String?) -> Unit)? = null
    private var callbackChanged: ((DataSnapshot, String?) -> Unit)? = null
    private var callbackAdded: ((DataSnapshot, String?) -> Unit)? = null
    private var callbackRemoved: ((DataSnapshot) -> Unit)? = null
    private var callbackCanceled: ((DatabaseError) -> Unit)? = null

    fun onChildMoved(callbackMoved: ((DataSnapshot, String?) -> Unit)? = null) {
        this.callbackMoved = callbackMoved
    }

    fun onChildChanged(callbackChanged: ((DataSnapshot, String?) -> Unit)? = null) {
        this.callbackChanged = callbackChanged
    }

    fun onChildAdded(callbackAdded: ((DataSnapshot, String?) -> Unit)? = null) {
        this.callbackAdded = callbackAdded
    }

    fun onChildRemoved(callbackRemoved: ((DataSnapshot) -> Unit)? = null) {
        this.callbackRemoved = callbackRemoved
    }

    fun onCancelled(callbackCanceled: ((DatabaseError) -> Unit)? = null) {
        this.callbackCanceled = callbackCanceled
    }

    override fun onChildMoved(snapshot: DataSnapshot, prevChildName: String?) {
        callbackMoved?.invoke(snapshot, prevChildName)
    }

    override fun onChildChanged(snapshot: DataSnapshot, prevChildName: String?) {
        callbackChanged?.invoke(snapshot, prevChildName)
    }

    override fun onChildAdded(snapshot: DataSnapshot, prevChildName: String?) {
        callbackAdded?.invoke(snapshot, prevChildName)
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        callbackRemoved?.invoke(snapshot)
    }

    override fun onCancelled(snapshot: DatabaseError) {
        Log.e(TAG, snapshot.message)
        callbackCanceled?.invoke(snapshot)
    }
}

inline fun observeChildren(wrap: ChildEventListenerWrapper.() -> Unit) : ChildEventListenerWrapper {
    val wrapper = ChildEventListenerWrapper()
    wrapper.wrap()
    return wrapper
}
