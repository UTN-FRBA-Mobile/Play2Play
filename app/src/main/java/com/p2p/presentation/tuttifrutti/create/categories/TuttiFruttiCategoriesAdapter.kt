package com.p2p.presentation.tuttifrutti.create.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewCategoryItemBinding
import com.p2p.utils.isEven

/** The adapter used to show the list of categories. */
class TuttiFruttiCategoriesAdapter(
    private val onSelectedChanges: (Category) -> Unit
) : ListAdapter<Pair<Category, Boolean>, TuttiFruttiCategoriesAdapter.ViewHolder>(Differ()) {

    /** The list of categories displayed on the recycler. */
    var categories = listOf<Category>()
        set(value) {
            field = value
            submitList()
        }

    var selectedCategories: List<Category>? = null
        set(value) {
            field = value
            submitList()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ViewCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackground else R.color.wild_sand

    private fun submitList() = submitList(categories.map {
        it to (selectedCategories?.contains(it) ?: false)
    })

    inner class ViewHolder(private val binding: ViewCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Pair<Category, Boolean>, position: Int) = with(binding) {
            val color = ContextCompat.getColor(itemView.context, getBackgroundColor(position))
            categoryItem.setBackgroundColor(color)
            item.text = category.first
            item.isChecked = category.second
            item.setOnClickListener { onSelectedChanges.invoke(category.first) }
        }
    }

    private class Differ : DiffUtil.ItemCallback<Pair<Category, Boolean>>() {

        override fun areItemsTheSame(oldItem: Pair<Category, Boolean>, newItem: Pair<Category, Boolean>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Pair<Category, Boolean>, newItem: Pair<Category, Boolean>): Boolean {
            return oldItem == newItem
        }
    }
}


