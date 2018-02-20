package com.kevindom.skeight.model

data class User(
        val id: String,
        val name: String,
        val email: String,
        val phoneNumber: String?,
        val photoUrl: String?,
        val registrationTokens: Map<String, Boolean>
) {
    //For Firebase retrieval
    constructor() : this(
            "",
            "",
            "",
            null,
            null,
            emptyMap()
    )
}