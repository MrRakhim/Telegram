package com.example.telegram

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_item.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter (
    private val chat: List<Chat>,
    private val onChatClick: (ArrayList<String>) -> Unit
): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)

        return ChatViewHolder(view)
    }
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bindItem(chat[position])
    }
    override fun getItemCount(): Int = chat.size

    private val auth by lazy { FirebaseAuth.getInstance() }

    inner class ChatViewHolder(
        private val view: View
    ):RecyclerView.ViewHolder(view){
        fun bindItem(chat: Chat){
            var username = "username"
            var user2id = "null"
            var userids: MutableList<String> = ArrayList<String>(2)

            for (i in chat.participants){
                if (i.uid != auth.currentUser!!.uid){
                    username = i.username
                }
            }

            val date = chat.lastMessageTimestamp.toDate()
            val cal = Calendar.getInstance()
            cal.time = date

            var hours = cal.get(Calendar.HOUR_OF_DAY)
            val min = cal.get(Calendar.MINUTE)

            var hour2 = cal.get(Calendar.HOUR_OF_DAY).toString()
            var min2 = cal.get(Calendar.MINUTE).toString()
            if (hours<10){
                hour2 = "0" + cal.get(Calendar.HOUR_OF_DAY).toString()
            }
            if (min<10){
                min2 = "0"+ cal.get(Calendar.MINUTE).toString()
            }
            val month = cal.get(Calendar.DAY_OF_WEEK).toString()
            val day = cal.get(Calendar.DAY_OF_MONTH).toString()

            var time = "$month $day $hour2:$min2"




            view.username_view.text = username
            view.message_view.text = chat.lastMessage
            view.date_view.text = time

            view.setOnClickListener {
                onChatClick(chat.participantIds as ArrayList<String>)
            }

        }
    }
}