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

class ConnectedPlayersTrucoAdapter(
    private val context: Context
) :BaseAdapter() {

    var players = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var totalPlayers : Int = 4
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Any {
        return players[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if (players.count() >= totalPlayers) {
            return totalPlayers
        } else {
            return players.count()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val connectedUserView: View = layoutInflater.inflate(R.layout.view_truco_connected_user, parent, false)
        val player: String = players[position]
        val playerName = connectedUserView.findViewById<View>(R.id.name) as TextView

        playerName.text = player

        if (position == 0) {
            val imageView = connectedUserView.findViewById<ImageView>(R.id.avatar)
            imageView.setImageResource(R.drawable.ic_baseline_account_circle_white)
            connectedUserView.setBackgroundResource(R.drawable.grid_view_item_hand_player)
        }


        // We set the OnTouchListener here because it needs the variables inside this method
        connectedUserView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val item = ClipData.Item(player as? CharSequence)
                    val dragShadow = DragShadowBuilder(view!!)
                    val dragData = ClipData(
                        player as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item
                    )

                    if (position != 0) {
                        view.background = ContextCompat.getDrawable(context, R.drawable.dotted_border)
                    } else {
                        view.background = ContextCompat.getDrawable(context, R.drawable.dotted_border)
                        view.findViewById<ImageView>(R.id.avatar)
                            .setImageResource(R.drawable.ic_baseline_account_circle_yellow)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        view.startDragAndDrop(dragData, dragShadow, null, 0)
                    } else {
                        view.startDrag(dragData, dragShadow, null, 0)
                    }
                }
                MotionEvent.ACTION_UP -> view.performClick()
            }
            true
        }

        connectedUserView.setOnDragListener(DragListener())
        return connectedUserView
    }
}
