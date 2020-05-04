package com.example.telegram

data class User(
    var username: String,
    var email: String,
    var uid: String,
    var status: String
) {
    constructor(): this("", "", "","offline")
}