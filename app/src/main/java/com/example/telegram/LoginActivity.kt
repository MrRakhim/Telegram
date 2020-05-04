package com.example.telegram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupView()
    }
    private fun setupView(){
        sign_in_button.setOnClickListener {
            val email = email_input.text.toString()
            val password = password_input.text.toString()
            signIn(email = email, password = password)
        }
        go_register.setOnClickListener {
            val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
    private fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val loggedIn = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(loggedIn)
                    Log.d("taaag", "WTF")
                    return@addOnCompleteListener
                }

                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

            }
    }
}
