package com.rocio.organizacionesperanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        holder.teamName.text = team.name

        // Set the categories and player count
        holder.teamCategory.text = team.categories.joinToString(", ") { it.name }
        // The player count is not directly available in the Team object.
        // This would require a more complex data loading strategy to be accurate.
        // For now, we'll just show a placeholder.
        holder.teamPlayersCount.text = "? jugadores"

        holder.editButton.setOnClickListener {
            listener.onEditTeam(team)
        }
    }

    override fun getItemCount(): Int = teams.size

    fun updateTeams(newTeams: List<Team>) {
        this.teams = newTeams
        notifyDataSetChanged()
    }

    class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.team_name)
        val teamCategory: TextView = itemView.findViewById(R.id.team_category)
        val teamPlayersCount: TextView = itemView.findViewById(R.id.team_players_count)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
    }
}