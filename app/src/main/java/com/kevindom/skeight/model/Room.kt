package com.kevindom.skeight.model

import java.io.Serializable

data class Room(
        val id: String,
        val name: String,
        val userIds: Map<String, Boolean>,
        val lastMessage: String = "Start a conversation!"
) : Serializable {
    //For Firebase retrieval
    constructor() : this(
            "",
            "",
            emptyMap(),
            ""
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Room

        return this.id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}