package com.rocio.organizacionesperanzas

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TeamManagementActivity : AppCompatActivity(), TeamManagementAdapter.OnTeamActionClickListener {

    private val TAG = "TeamManagementActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamManagementAdapter
    private val teams = mutableListOf<Team>()
    private var userRole: UserRole? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_management)

        userRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_ROLE", UserRole::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_ROLE") as? UserRole
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestionar Equipos"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView = findViewById(R.id.teams_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeamManagementAdapter(teams, this)
        recyclerView.adapter = adapter

        setupFab()
        loadTeams()
    }

    private fun setupFab() {
        val fab: FloatingActionButton = findViewById(R.id.add_team_fab)
        if (userRole == UserRole.ORGANIZER) {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener { showAddOrEditTeamDialog(null) }
        } else {
            fab.visibility = View.GONE
        }
    }

    private fun loadTeams() {
        lifecycleScope.launch {
            try {
                val teamResult = AppRepository.getAllTeams()
                teams.clear()
                teams.addAll(teamResult)
                adapter.updateTeams(teams)
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar equipos desde la API", e)
                AlertDialog.Builder(this@TeamManagementActivity)
                    .setTitle("Error de Red")
                    .setMessage("No se pudieron cargar los equipos. Detalles: ${e.message}")
                    .setPositiveButton("Aceptar", null).show()
            }
        }
    }

    override fun onEditTeam(team: Team) {
        showAddOrEditTeamDialog(team)
    }

    override fun onDeleteTeam(team: Team) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el equipo '${team.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    val success = AppRepository.deleteTeam(team.id)
                    if (success) {
                        Toast.makeText(this@TeamManagementActivity, "Equipo eliminado", Toast.LENGTH_SHORT).show()
                        loadTeams()
                    } else {
                        Toast.makeText(this@TeamManagementActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun showAddOrEditTeamDialog(team: Team?) {
        val isEditing = team != null
        val title = if (isEditing) "Editar Equipo" else "Añadir Equipo"

        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_team, null)
        val teamNameInput = dialogView.findViewById<EditText>(R.id.team_name_input)
        val categoriesListView = dialogView.findViewById<ListView>(R.id.categories_list)

        lifecycleScope.launch {
            val allCategories = AppRepository.getAllCategories()
            val categoryNames = allCategories.map { it.name }
            val listAdapter = ArrayAdapter(this@TeamManagementActivity, android.R.layout.simple_list_item_multiple_choice, categoryNames)
            categoriesListView.adapter = listAdapter
            categoriesListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

            if (isEditing) {
                teamNameInput.setText(team!!.name)
                val teamCategoryIds = team.categories.map { it.id }.toSet()
                for (i in allCategories.indices) {
                    if (teamCategoryIds.contains(allCategories[i].id)) {
                        categoriesListView.setItemChecked(i, true)
                    }
                }
            }

            AlertDialog.Builder(this@TeamManagementActivity)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(if (isEditing) "Guardar" else "Añadir") { _, _ ->
                    val name = teamNameInput.text.toString().trim()
                    if (name.isNotEmpty()) {
                        val selectedPositions: SparseBooleanArray = categoriesListView.checkedItemPositions
                        val selectedCategories = mutableListOf<Category>()
                        for (i in 0 until allCategories.size) {
                            if (selectedPositions[i]) {
                                selectedCategories.add(allCategories[i])
                            }
                        }

                        val teamToSave = if (isEditing) {
                            team!!.copy(name = name, categories = selectedCategories)
                        } else {
                            Team(id = "", name = name, playerCount = 0, categories = selectedCategories)
                        }

                        lifecycleScope.launch {
                            val result = if (isEditing) {
                                AppRepository.updateTeam(team!!.id, teamToSave)
                            } else {
                                AppRepository.addTeam(teamToSave)
                            }

                            if (result != null) {
                                Toast.makeText(this@TeamManagementActivity, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                                loadTeams()
                            } else {
                                Toast.makeText(this@TeamManagementActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancelar", null).show()
        }
    }
}