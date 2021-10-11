package com.p2p.presentation.truco.lobby

import android.content.ClipData
import android.view.DragEvent
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.p2p.R
import com.p2p.utils.Logger
import java.util.*

class DragListener : View.OnDragListener {
    override fun onDrag(view: View, event: DragEvent?): Boolean {
        when (event!!.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                view.elevation = ELEVATION_DRAG_START
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.elevation = ELEVATION_DRAG_ENTER
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION ->
                return true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.elevation = ELEVATION_DRAG_START
                return true
            }
            DragEvent.ACTION_DROP -> {
                val adapter = getViewAdapter(view)
                val droppedPlayer = getViewPlayerName(view)
                val players: List<String> = adapter.players

                val replacedItem: ClipData.Item = event.clipData.getItemAt(0)
                val replacedPlayer = replacedItem.text

                val replacedPlayerIndex: Int = getPlayerIndex(replacedPlayer, players)
                val droppedPlayerIndex: Int = getPlayerIndex(droppedPlayer, players)

                Collections.swap(players, replacedPlayerIndex, droppedPlayerIndex)
                adapter.notifyDataSetChanged()

                return true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                view.elevation = ELEVATION_DEFAULT

                // If the view is dropped successfully
                if(event.result) { return true }

                val adapter = getViewAdapter(view)
                val player = getViewPlayerName(view)
                val players: List<String> = adapter.players

                view.findViewById<ImageView>(R.id.avatar).setImageResource(R.drawable.ic_baseline_account_circle_white)

                when(getPlayerIndex(player, players)) {
                    0 -> view.background = ContextCompat.getDrawable(view.context, R.drawable.grid_view_item_hand_player)
                    1, 2 -> view.background = ContextCompat.getDrawable(view.context, R.drawable.grid_view_item_second_team_player)
                    3 -> view.background = ContextCompat.getDrawable(view.context, R.drawable.grid_view_item_first_team_player)
                }

                return true
            }
            else -> {
                Logger.e(TAG, "Unknown action type received by OnDragListener.")
                return false
            }
        }
    }

    private fun getViewAdapter(view: View): ConnectedPlayersTrucoAdapter {
        val gridView : GridView = view.parent as GridView
        return gridView.adapter as ConnectedPlayersTrucoAdapter
    }

    private fun getViewPlayerName(view: View) = view.findViewById<TextView>(R.id.name).text

    private fun getPlayerIndex(player: CharSequence, players: List<String>) = players.indexOf(player)

    companion object {
        const val TAG = "P2P_TRUCO_TEAMS_DRAG_AND_DROP"
        const val ELEVATION_DEFAULT = 1F
        const val ELEVATION_DRAG_START = 6F
        const val ELEVATION_DRAG_ENTER = 18F
    }
}
