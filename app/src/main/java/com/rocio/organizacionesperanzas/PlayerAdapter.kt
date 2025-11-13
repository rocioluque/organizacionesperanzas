package com.rocio.organizacionesperanzas

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class PlayerAdapter(
    private var players: List<Player>,
    private val userRole: UserRole?,
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.playerName.text = player.fullName

        holder.playerPhoto.load(player.photoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
        }

        val indicatorColor = when (player.status) {
            PlayerStatus.APPROVED -> Color.GREEN
            PlayerStatus.REJECTED -> Color.RED
            PlayerStatus.PENDING -> Color.YELLOW
        }
        holder.statusIndicator.setColorFilter(indicatorColor)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PlayerDetailsActivity::class.java).apply {
                putExtra("PLAYER_ID", player.id)
                putExtra("USER_ROLE", userRole)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlayers(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged() // In a real app, DiffUtil would be more efficient
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.player_name)
        val playerPhoto: ImageView = itemView.findViewById(R.id.player_photo_item)
        val statusIndicator: ImageView = itemView.findViewById(R.id.status_indicator)
    }
}