package com.rocio.organizacionesperanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// CORREGIDO: El adaptador ahora es mucho más simple
class TeamManagementAdapter(
    private var teams: List<Team>,
    private val listener: OnTeamActionClickListener
) : RecyclerView.Adapter<TeamManagementAdapter.TeamViewHolder>() {

    interface OnTeamActionClickListener {
        fun onEditTeam(team: Team)
        fun onDeleteTeam(team: Team)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.team_management_list_item, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]
        holder.bind(team)
    }

    override fun getItemCount(): Int = teams.size

    // CORREGIDO: El método de actualización ahora solo necesita los equipos
    fun updateTeams(newTeams: List<Team>) {
        this.teams = newTeams
        notifyDataSetChanged()
    }

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teamName: TextView = itemView.findViewById(R.id.team_name)
        private val teamCategories: TextView = itemView.findViewById(R.id.team_categories)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(team: Team) {
            teamName.text = team.name

            // CORREGIDO: La lógica ahora es mucho más simple
            val categoryNames = team.categories.map { it.name }
            teamCategories.text = if (categoryNames.isNotEmpty()) categoryNames.joinToString(", ") else "Sin categoría"

            editButton.setOnClickListener { listener.onEditTeam(team) }
            deleteButton.setOnClickListener { listener.onDeleteTeam(team) }
        }
    }
}