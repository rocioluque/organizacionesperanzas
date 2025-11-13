package com.rocio.organizacionesperanzas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy

class PlayerAdapter(
    private var players: List<Player>,
    private val userRole: UserRole?,
    private val listener: OnPlayerActionClickListener
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    interface OnPlayerActionClickListener {
        fun onEditPlayer(player: Player)
        fun onDeletePlayer(player: Player)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]

        holder.playerName.text = player.fullName
        holder.playerBirthDate.text = player.birthDate ?: "Fecha no disponible"

        holder.playerPhoto.load(player.photoUrl) {
            crossfade(true)
            error(R.drawable.photojug_placeholder_background)
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
            listener(
                onStart = { Log.d("CoilDebug", "Starting to load image: ${player.photoUrl}") },
                onSuccess = { _, result -> Log.d("CoilDebug", "Success loading image from ${result.dataSource}") },
                onError = { _, result -> Log.e("CoilDebug", "Error loading image: ${result.throwable}") }
            )
        }

        when (player.status) {
            PlayerStatus.APPROVED -> {
                holder.statusIndicator.setImageResource(R.drawable.ic_check_circle)
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.oe_success))
            }
            PlayerStatus.PENDING -> {
                holder.statusIndicator.setImageResource(R.drawable.ic_status_pending)
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.oe_warning))
            }
            PlayerStatus.REJECTED -> {
                holder.statusIndicator.setImageResource(R.drawable.ic_cancel)
                holder.statusIndicator.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.oe_error))
            }
        }

        holder.playerItemLayout.setOnClickListener { listener.onEditPlayer(player) }

        holder.actionButton.visibility = View.VISIBLE
        holder.actionButton.setOnClickListener { view ->
            showPopupMenu(view, player)
        }
    }

    private fun showPopupMenu(view: View, player: Player) {
        val popup = PopupMenu(view.context, view)
        
        if (userRole == UserRole.ORGANIZER) {
            popup.menuInflater.inflate(R.menu.player_item_menu_admin, popup.menu)
        } else {
            popup.menuInflater.inflate(R.menu.player_item_menu, popup.menu)
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit_player, R.id.action_change_status -> {
                    listener.onEditPlayer(player)
                    true
                }
                R.id.action_delete_player -> {
                    listener.onDeletePlayer(player)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getItemCount(): Int = players.size

    fun updatePlayers(newPlayers: List<Player>) {
        this.players = newPlayers
        notifyDataSetChanged()
    }

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerPhoto: ImageView = itemView.findViewById(R.id.player_photo_item)
        val playerName: TextView = itemView.findViewById(R.id.player_name)
        val playerBirthDate: TextView = itemView.findViewById(R.id.player_birth_date)
        val statusIndicator: ImageView = itemView.findViewById(R.id.status_indicator)
        val actionButton: ImageButton = itemView.findViewById(R.id.action_button)
        val playerItemLayout: LinearLayout = itemView.findViewById(R.id.player_item_layout)
    }
}