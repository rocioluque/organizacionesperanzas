package com.rocio.organizacionesperanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class UserManagementAdapter(
    private var users: List<User>,
    private val listener: OnUserActionClickListener
) : RecyclerView.Adapter<UserManagementAdapter.UserViewHolder>() {

    interface OnUserActionClickListener {
        fun onEditUser(user: User)
        fun onDeleteUser(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_management_list_item, parent, false)
        return UserViewHolder(view)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        // CORREGIDO: Usar el email, que sí existe en el modelo User
        val nameFromEmail = user.email.substringBefore('@')
            .replace('.', ' ')
            .split(' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                if (word.isNotEmpty()) {
                    word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()
                } else {
                    ""
                }
            }
        
        holder.userName.text = nameFromEmail
        holder.userEmail.text = user.email
        holder.userEmail.visibility = View.VISIBLE

        holder.userRole.text = user.role.name

        // Set background and text color based on role
        val roleBackground = when (user.role) {
            UserRole.ORGANIZER -> R.drawable.role_admin_background
            else -> R.drawable.role_user_background
        }
        val roleTextColor = when (user.role) {
            UserRole.ORGANIZER -> ContextCompat.getColor(holder.itemView.context, R.color.white)
            else -> ContextCompat.getColor(holder.itemView.context, R.color.oe_text_dark)
        }

        holder.userRole.background = ContextCompat.getDrawable(holder.itemView.context, roleBackground)
        holder.userRole.setTextColor(roleTextColor)

        holder.editButton.setOnClickListener {
            listener.onEditUser(user)
        }
         holder.deleteButton.setOnClickListener { listener.onDeleteUser(user) }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userEmail: TextView = itemView.findViewById(R.id.user_email)
        val userRole: TextView = itemView.findViewById(R.id.user_role)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }
}