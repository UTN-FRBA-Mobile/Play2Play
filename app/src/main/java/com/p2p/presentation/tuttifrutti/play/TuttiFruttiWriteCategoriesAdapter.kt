package com.p2p.presentation.tuttifrutti.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.p2p.databinding.ViewWriteCategoryItemBinding
import com.p2p.presentation.tuttifrutti.create.categories.Category


/** The adapter used to show the list of selected categories and be written. */
class TuttiFruttiWriteCategoriesAdapter(private val categories: List<Category>, val onFocusOut: (Category, String?) -> Unit) :
    RecyclerView.Adapter<TuttiFruttiWriteCategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewWriteCategoryItemBinding.inflate(
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

    inner class ViewHolder(private val binding: ViewWriteCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category) = with(binding) {
            textField.setOnFocusChangeListener{ _, focus ->
                if(!focus){
                    onFocusOut(category, textField.text.toString())
                }
            }
            input.hint = category
        }
    }

}

