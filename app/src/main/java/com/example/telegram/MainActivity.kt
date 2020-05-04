package com.example.telegram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start_conversation.*

class MainActivity : AppCompatActivity() {
    private val database by lazy {FirebaseFirestore.getInstance()}
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId){
            R.id.logout_button->{
                LifecycleListener.onMoveToBackground()
                auth.signOut()
                setupView()
                super.onStop()
                true
            }
            else->{
                return super.onOptionsItemSelected(item)

            }
        }
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

    override fun onStop() {
        super.onStop()
    }
    private fun setupView(){
        if(auth.currentUser != null){
            getUsername()
            chatsView()
        }else{
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }


    }

    private fun chatsView(){
        list_view.layoutManager = LinearLayoutManager(this@MainActivity)
        var chats = listOf<Chat>()
        val db = database.collection("chats")
        db.whereArrayContains("participantIds", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty){
                    Log.d("TAG", "ThereIsNoChat")
                }
                else{
                    for (document in documents) {
                        val chat = document.toObject<Chat>()!!
                        if (chat != null) {
                            chats += listOf(chat)
                            list_view.adapter = ChatAdapter(chats,
                                onChatClick = { id ->
                                    val intent = Intent(this@MainActivity, Conversation::class.java)
                                    intent.putStringArrayListExtra(Conversation.CHAT_ID, id)
                                    startActivity(intent)
                                    })
                            }
                    }
                }

            }
        add_button.setOnClickListener {
            val startConv = Intent(this@MainActivity, StartConversation::class.java)
            startActivity(startConv)
        }


    }
    private fun getUsername(){
        val db = database.collection("users").document(auth.currentUser!!.uid)
            db.get().addOnCompleteListener { documentSnapshot ->
                if (documentSnapshot.isSuccessful) {
                    val user = documentSnapshot.result?.toObject<User>()!!
                    setTitle(user.username)
                }
            }


    }


}
