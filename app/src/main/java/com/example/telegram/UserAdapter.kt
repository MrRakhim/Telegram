package com.example.telegram

import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.layout_item.view.*

class UserAdapter (
    private val user: List<User>,
    private val onUserClick: (ArrayList<String>) -> Unit
): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindItem(user[position])
    }
    override fun getItemCount(): Int = user.size

    inner class UserViewHolder(
        private val view: View
    ): RecyclerView.ViewHolder(view){
            fun bindItem(user: User){
                val userList: ArrayList<String> = ArrayList<String>(4)
                 userList.add(user.username)
                userList.add(user.email)
                userList.add(user.uid)
                userList.add(user.status)

                view.username_view.text = user.username
                view.message_view.text = user.email
                view.date_view.text = user.status
                view.setOnClickListener {
                    onUserClick(userList)
                }
            }

        }


    }