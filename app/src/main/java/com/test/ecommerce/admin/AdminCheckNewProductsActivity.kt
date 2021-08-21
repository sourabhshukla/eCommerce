package com.test.ecommerce.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.test.ecommerce.R
import com.test.ecommerce.databinding.ActivityAdminCheckNewProductsBinding
import com.test.ecommerce.databinding.ProductItemLayoutBinding
import com.test.ecommerce.model.Products
import com.test.ecommerce.viewHolder.ProductViewHolder

class AdminCheckNewProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminCheckNewProductsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var unverifiedProductsRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCheckNewProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView=binding.adminProductsChecklist
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(this)
        unverifiedProductsRef=Firebase.database.reference.child("Products")
    }

    override fun onStart() {
        super.onStart()

        val options=FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(unverifiedProductsRef.orderByChild("productState").equalTo("Not Approved"),Products::class.java)
            .build()

        val adapter=object :FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                val binding=ProductItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return ProductViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: ProductViewHolder,
                position: Int,
                model: Products
            ) {
                holder.binding
                holder.binding.productName.text=model.pname
                holder.binding.productDescription.text=model.description
                holder.binding.productPrice.text=model.price
                Picasso.get().load(model.image).into(holder.binding.productImage)
                holder.itemView.setOnClickListener {
                    val pid=model.pid
                    val options= arrayOf<CharSequence>("Yes","No")
                    val builder=AlertDialog.Builder(it.context)
                    builder.apply {
                        setTitle("Do You Want To Approve This Product. Are You Sure?")
                        setItems(options) { _, which ->
                            if (which==0){ changeProductState(pid) }
                            if (which==1){  }
                        }
                        show()
                    }
                }
            }
        }
        recyclerView.adapter=adapter
        adapter.startListening()
    }

    private fun changeProductState(pid: String?) {
        unverifiedProductsRef.child(pid!!).child("productState").setValue("Approved")
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"The Item has been approved, It's now available for sale",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Item Approval Failed",Toast.LENGTH_SHORT).show()
                }
            }
    }
}