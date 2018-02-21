package com.kevindom.skeight.util

object NamingHelper {

    fun getFirstName(name: String): String? {
        val pieces = name.split(" ")
        return if (pieces.isNotEmpty())
            pieces.first()
        else null
    }
}