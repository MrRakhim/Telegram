package com.example.telegram

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val database by lazy { FirebaseFirestore.getInstance()}
private val auth by lazy { FirebaseAuth.getInstance() }
object LifecycleListener : LifecycleObserver{

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeround(){
        if(auth.currentUser?.uid != null){
            val db = database.collection("users").document(auth.currentUser!!.uid)
            db.update("status", "online")
                .addOnSuccessListener {
                    Log.d("users:::", "online")
                }
                .addOnFailureListener { }
        }else{
            Log.d("lifecycle", "keldim")
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground(){
        if(auth.currentUser?.uid != null){
            val db = database.collection("users").document(auth.currentUser!!.uid)
            db.update("status", "offline")
                .addOnSuccessListener {
                    Log.d("users:::", "offline")
                }
                .addOnFailureListener { }
        }else{
            Log.d("lifecycle", "ka kelem")
        }

    }
}