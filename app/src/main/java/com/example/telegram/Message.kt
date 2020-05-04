package com.example.telegram

import com.google.firebase.Timestamp

data class Message(
    val chatId: String,
    val senderId: String,
    val message: String,
    val timestamp: Timestamp
) {
    constructor(): this("","","", Timestamp.now())
}