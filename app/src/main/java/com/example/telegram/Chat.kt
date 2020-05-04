package com.example.telegram

import com.google.firebase.Timestamp


data class Chat(
    val lastMessage: String,
    val lastMessageTimestamp: Timestamp,
    var participantIds: List<String>,
    val participants: List<User>
){
    constructor(): this("", Timestamp.now(), emptyList(), emptyList())
}