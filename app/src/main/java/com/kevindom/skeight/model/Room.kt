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
}