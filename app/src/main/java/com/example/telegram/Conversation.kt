package com.example.telegram

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.activity_main.*


class Conversation : AppCompatActivity() {
    private val database by lazy { FirebaseFirestore.getInstance()}
    private val auth by lazy { FirebaseAuth.getInstance() }
    companion object {
        const val CHAT_ID = "conversation"
        const val USER_ID = "USERLER"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        setupView()

    }
    override fun onResume() {
        super.onResume()
        LifecycleListener.onMoveToForeround()
    }

    override fun onPause() {
        super.onPause()
        LifecycleListener.onMoveToBackground()
    }
    private fun setupView(){
        message_list_view.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }
        val chatID1 = intent.getStringArrayListExtra(CHAT_ID)
        val user2idbolukereked= intent.getStringArrayListExtra(USER_ID)

        if (chatID1 != null){
            Log.d("chat", "not null")
            val chatid = chatID1.toString()
            getChatMessages(chatid)
        }else if(user2idbolukereked != null){
            title = user2idbolukereked[0]
            val user2 = user2idbolukereked.get(2)
            val  user1 = auth.currentUser!!.uid
            val chatid1 = listOf(user1,user2).toString()
            Log.d("ids, 1", chatid1)
            val chatid2 = listOf(user2,user1).toString()
            Log.d("ids, 2", chatid2)
            checkChat(chatid1, chatid2){definer ->
                if(definer == "ATOB"){
                    Log.d("ATOB", chatid2)
                    getChatMessages(chatid2)
                }else if (definer == "BTOA") {
                    Log.d("BTOA", chatid1)
                    getChatMessages(chatid1)
                }else{
                    getChatMessages(chatid1)
                }
            }
        }

            message_send_button.setOnClickListener {
                var sentMessage = message_input_view.text.toString()
                if (sentMessage.isNotEmpty()){
                        prepareChat(sentMessage)
                        Log.d("tag1", sentMessage)
                }
                else {
                    Toast.makeText(this, "Type something..", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun getChatMessages(chatid: String){
        val messageder = mutableListOf<Message>()
        database.collection("messages").orderBy("timestamp", Query.Direction.DESCENDING)
            .whereEqualTo("chatId", chatid)
            .get()
            .addOnSuccessListener {documents ->
                if (documents.isEmpty()){
                    Log.d("TAG", "ThereIsNoChat")
                }
                else{
                    for (it in documents) {
                        val message = it.toObject<Message>()!!
                        if(message != null) {
                            messageder += listOf(message)
                            message_list_view.adapter = MessageAdapter(messageder)

                        }
                    }
                    Log.d("m:", messageder.toString())
                }
            }


        Handler().postDelayed({
            message_list_view.smoothScrollToPosition(0)
        }, 100)
    }
    private fun prepareMessage(chatid: String ,messageText: String){
        val senderId = auth.currentUser?.uid
        val time = Timestamp.now()
        val message = Message(chatId = chatid, senderId = senderId!!, message = messageText,timestamp = time)
        addMessage(message)
        Log.d("tag4", "Message prepared")

    }
    private fun addMessage(message: Message){
        database
            .collection("messages")
            .document()
            .set(
                message
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("tag5", "Message added")
                    message_input_view.text.clear()
                    return@addOnCompleteListener
                }
            }
    }

    private fun prepareChat(message: String){
        var user2id = ""
        val user2idbolukereked= intent.getStringArrayListExtra(USER_ID)
        var user2 = User("","","","")
        if( user2idbolukereked != null){
            user2id = user2idbolukereked.get(2)
            title = user2idbolukereked[0]
            val user1id = auth.currentUser!!.uid
            val participantsID1 = listOf(user1id, user2id)
            val participantsID2 = listOf(user2id, user1id)
            checkChat(participantsID1.toString(),participantsID2.toString()){definer ->
                var participants = listOf(user2)
                if (definer == "BTOA"){
                    updateChat(participantsID1.toString(), message)
                    Log.d("definer", "BTOA")
                }
                else if (definer == "ATOB"){
                    updateChat(participantsID2.toString(),message)
                    Log.d("definer", "ATOB")
                }
                else if(definer == "NOT") {
                    val chat = Chat(
                        lastMessage = message,
                        lastMessageTimestamp = Timestamp.now(),
                        participantIds = participantsID1,
                        participants = participants

                    )
                    Log.d("definer", "NOT")
                    addChat(chat, participantsID1.toString())
                    getUser(auth.currentUser!!.uid, participantsID1.toString())
                }
            }
        }
        else{
            val chatID1 = intent.getStringArrayListExtra(CHAT_ID).toString()
            updateChat(chatID1, message)
        }

    }
    fun checkChat(usersid: String, usersid2: String, onComplete:(String) -> Unit) {
        val db = database.collection("chats")
        db.document(usersid).get()
            .addOnSuccessListener { it ->
                if (it.exists()){
                     val definer = "BTOA"
                    onComplete(definer)
                }else{
                    db.document(usersid2).get()
                        .addOnSuccessListener {cob ->
                            if (cob.exists()){
                                val definer = "ATOB"
                                onComplete(definer)
                            }
                            else{
                                val definer = "NOT"
                                onComplete(definer)
                            }
                        }
                }
            }
    }
    private fun updateChat(chatid: String, message: String){
        val db = database.collection("chats").document(chatid)
        db.update("lastMessage", message,
            "lastMessageTimestamp", Timestamp.now())
            .addOnSuccessListener {
                prepareMessage(chatid, message)
                setupView()
            }
            .addOnFailureListener { }
    }
    private fun addChat(chat: Chat, usersid: String){
        val db = database.collection("chats").document(usersid)
        db.set(chat)
            .addOnSuccessListener {
                prepareMessage(usersid, chat.lastMessage)
                setupView()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Log.d("tag3", "something wrong!!!")
            }

    }
    private fun getUser(id: String, chatid: String) {
        val user2idbolukereked= intent.getStringArrayListExtra(USER_ID)
        var user2 = User("","","","")
        if( user2idbolukereked != null){
            user2 = User(user2idbolukereked.get(0), user2idbolukereked.get(1), user2idbolukereked.get(2),"")
        }
        var participants = listOf(user2)
        database.collection("users")
            .document(id)
            .get().addOnCompleteListener { documentSnapshot ->
                if (documentSnapshot.isSuccessful) {
                val usergo = documentSnapshot.result?.toObject<User>()!!
                    participants += usergo
                    val db = database.collection("chats").document(chatid)
                    db.update("participants", participants)
                        .addOnSuccessListener {
                            Log.d("users:::", participants.toString())
                        }
                        .addOnFailureListener { }
                }


                }
            }
}

