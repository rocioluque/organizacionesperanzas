package com.rocio.organizacionesperanzas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val items: List<Category>,
    private val userRole: UserRole?
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        holder.categoryName.text = item.name

        holder.itemLayout.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TeamSelectionActivity::class.java).apply {
                putExtra("CATEGORY_ID", item.id)
                putExtra("CATEGORY_NAME", item.name)
                putExtra("USER_ROLE", userRole)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.category_item_layout)
    }
}