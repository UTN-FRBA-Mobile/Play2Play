package com.p2p.presentation.truco.lobby

import com.p2p.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.p2p.model.tuttifrutti.FinishedRoundInfo

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
        val root: View = layoutInflater.inflate(R.layout.view_truco_connected_user, parent, false)
        val player: String = players[position]
        val playerName = root.findViewById<View>(R.id.name) as TextView

        playerName.text = player

        if (position == 0) {
            val imageView = root.findViewById<ImageView>(R.id.avatar)
            imageView.setImageResource(R.drawable.ic_baseline_account_circle_white)
            root.setBackgroundResource(R.drawable.grid_view_item_hand_player)
        };

        return root
    }
}
