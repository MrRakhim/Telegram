package com.example.telegram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseFirestore.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupView()

    }
    private fun setupView(){
        sign_up_button.setOnClickListener {
            val email = email_input_r.text.toString()
            val username = username_input_r.text.toString()
            val password = password_input_r.text.toString()

            signUp(email = email, password = password, username = username)

        }
    }

    private fun signUp(email: String, password: String, username: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    val userId = auth.uid.toString()
                    addUser(username, email, userId)
                    Toast.makeText(this, "Successfully registered!", Toast.LENGTH_LONG).show()
                    val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(loginIntent)
                    return@addOnCompleteListener

                }

                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }




    }
    private fun addUser(username: String, email: String, userid: String){
        val users = hashMapOf(
            "username" to username,
            "email" to email,
            "uid" to userid,
            "status" to "offline"
        )
        database.collection("users").document(userid).set(users)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){

                }
            }

    }
}
