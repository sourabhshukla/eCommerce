package com.test.ecommerce.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.test.ecommerce.databinding.SellerItemViewBinding
import com.test.ecommerce.interfaces.ItemClickListener

class ItemViewHolder(val binding: SellerItemViewBinding) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var listener: ItemClickListener

    override fun onClick(v: View?) {
        if (v != null) {
            listener.onClick(v, adapterPosition, false)
        }
    }

    fun setOnClickListener(listener: ItemClickListener) {
        this.listener = listener
    }
}
