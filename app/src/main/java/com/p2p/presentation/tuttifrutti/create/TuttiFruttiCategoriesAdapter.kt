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
            categoriesData = categories.mapIndexed { index, category ->
                CategoryData(category, isSelected = false, getBackgroundColour(index))
            }
            notifyDataSetChanged()
        }

    var categoriesData: List<CategoryData> = listOf()

    var selectedCategories = listOf<Category>()
        set(value) {
            field = value
            categoriesData.forEach {
                it.isSelected = selectedCategories.contains(it.category)
            }
            notifyDataSetChanged()
        }


    /** The current category on the list. */
    var selected: Pair<Category, Boolean>? = null
        private set(value) {
            if (field == value) return
            field = value
            val categoryData = categoriesData.find { it.category == value!!.first }!!
            categoryData.run {
                val lastValue = isSelected
                isSelected = !lastValue
            }
            onSelectedChanged.invoke(value!!.first, categoryData.isSelected)
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
        return holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    private fun getBackgroundColour(index: Int) =
        if (index.isEven()) R.color.colorBackgroundListFirst else R.color.colorBackgroundListSecond

    inner class ViewHolder(private val binding: ViewCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category) = with(binding) {
            val categoryData = categoriesData.find { it.category == category }!!
            item.text = category
            item.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    categoryData.backgroundColour
                )
            )
            item.setOnClickListener {
                selected = Pair(category, categoryData.isSelected)
            }
            item.isChecked = categoryData.isSelected
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


data class CategoryData(val category: Category, var isSelected: Boolean, val backgroundColour: Int)
