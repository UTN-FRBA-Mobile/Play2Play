package com.p2p.presentation.truco.lobby

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.p2p.R
import com.p2p.utils.Logger
import java.util.*


class DragListener : View.OnDragListener {
    override fun onDrag(view: View?, event: DragEvent?): Boolean {
        when (event!!.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                view!!.elevation = ELEVATION_DRAG_START
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view!!.elevation = ELEVATION_DRAG_ENTER
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION ->
                return true
            DragEvent.ACTION_DRAG_EXITED -> {
                view!!.elevation = ELEVATION_DRAG_START
                return true
            }
            DragEvent.ACTION_DROP -> {
                val droppedPlayer = view!!.findViewById<TextView>(R.id.name).text

                val gridView : GridView = view.parent as GridView
                val adapter: ConnectedPlayersTrucoAdapter = gridView.adapter as ConnectedPlayersTrucoAdapter
                val players: List<String> = adapter.players

                val replacedItem: ClipData.Item = event.clipData.getItemAt(0)
                val raplacedPlayer = replacedItem.text

                val replacedPlayerIndex: Int = players.indexOf(raplacedPlayer)
                val droppedPlayerIndex: Int = players.indexOf(droppedPlayer)

                Collections.swap(players, replacedPlayerIndex, droppedPlayerIndex)
                adapter.notifyDataSetChanged()

                // Invalidate the view to force a redraw
                view!!.invalidate()

                return true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                view!!.elevation = ELEVATION_DEFAULT
                return true
            }
            else -> {
                Logger.e(TAG, "Unknown action type received by OnDragListener.")
                return false
            }
        }
    }

    companion object {
        const val TAG = "P2P_TRUCO_TEAMS_DRAG_AND_DROP"
        const val ELEVATION_DEFAULT = 1F
        const val ELEVATION_DRAG_START = 6F
        const val ELEVATION_DRAG_ENTER = 18F
    }
}