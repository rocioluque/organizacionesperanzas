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

        holder.editButton.setOnClickListener {
            listener.onEditTeam(team)
        }

        // The delete button is not in the new layout, so this is removed.
        // holder.deleteButton.setOnClickListener {
        //     listener.onDeleteTeam(team)
        // }
    }

    override fun getItemCount(): Int = teams.size

    fun updateTeams(newTeams: List<Team>) {
        this.teams = newTeams
        notifyDataSetChanged()
    }

    class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.team_name)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        // The delete button is not in the new layout
        // val deleteButton: ImageButton = itemView.findViewById(R.id.delete_team_button)
    }
}