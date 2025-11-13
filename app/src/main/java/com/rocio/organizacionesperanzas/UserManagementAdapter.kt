package com.rocio.organizacionesperanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserManagementAdapter(
    private var users: List<User>,
    private val listener: OnUserActionClickListener
) : RecyclerView.Adapter<UserManagementAdapter.UserViewHolder>() {

    interface OnUserActionClickListener {
        fun onEditUser(user: User)
        fun onDeleteUser(user: User) // This can be kept for swipe-to-delete gestures in the future
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_management_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = user.username
        holder.userRole.text = user.role.name

        holder.editButton.setOnClickListener {
            listener.onEditUser(user)
        }

        // The delete button is not in the new layout.
        // If you want to delete, you can add a long-press or swipe gesture.
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userRole: TextView = itemView.findViewById(R.id.user_role)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
    }
}