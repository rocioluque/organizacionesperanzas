package com.rocio.organizacionesperanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamManagementAdapter
    private val teams = mutableListOf<Team>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_management)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestionar Equipos"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView = findViewById(R.id.teams_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeamManagementAdapter(teams, this)
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.add_team_fab)
        fab.setOnClickListener {
            showAddOrEditTeamDialog(null)
        }

        loadTeams()
    }

    private fun loadTeams() {
        lifecycleScope.launch {
            val result = AppRepository.getAllTeams()
            teams.clear()
            teams.addAll(result)
            adapter.updateTeams(teams)
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
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditTeamDialog(team: Team?) {
        val isEditing = team != null
        val title = if (isEditing) "Editar Equipo" else "Añadir Equipo"

        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_team, null)
        val teamNameInput = dialogView.findViewById<EditText>(R.id.team_name_input)

        if (isEditing) {
            teamNameInput.setText(team!!.name)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(if (isEditing) "Guardar" else "Añadir") { _, _ ->
                val name = teamNameInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        val teamToSave = Team(id = team?.id ?: "", name = name, categories = emptyList())
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
            .setNegativeButton("Cancelar", null)
            .show()
    }
}