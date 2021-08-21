package com.test.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.test.ecommerce.databinding.ActivitySearchProductsBinding
import com.test.ecommerce.databinding.ProductItemLayoutBinding
import com.test.ecommerce.model.Products
import com.test.ecommerce.viewHolder.ProductViewHolder

class SearchProductsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchProductsBinding
    //private lateinit var searchInput:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchList.layoutManager=LinearLayoutManager(this)
        binding.searchList.setHasFixedSize(true)
        binding.searchBtn.setOnClickListener {
            //searchInput=binding.searchProductName.text.toString()
            onStart()
        }
    }

    override fun onStart() {
        super.onStart()
        val productRef=Firebase.database.reference.child("Products")
        val options=FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(productRef.orderByChild("pname").startAt(binding.searchProductName.text.toString()),Products::class.java)
            .build()
        val adapter=object: FirebaseRecyclerAdapter<Products,ProductViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                val binding=ProductItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return ProductViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: ProductViewHolder,
                position: Int,
                model: Products
            ) {
                holder.binding.productName.text=model.pname
                holder.binding.productDescription.text=model.description
                holder.binding.productPrice.text=model.price
                Picasso.get().load(model.image).into(holder.binding.productImage)
                holder.itemView.setOnClickListener {
                    startActivity(Intent(it.context,ProductDetailActivity::class.java).putExtra("pid",model.pid))
                }
            }
        }
        binding.searchList.adapter=adapter
        adapter.startListening()
    }
}