package com.kevindom.skeight.firebase

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.wrapper.observeChildren

class MessageManager {

    private companion object {
        const val TAG = "MessageManager"

        const val MESSAGES = "messages"
    }

    private val database = FirebaseDatabase.getInstance().reference

    fun addOnMessageListener(roomId: String, listener: (Message) -> Unit) {
        database.child(MESSAGES)
                .orderByChild("roomId")
                .equalTo(roomId)
                .addChildEventListener(observeChildren {
                    onChildAdded { snapshot, _ ->
                        val message = snapshot.getValue(Message::class.java)
                        message?.let(listener)
                    }
                })
    }

    fun addMessage(message: Message) {
        database
                .child(MESSAGES)
                .push()
                .setValue(message)
                .addOnFailureListener {
                    Log.e(TAG, it.message ?: "Something went wrong", it.cause)
                }
    }
}