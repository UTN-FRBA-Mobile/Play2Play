package com.p2p.presentation.tuttifrutti.finalscore

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.presentation.tuttifrutti.create.categories.TuttiFruttiCategoriesAdapter
import com.p2p.utils.isEven

class TuttiFruttiFinalScoreAdapter :
    RecyclerView.Adapter<TuttiFruttiFinalScoreAdapter.ViewHolder>() {

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackground else R.color.wild_sand

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TuttiFruttiFinalScoreAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TuttiFruttiFinalScoreAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}