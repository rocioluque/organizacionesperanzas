package com.rocio.organizacionesperanzas

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CategorySelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var userRole: UserRole? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Seleccionar Categoría"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        userRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_ROLE", UserRole::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_ROLE") as? UserRole
        }
        userId = intent.getStringExtra("USER_ID")

        recyclerView = findViewById(R.id.category_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadCategories()
    }

    private fun loadCategories() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val items: List<Any> = if (userRole == UserRole.ORGANIZER) {
                // Organizers get a simple list of all categories
                AppRepository.getAllCategories()
            } else {
                // Delegates get their assigned categories (which include team name)
                userId?.let { AppRepository.getAssignedCategories(it) } ?: emptyList()
            }
            
            progressBar.visibility = View.GONE
            recyclerView.adapter = CategoryAdapter(items, userRole)
        }
    }
}