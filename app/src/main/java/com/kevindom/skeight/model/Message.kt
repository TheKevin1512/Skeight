package com.kevindom.skeight.model

import java.io.Serializable

data class Message(
        val roomId: String,
        val userId: String,
        val name: String,
        val content: String,
        val timestamp: Long
) : Serializable {
    //For Firebase retrieval
    constructor() : this(
            "",
            "",
            "",
            "",
            0L
    )
}