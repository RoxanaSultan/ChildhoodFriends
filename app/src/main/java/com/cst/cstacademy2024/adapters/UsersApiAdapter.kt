package com.cst.cstacademy2024.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cst.cstacademy2024.models.UserAPI
import com.cst.cstacademy2024.R  // Replace with your actual R class from your project

class UsersApiAdapter(private val users: List<UserAPI>) : RecyclerView.Adapter<UsersApiAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstNameTextView: TextView = itemView.findViewById(R.id.tv_first_name)
        val lastNameTextView: TextView = itemView.findViewById(R.id.tv_last_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.firstNameTextView.text = user.name.firstname
        holder.lastNameTextView.text = user.name.lastname
    }

    override fun getItemCount() = users.size
}