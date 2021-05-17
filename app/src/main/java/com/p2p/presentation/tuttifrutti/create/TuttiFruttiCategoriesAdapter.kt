package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewCategoryItemBinding
import com.p2p.utils.isEven


/** The adapter used to show the list of categories. */
class TuttiFruttiCategoriesAdapter(private val onSelectedChanged: (Category, Boolean) -> Unit) :
    RecyclerView.Adapter<TuttiFruttiCategoriesAdapter.ViewHolder>() {

    /** The list of categories displayed on the recycler. */
    var categories = listOf<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    var selectedCategories: List<Category>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    /** The current category on the list. */
    var selected: Pair<Category, Boolean>? = null
        private set(value) {
            if (field == value) return
            field = value
            onSelectedChanged.invoke(value!!.first, value!!.second)
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size

    private fun getBackgroundColour(index: Int) =
        if (index.isEven()) R.color.colorBackgroundListFirst else R.color.colorBackgroundListSecond

    inner class ViewHolder(private val binding: ViewCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category, position: Int) = with(binding) {
            val isSelected = selectedCategories?.contains(category) ?: false
            item.text = category
            item.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColour(position)
                )
            )
            item.setOnClickListener {
                selected = Pair(category, !isSelected)
            }
            item.isChecked = isSelected
            categoryItem
                .animate()
                .alpha(
                    when (selected?.first) {
                        category -> SELECTED_OPACITY
                        null -> NONE_SELECTED_OPACITY
                        else -> NO_SELECTED_OPACITY
                    }
                )
                .start()
        }
    }

    companion object {

        private const val SELECTED_OPACITY = 1f
        private const val NO_SELECTED_OPACITY = 0.5f
        private const val NONE_SELECTED_OPACITY = 0.8f
    }
}


