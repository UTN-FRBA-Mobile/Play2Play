package ar.com.play2play.presentation.tuttifrutti.create.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ar.com.play2play.databinding.ViewSelectedCategoryItemBinding


/** The adapter used to show the list of selected categories. */
class TuttiFruttiSelectedCategoriesAdapter(private val onDeleteCategory: (Category) -> Unit) :
    RecyclerView.Adapter<TuttiFruttiSelectedCategoriesAdapter.ViewHolder>() {

    /** The list of the selected categories */
    var selectedCategories = listOf<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewSelectedCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(selectedCategories[position])
    }

    override fun getItemCount() = selectedCategories.size

    inner class ViewHolder(private val binding: ViewSelectedCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category) = with(binding) {
            categoryToMaybeDelete.text = category
            categoryToMaybeDelete.setOnClickListener {
                onDeleteCategory.invoke(category)
            }
        }
    }

}


