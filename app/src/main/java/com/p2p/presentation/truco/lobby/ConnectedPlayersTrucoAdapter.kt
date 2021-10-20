package com.p2p.presentation.truco.lobby

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.p2p.R
import java.util.*

class ConnectedPlayersTrucoAdapter(
    private val context: Context
) : BaseAdapter() {

    var players = mutableListOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var totalPlayers: Int = 2
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun sortedPlayers(): List<String> = if (totalPlayers == 4) {
        Collections.swap(players, 2, 3)
        players
    } else
        players

    override fun getItem(position: Int): Any {
        return players[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int = players.count().coerceAtMost(totalPlayers)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val connectedUserView: View =
            layoutInflater.inflate(R.layout.view_truco_connected_user, parent, false)
        val player: String = players[position]
        val playerName = connectedUserView.findViewById<View>(R.id.name) as TextView
        var itemBackground = 0

        playerName.text = player

        val imageView = connectedUserView.findViewById<ImageView>(R.id.avatar)
        imageView.setImageResource(R.drawable.ic_baseline_account_circle_white)

        when (position) {
            0 -> itemBackground = R.drawable.grid_view_item_hand_player
            1, 2 -> itemBackground = R.drawable.grid_view_item_second_team_player
            3 -> itemBackground = R.drawable.grid_view_item_first_team_player
        }

        connectedUserView.setBackgroundResource(itemBackground)

        connectedUserView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val item = ClipData.Item(player as? CharSequence)
                    val dragData = ClipData(
                        player as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item
                    )
                    var dottedBackground = 0
                    var imageAvatar = 0

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        view.startDragAndDrop(dragData, View.DragShadowBuilder(view), null, 0)
                    } else {
                        view.startDrag(dragData, View.DragShadowBuilder(view), null, 0)
                    }

                    when (position) {
                        0, 3 -> {
                            dottedBackground = R.drawable.dotted_border_yellow
                            imageAvatar = R.drawable.ic_baseline_account_circle_yellow
                        }
                        1, 2 -> {
                            dottedBackground = R.drawable.dotted_border_blue
                            imageAvatar = R.drawable.ic_baseline_account_circle_blue
                        }
                    }

                    view.background = ContextCompat.getDrawable(context, dottedBackground)
                    view.findViewById<ImageView>(R.id.avatar).setImageResource(imageAvatar)

                }
                MotionEvent.ACTION_UP -> view.performClick()
            }
            true
        }

        connectedUserView.setOnDragListener(DragListener())
        return connectedUserView
    }
}

