package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewCategoryItemBinding
import com.p2p.utils.isEven


/** The adapter used to show the list of categories. */
class TuttiFruttiCategoriesAdapter(private val onSelectedChanges: (Category) -> Unit) :
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

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackgroundListFirst else R.color.colorBackgroundListSecond

    inner class ViewHolder(private val binding: ViewCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category, position: Int) = with(binding) {
            item.text = category
            item.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(position)
                )
            )
            item.setOnClickListener {
                onSelectedChanges.invoke(category)
            }
            item.isChecked = selectedCategories?.contains(category) ?: false
            categoryItem
                .animate()
                .alpha(
                    when (category) {
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


