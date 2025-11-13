package com.rocio.organizacionesperanzas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val items: List<Any>,
    private val userRole: UserRole?
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        when (val item = items[position]) {
            is AssignedCategory -> {
                holder.teamName.text = item.teamName
                holder.categoryName.text = item.category.name
                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, PlayerManagementActivity::class.java).apply {
                        putExtra("CATEGORY_ID", item.category.id)
                        putExtra("CATEGORY_NAME", item.category.name)
                        putExtra("USER_ROLE", userRole)
                    }
                    context.startActivity(intent)
                }
            }
            is Category -> {
                holder.teamName.visibility = View.GONE // Hide team name for admins
                holder.categoryName.text = item.name
                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, TeamSelectionActivity::class.java).apply {
                        putExtra("CATEGORY_ID", item.id)
                        putExtra("CATEGORY_NAME", item.name)
                        putExtra("USER_ROLE", userRole)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.team_name_item)
        val categoryName: TextView = itemView.findViewById(R.id.category_name_item)
    }
}