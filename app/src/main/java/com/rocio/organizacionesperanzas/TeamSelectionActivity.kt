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

class TeamSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_selection)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val categoryId = intent.getStringExtra("CATEGORY_ID")
        val categoryName = intent.getStringExtra("CATEGORY_NAME")
        val userRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_ROLE", UserRole::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_ROLE") as? UserRole
        }

        // --- DEBUG LOG ---
        println("TeamSelectionActivity created. Received categoryId: $categoryId")

        supportActionBar?.title = "Equipos en $categoryName"

        recyclerView = findViewById(R.id.team_recycler_view)
        progressBar = findViewById(R.id.team_progress_bar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (categoryId != null) {
            loadTeams(categoryId, userRole)
        } else {
            progressBar.visibility = View.GONE
            println("ERROR: categoryId is null, cannot load teams.")
        }
    }

    private fun loadTeams(categoryId: String, userRole: UserRole?) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            val teams = AppRepository.getTeamsByCategory(categoryId)
            progressBar.visibility = View.GONE

            recyclerView.adapter = TeamAdapter(teams, categoryId, userRole)
        }
    }
}