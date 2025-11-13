package com.rocio.organizacionesperanzas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerManagementActivity : AppCompatActivity(), PlayerAdapter.OnPlayerActionClickListener {

    private lateinit var playersRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PlayerAdapter

    private var categoryId: String? = null
    private var teamId: String? = null
    private var userRole: UserRole? = null

    private val allPlayers = mutableListOf<Player>()
    private var currentSearchQuery: String = ""

    override fun onResume() {
        super.onResume()
        loadPlayers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_management)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        categoryId = intent.getStringExtra("CATEGORY_ID")
        val categoryName = intent.getStringExtra("CATEGORY_NAME")
        teamId = intent.getStringExtra("TEAM_ID")
        val teamName = intent.getStringExtra("TEAM_NAME")

        userRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_ROLE", UserRole::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_ROLE") as? UserRole
        }

        supportActionBar?.title = if (teamName != null) teamName else categoryName

        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        playersRecyclerView = findViewById(R.id.players_recycler_view)
        progressBar = findViewById(R.id.player_progress_bar)
        playersRecyclerView.layoutManager = LinearLayoutManager(this)
        // The adapter is now simpler and doesn't need the maps
        adapter = PlayerAdapter(emptyList(), userRole, this)
        playersRecyclerView.adapter = adapter
    }

    private fun setupFab() {
        val addPlayerFab = findViewById<FloatingActionButton>(R.id.add_player_fab)
        if (userRole == UserRole.ORGANIZER) {
            addPlayerFab.visibility = View.GONE
        } else {
            addPlayerFab.setOnClickListener {
                val intent = Intent(this, PlayerDetailsActivity::class.java).apply {
                    putExtra("USER_ROLE", userRole)
                    putExtra("CATEGORY_ID", categoryId)
                    putExtra("TEAM_ID", teamId)
                }
                startActivity(intent)
            }
        }
    }

    private fun loadPlayers() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE

            val playersFromCategory = categoryId?.let { AppRepository.getPlayers(it) } ?: emptyList()

            val playersToShow = if (teamId != null) {
                playersFromCategory.filter { player -> player.teamId == teamId }
            } else {
                playersFromCategory
            }

            allPlayers.clear()
            allPlayers.addAll(playersToShow)
            applyFilters()
            progressBar.visibility = View.GONE
        }
    }

    private fun applyFilters() {
        val filteredList = if (currentSearchQuery.isEmpty()) {
            allPlayers
        } else {
            val query = currentSearchQuery.toLowerCase(Locale.getDefault())
            allPlayers.filter { it.fullName.toLowerCase(Locale.getDefault()).contains(query) }
        }
        adapter.updatePlayers(filteredList)
    }

    override fun onEditPlayer(player: Player) {
        val intent = Intent(this, PlayerDetailsActivity::class.java).apply {
            putExtra("PLAYER_ID", player.id)
            putExtra("USER_ROLE", userRole)
        }
        startActivity(intent)
    }

    override fun onDeletePlayer(player: Player) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar a ${player.fullName}?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    AppRepository.deletePlayer(player.id)
                    loadPlayers() // Reload players after deletion
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.player_management_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText.orEmpty()
                applyFilters()
                return true
            }
        })

        return true
    }
}