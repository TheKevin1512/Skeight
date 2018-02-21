package com.kevindom.skeight.model

import java.io.Serializable

data class Message(
        val roomId: String,
        val userId: String,
        val name: String,
        var content: String,
        val timestamp: Long,
        val photoUrl: String? = null
) : Serializable {
    //For Firebase retrieval
    constructor() : this(
            "",
            "",
            "",
            "",
            0L,
            null
    )
}