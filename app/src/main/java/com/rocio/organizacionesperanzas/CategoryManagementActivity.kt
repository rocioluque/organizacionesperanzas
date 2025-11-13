package com.rocio.organizacionesperanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class CategoryManagementActivity : AppCompatActivity(), CategoryManagementAdapter.OnCategoryActionClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryManagementAdapter
    private lateinit var progressBar: ProgressBar
    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_management)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestionar Categorías"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView = findViewById(R.id.categories_recycler_view)
        progressBar = findViewById(R.id.categories_progress_bar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoryManagementAdapter(categories, this)
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.add_category_fab)
        fab.setOnClickListener {
            showAddOrEditCategoryDialog(null)
        }

        loadCategories()
    }

    private fun loadCategories() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = AppRepository.getAllCategories()
            categories.clear()
            categories.addAll(result)
            adapter.updateCategories(categories)
            progressBar.visibility = View.GONE
        }
    }

    override fun onEditCategory(category: Category) {
        showAddOrEditCategoryDialog(category)
    }

    override fun onDeleteCategory(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar la categoría '${category.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    val success = AppRepository.deleteCategory(category.id)
                    progressBar.visibility = View.GONE
                    if (success) {
                        Toast.makeText(this@CategoryManagementActivity, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                        val position = categories.indexOfFirst { it.id == category.id }
                        if (position != -1) {
                            categories.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    } else {
                        Toast.makeText(this@CategoryManagementActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditCategoryDialog(category: Category?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val categoryNameInput = dialogView.findViewById<EditText>(R.id.category_name_input)

        val isEditing = category != null
        if (isEditing) {
            categoryNameInput.setText(category!!.name)
        }

        val title = if (isEditing) "Editar Categoría" else "Añadir Categoría"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(if (isEditing) "Guardar" else "Añadir") { _, _ ->
                val name = categoryNameInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        val newCategoryData = if (isEditing) category!!.copy(name = name) else Category(id = "", name = name)
                        val result = if (isEditing) {
                            AppRepository.updateCategory(category!!.id, newCategoryData)
                        } else {
                            AppRepository.addCategory(newCategoryData)
                        }
                        
                        progressBar.visibility = View.GONE
                        if (result != null) {
                            Toast.makeText(this@CategoryManagementActivity, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                            if (isEditing) {
                                val position = categories.indexOfFirst { it.id == result.id }
                                if (position != -1) {
                                    categories[position] = result
                                    adapter.notifyItemChanged(position)
                                }
                            } else {
                                categories.add(result)
                                adapter.notifyItemInserted(categories.size - 1)
                            }
                        } else {
                            Toast.makeText(this@CategoryManagementActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}