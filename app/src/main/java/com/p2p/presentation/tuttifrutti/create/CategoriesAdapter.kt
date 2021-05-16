package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.p2p.databinding.ViewCategoryItemBinding

/** The adapter used to show the list of categories. */
class CategoriesAdapter(private val onSelectedChanged: (Category?) -> Unit) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    /** The list of games displayed on the recycler. */
    var categories = listOf<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** The current category on the list. */
    var selected: Category? = null
        private set(value) {
            if (field == value) return
            field = value
            onSelectedChanged.invoke(value)
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(categories[position])

    override fun getItemCount() = categories.size

    inner class ViewHolder(private val binding: ViewCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category) = with(binding) {
            categoryItem.text = category.name
            container.setOnClickListener { selected = category }
            container
                .animate()
                .alpha(
                    when (selected) {
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
