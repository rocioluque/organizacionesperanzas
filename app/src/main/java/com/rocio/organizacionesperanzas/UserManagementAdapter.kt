package com.rocio.organizacionesperanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

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

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val userEmail: TextView = itemView.findViewById(R.id.user_email)
        private val userRole: TextView = itemView.findViewById(R.id.user_role)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(user: User) {
            // CORREGIDO: Usar 'username' directamente
            userName.text = user.username
            userEmail.visibility = View.GONE // Ocultamos el campo de email que ya no usamos

            userRole.text = user.role.name

            val roleBackground = when (user.role) {
                UserRole.ORGANIZER -> R.drawable.role_admin_background
                else -> R.drawable.role_user_background
            }
            val roleTextColor = when (user.role) {
                UserRole.ORGANIZER -> ContextCompat.getColor(itemView.context, R.color.white)
                else -> ContextCompat.getColor(itemView.context, R.color.oe_text_dark)
            }

            userRole.background = ContextCompat.getDrawable(itemView.context, roleBackground)
            userRole.setTextColor(roleTextColor)

            editButton.setOnClickListener { listener.onEditUser(user) }
            deleteButton.setOnClickListener { listener.onDeleteUser(user) }
        }
    }
}