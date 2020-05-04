package com.example.telegram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_start_conversation.*
import java.util.*

class StartConversation : AppCompatActivity() {
    private val database by lazy { FirebaseFirestore.getInstance()}
    private val auth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_conversation)
        title = "Select User"
        setupView()
    }
    override fun onResume() {
        super.onResume()
        LifecycleListener.onMoveToForeround()
        setupView()
    }

    override fun onPause() {
        super.onPause()
        LifecycleListener.onMoveToBackground()
    }
    private fun setupView(){
        user_list_view.layoutManager = LinearLayoutManager(this@StartConversation)
        var userlar = listOf<User>()
        database.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach {
                    val user = it.toObject(User::class.java)!!
                    if(user?.uid != auth.currentUser!!.uid && user != null) {
                        userlar += listOf(user)
                        user_list_view.adapter = UserAdapter(userlar,
                            onUserClick = {user ->
                                val intent = Intent(this@StartConversation, Conversation::class.java)
                                intent.putStringArrayListExtra(Conversation.USER_ID, user)
                                startActivity(intent)
                            })
                    }
                }
            }
    }



}
