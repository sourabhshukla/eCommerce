package com.test.ecommerce.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.test.ecommerce.databinding.CartItemsLayoutBinding
import com.test.ecommerce.interfaces.ItemClickListener

class CartViewHolder(val binding: CartItemsLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private lateinit var itemClickListener: ItemClickListener

    override fun onClick(v: View?) {
        itemClickListener.onClick(v!!, adapterPosition,false)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener=itemClickListener
    }
}