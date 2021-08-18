package com.test.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityAdminUserProductsBinding
import com.test.ecommerce.databinding.CartItemsLayoutBinding
import com.test.ecommerce.model.Cart
import com.test.ecommerce.viewHolder.CartViewHolder

class AdminUserProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUserProductsBinding
    private lateinit var cartListRef: DatabaseReference
    private lateinit var userId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminUserProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId=intent.getStringExtra("uid")!!
        binding.productsList.layoutManager=LinearLayoutManager(this)
        binding.productsList.setHasFixedSize(true)
        cartListRef= Firebase.database.reference.child("cart list").child("Admin View").child(userId).child("Products")
    }

    override fun onStart() {
        super.onStart()

        val options=FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef,Cart::class.java)
            .build()

        val adapter=object: FirebaseRecyclerAdapter<Cart, CartViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
                val binding=CartItemsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return CartViewHolder(binding)
            }

            override fun onBindViewHolder(holder: CartViewHolder, position: Int, model: Cart) {
                holder.binding.cartProductName.text=model.pname
                holder.binding.cartProductPrice.text="Price= "+model.price
                holder.binding.cartProductQuantity.text="Quantity= "+model.quantity
            }
        }
        binding.productsList.adapter=adapter
        adapter.startListening()
    }
}