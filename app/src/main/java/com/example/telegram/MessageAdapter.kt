package com.example.telegram

import android.net.ParseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.layout_item_message_in.view.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val message: List<Message> = emptyList()
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutResource = if (viewType == 0)
            R.layout.layout_item_message_out
        else
            R.layout.layout_item_message_in

        return MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(layoutResource, parent, false)
        )
    }
    private val auth by lazy { FirebaseAuth.getInstance() }
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindMessage(message = message[position])
    }
    override fun getItemCount(): Int = message.size

    override fun getItemViewType(position: Int): Int {
        return if (message[position].senderId == auth.currentUser!!.uid)
            0
        else
            1
    }
    inner class MessageViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bindMessage(message: Message) {
            val date = message.timestamp.toDate()
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
            val time = hour2 + ":"+ min2


            view.text_view.text = message.message
            view.date_text_view.text = time
            Log.d("mess", message.message)
        }


    }
}