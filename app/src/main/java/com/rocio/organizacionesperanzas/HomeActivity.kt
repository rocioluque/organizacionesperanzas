package com.rocio.organizacionesperanzas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Get data from intent
        val userRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_ROLE", UserRole::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_ROLE") as? UserRole
        }
        val userId = intent.getStringExtra("USER_ID")

        // Get views from the layout
        val titleTextView = findViewById<TextView>(R.id.home_title)
        val managePlayersButton = findViewById<Button>(R.id.manage_players_button)
        val manageCategoriesButton = findViewById<Button>(R.id.manage_categories_button)
        val manageTeamsButton = findViewById<Button>(R.id.manage_teams_button)
        val manageUsersButton = findViewById<Button>(R.id.manage_users_button)

        if (userRole == UserRole.ORGANIZER) {
            // --- ADMIN / ORGANIZER ---
            supportActionBar?.title = "Panel de Administrador"
            titleTextView.visibility = View.VISIBLE
            manageCategoriesButton.visibility = View.VISIBLE
            manageTeamsButton.visibility = View.VISIBLE
            manageUsersButton.visibility = View.VISIBLE

            managePlayersButton.text = "Ver Jugadores por Categoría"
            managePlayersButton.setOnClickListener {
                val intent = Intent(this, CategorySelectionActivity::class.java).apply {
                    putExtra("USER_ROLE", userRole)
                    putExtra("USER_ID", userId)
                }
                startActivity(intent)
            }

            manageCategoriesButton.setOnClickListener {
                val intent = Intent(this, CategoryManagementActivity::class.java)
                startActivity(intent)
            }
            manageTeamsButton.setOnClickListener {
                val intent = Intent(this, TeamManagementActivity::class.java)
                startActivity(intent)
            }
            manageUsersButton.setOnClickListener {
                val intent = Intent(this, UserManagementActivity::class.java)
                startActivity(intent)
            }

        } else { 
            // --- DELEGATE ---
            supportActionBar?.title = "Mis Jugadores"
            titleTextView.visibility = View.GONE
            manageCategoriesButton.visibility = View.GONE
            manageTeamsButton.visibility = View.GONE
            manageUsersButton.visibility = View.GONE

            managePlayersButton.text = "Gestionar Mis Jugadores"
            managePlayersButton.setOnClickListener {
                val intent = Intent(this, CategorySelectionActivity::class.java).apply {
                    putExtra("USER_ROLE", userRole)
                    putExtra("USER_ID", userId)
                }
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}