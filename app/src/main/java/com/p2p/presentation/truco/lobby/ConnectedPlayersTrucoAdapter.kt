package com.p2p.presentation.truco.lobby

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.p2p.databinding.ViewConnectedUserBinding

class ConnectedPlayersTrucoAdapter(
    private val context: Context,
    private val players: Array<String>
) :BaseAdapter() {

    // players notifyDataSetChanged()


    // delete?
    inner class ViewHolder(private val binding: ViewConnectedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(connectedPlayer: String) = with(binding) {
            name.text = connectedPlayer
        }
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
        TODO("Not yet implemented")
    }
}
