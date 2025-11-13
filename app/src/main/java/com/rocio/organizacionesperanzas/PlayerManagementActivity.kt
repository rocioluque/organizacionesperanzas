package com.rocio.organizacionesperanzas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerManagementActivity : AppCompatActivity() {

    private lateinit var playersRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PlayerAdapter

    private var categoryId: String? = null
    private var teamId: String? = null // For ORGANIZER role
    private var userRole: UserRole? = null

    private val allPlayers = mutableListOf<Player>()
    private var currentStatusFilter: PlayerStatus? = null
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

        supportActionBar?.title = if (teamName != null) "$teamName - $categoryName" else categoryName

        setupRecyclerView()

        val addPlayerFab = findViewById<FloatingActionButton>(R.id.add_player_fab)
        if (userRole == UserRole.ORGANIZER) {
            addPlayerFab.visibility = View.GONE
        } else {
            addPlayerFab.setOnClickListener {
                // Open Details screen in CREATE mode
                val intent = Intent(this, PlayerDetailsActivity::class.java).apply {
                    putExtra("USER_ROLE", userRole)
                    putExtra("CATEGORY_ID", categoryId)
                    putExtra("TEAM_ID", teamId)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        playersRecyclerView = findViewById(R.id.players_recycler_view)
        progressBar = findViewById(R.id.player_progress_bar)
        playersRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlayerAdapter(emptyList(), userRole)
        playersRecyclerView.adapter = adapter
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        currentStatusFilter = when (item.itemId) {
            R.id.action_filter_approved -> PlayerStatus.APPROVED
            R.id.action_filter_pending -> PlayerStatus.PENDING
            R.id.action_filter_rejected -> PlayerStatus.REJECTED
            else -> null // For R.id.action_filter_all
        }
        applyFilters()
        return super.onOptionsItemSelected(item)
    }

    private fun loadPlayers() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            categoryId?.let {
                allPlayers.clear()
                val playersFromCategory = AppRepository.getPlayers(it)
                // If a teamId is provided, filter players by team
                val playersToShow = if (teamId != null) {
                    playersFromCategory.filter { player -> player.teamId == teamId }
                } else {
                    playersFromCategory
                }
                allPlayers.addAll(playersToShow)
                applyFilters()
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun applyFilters() {
        var filteredList = if (currentSearchQuery.isEmpty()) {
            allPlayers
        } else {
            val query = currentSearchQuery.toLowerCase(Locale.getDefault())
            allPlayers.filter { it.fullName.toLowerCase(Locale.getDefault()).contains(query) }
        }

        if (currentStatusFilter != null) {
            filteredList = filteredList.filter { it.status == currentStatusFilter }
        }

        adapter.updatePlayers(filteredList)
    }
}