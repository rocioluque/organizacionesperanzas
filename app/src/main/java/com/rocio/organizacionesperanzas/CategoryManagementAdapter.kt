package com.rocio.organizacionesperanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryManagementAdapter(
    private var categories: List<Category>,
    private val listener: OnCategoryActionClickListener
) : RecyclerView.Adapter<CategoryManagementAdapter.CategoryViewHolder>() {

    interface OnCategoryActionClickListener {
        fun onEditCategory(category: Category)
        fun onDeleteCategory(category: Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_management_list_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.name

        holder.editButton.setOnClickListener {
            listener.onEditCategory(category)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteCategory(category)
        }
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name_manage)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_category_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_category_button)
    }
}