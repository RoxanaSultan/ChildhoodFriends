package com.cst.cstacademy2024

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cst.cstacademy2024.models.User

class UsersAdapter(private var users: List<User>) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateList(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.tv_first_name)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.tv_last_name)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.tv_phone_number)

        fun bind(user: User) {
            firstNameTextView.text = user.firstName
            lastNameTextView.text = user.lastName
            phoneNumberTextView.text = user.phone
        }
    }
}