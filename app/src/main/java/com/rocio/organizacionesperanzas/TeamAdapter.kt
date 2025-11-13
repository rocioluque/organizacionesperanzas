package com.rocio.organizacionesperanzas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamAdapter(
    private val teams: List<Team>,
    private val categoryId: String,
    private val userRole: UserRole?
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.team_list_item, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]
        holder.teamName.text = team.name

        holder.teamItemLayout.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PlayerManagementActivity::class.java).apply {
                putExtra("CATEGORY_ID", categoryId)
                putExtra("TEAM_ID", team.id)
                putExtra("TEAM_NAME", team.name)
                putExtra("USER_ROLE", userRole)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = teams.size

    class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.team_name)
        val teamItemLayout: LinearLayout = itemView.findViewById(R.id.team_item_layout)
    }
}