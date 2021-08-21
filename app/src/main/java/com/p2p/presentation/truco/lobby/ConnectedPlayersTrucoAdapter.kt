package com.p2p.presentation.truco.lobby

import com.p2p.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ConnectedPlayersTrucoAdapter(
    private val context: Context
) :BaseAdapter() {

    var players = listOf<String>()
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
        return players.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val root: View = layoutInflater.inflate(R.layout.view_truco_connected_user, parent, false)
        val player: String = players[position]
        val playerName = root.findViewById<View>(R.id.name) as TextView

        playerName.text = player

        return root
    }
}
