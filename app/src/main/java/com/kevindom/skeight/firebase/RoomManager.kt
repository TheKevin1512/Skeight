package com.kevindom.skeight.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.kevindom.skeight.R
import com.kevindom.skeight.model.Room
import com.kevindom.skeight.wrapper.observeChildren
import str

class RoomManager(
        private val context: Context
) {

    private companion object {
        const val TAG = "RoomManager"

        const val ROOMS = "rooms"
        const val FIELD_USER_IDS = "userIds"
    }

    private val database = FirebaseDatabase.getInstance().reference

    fun addOnRoomsListener(userId: String, listener: RoomListener) {
        database.child(ROOMS)
                .orderByChild(FIELD_USER_IDS + "/" + userId)
                .equalTo(true)
                .addChildEventListener(observeChildren {
                    onChildAdded { snapshot, _ ->
                        val room = snapshot.getValue(Room::class.java)
                        room?.let {
                            listener.onRoomAdded(it)
                        }
                    }
                    onChildRemoved {
                        val room = it.getValue(Room::class.java)
                        room?.let {
                            listener.onRoomRemoved(it)
                        }
                    }
                    onChildChanged { snapshot, _ ->
                        val room = snapshot.getValue(Room::class.java)
                        room?.let {
                            listener.onRoomChanged(it)
                        }
                    }
                })
    }

    fun addRoom(name: String, userIds: Map<String, Boolean>, completeListener: () -> Unit) {
        val ref = database
                .child(ROOMS)
                .push()
        ref
                .setValue(Room(ref.key, name, userIds))
                .addOnCompleteListener {
                    completeListener()
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message ?: R.string.general_error_message.str(context), it.cause)
                }
    }

    interface RoomListener {
        fun onRoomAdded(room: Room)
        fun onRoomRemoved(room: Room)
        fun onRoomChanged(room: Room)
    }
}