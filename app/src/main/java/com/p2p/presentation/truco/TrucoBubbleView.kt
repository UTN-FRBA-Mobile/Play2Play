package com.p2p.presentation.truco

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.p2p.R
import com.p2p.databinding.ViewTrucoBubbleBinding

class TrucoBubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        val binding = ViewTrucoBubbleBinding.inflate(LayoutInflater.from(context), this)
        context.obtainStyledAttributes(attrs, R.styleable.TrucoBubbleView).use {
            it.getString(R.styleable.TrucoBubbleView_text)?.let { bubbleText ->
                binding.text.text = bubbleText
            }
        }
    }
}

