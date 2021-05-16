package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewCategoryItemBinding
import com.p2p.databinding.ViewSelectedCategoryItemBinding
import com.p2p.utils.isEven


/** The adapter used to show the list of categories. */
class SelectedCategoriesAdapter(private val onDeleteCategory: (Category) -> Unit) : RecyclerView.Adapter<SelectedCategoriesAdapter.ViewHolder>() {

    /** The list of games displayed on the recycler. */
    var selectedCategories = listOf<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    /** The current category on the list. */
    var selected: Category? = null
        private set(value) {
            if (field == value) return
            field = value
            val filteredCategories = selectedCategories.filter { it != value!! }
            selectedCategories = filteredCategories
            onDeleteCategory.invoke(value!!)
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewSelectedCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(selectedCategories[position])
    }

    override fun getItemCount() = selectedCategories.size

    inner class ViewHolder(private val binding: ViewSelectedCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category) = with(binding) {
            selectedCategoryName.text = category.name
            item.setOnClickListener {
                selected = category
            }
        }
    }

}


