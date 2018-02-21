package com.kevindom.skeight.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.kevindom.skeight.R
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.wrapper.observeChildren
import str

class MessageManager(
        private val context: Context
) {

    private companion object {
        const val TAG = "MessageManager"

        const val MESSAGES = "messages"
        const val FIELD_ROOM_ID = "roomId"
    }

    private val database = FirebaseDatabase.getInstance().reference

    fun addOnMessageListener(roomId: String, listener: (Message) -> Unit) {
        database.child(MESSAGES)
                .orderByChild(FIELD_ROOM_ID)
                .equalTo(roomId)
                .addChildEventListener(observeChildren {
                    onChildAdded { snapshot, _ ->
                        val message = snapshot.getValue(Message::class.java)
                        message?.let(listener)
                    }
                })
    }

    fun reserveMessage(): String {
        return database.child(MESSAGES)
                .push()
                .key
    }

    fun addMessage(key: String, message: Message) {
        database
                .child(MESSAGES)
                .child(key)
                .setValue(message)
                .addOnFailureListener { Log.e(TAG, it.message ?: R.string.general_error_message.str(context), it.cause) }
    }

    fun addMessage(message: Message) {
        database
                .child(MESSAGES)
                .push()
                .setValue(message)
                .addOnFailureListener { Log.e(TAG, it.message ?: R.string.general_error_message.str(context), it.cause) }
    }
}