package com.rocio.organizacionesperanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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
                        loadCategories()
                    } else {
                        Toast.makeText(this@CategoryManagementActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditCategoryDialog(category: Category?) {
        val isEditing = category != null

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val categoryNameInput = dialogView.findViewById<EditText>(R.id.category_name_input)
        val addButton = dialogView.findViewById<Button>(R.id.add_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        if (isEditing) {
            categoryNameInput.setText(category!!.name)
            addButton.text = "Guardar"
        } else {
            addButton.text = "Añadir"
        }

        addButton.setOnClickListener {
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
                        loadCategories()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this@CategoryManagementActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}