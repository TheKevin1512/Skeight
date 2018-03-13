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

    fun addOnUserListener(roomId: String, addListener: (String) -> Unit, removeListener: (String) -> Unit) {
        database.child(ROOMS)
                .child(roomId)
                .child(FIELD_USER_IDS)
                .addChildEventListener(observeChildren {
                    onChildAdded { snapshot, s ->
                        val userId = snapshot.key
                        addListener(userId)
                    }
                    onChildRemoved {
                        val userId = it.key
                        removeListener(userId)
                    }
                })
    }

    fun addOnRoomsListener(userId: String, listener: RoomListener) {
        database.child(ROOMS)
                .orderByChild("$FIELD_USER_IDS/$userId")
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

    fun addUser(roomId: String, userId: String) {
        database.child(ROOMS)
                .child(roomId)
                .child(FIELD_USER_IDS)
                .child(userId)
                .setValue(true)
                .addOnFailureListener {
                    Log.e(TAG, it.message ?: R.string.general_error_message.str(context), it.cause)
                }
    }

    fun removeUser(roomId: String, userId: String) {
        database.child(ROOMS)
                .child(roomId)
                .child(FIELD_USER_IDS)
                .child(userId)
                .removeValue()
    }

    interface RoomListener {
        fun onRoomAdded(room: Room)
        fun onRoomRemoved(room: Room)
        fun onRoomChanged(room: Room)
    }
}