package com.kevindom.skeight.firebase

import com.google.firebase.database.FirebaseDatabase
import com.kevindom.skeight.model.User
import com.kevindom.skeight.wrapper.observeValueEvent

class UserManager {

    private companion object {
        const val USERS = "users"
        const val FIELD_REGISTRATION_TOKENS = "registrationTokens"
    }

    private val database = FirebaseDatabase.getInstance().reference

    fun addOnUserListener(userId: String, completeListener: (User) -> Unit) {
        database.child(USERS)
                .child(userId)
                .addListenerForSingleValueEvent(observeValueEvent {
                    onDataChange {
                        val user = it.getValue(User::class.java)
                        user?.let { completeListener(it) }
                    }
                })
    }

    fun addOnUsersListener(completeListener: (List<User>) -> Unit) {
        database.child(USERS)
                .addListenerForSingleValueEvent(observeValueEvent {
                    onDataChange {
                        val users = mutableListOf<User>()
                        it.children
                                .map { it.getValue(User::class.java) }
                                .forEach { user -> user?.let { users.add(it) } }
                        completeListener(users)
                    }
                })
    }

    fun addUser(user: User, completeListener: () -> Unit, failureListener: (Exception) -> Unit) {
        database
                .child(USERS)
                .child(user.id)
                .setValue(user)
                .addOnCompleteListener { completeListener() }
                .addOnFailureListener { failureListener(it) }
    }

    fun addRegistrationToken(userId: String, registrationToken: String, completeListener: () -> Unit, failureListener: (Exception) -> Unit) {
        database
                .child(USERS)
                .child(userId)
                .child("$FIELD_REGISTRATION_TOKENS/$registrationToken")
                .setValue(true)
                .addOnCompleteListener { completeListener() }
                .addOnFailureListener { failureListener(it) }
    }

    fun checkIfExists(userId: String, exists: (Boolean) -> Unit) {
        database
                .child(USERS)
                .child(userId)
                .addListenerForSingleValueEvent(observeValueEvent {
                    onDataChange { exists(it.exists()) }
                })
    }
}