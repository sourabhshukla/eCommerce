package com.test.ecommerce.sellers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.test.ecommerce.MainActivity
import com.test.ecommerce.R
import com.test.ecommerce.databinding.ActivitySellerHomeBinding
import com.test.ecommerce.databinding.ProductItemLayoutBinding
import com.test.ecommerce.databinding.SellerItemViewBinding
import com.test.ecommerce.model.Products
import com.test.ecommerce.viewHolder.ItemViewHolder
import com.test.ecommerce.viewHolder.ProductViewHolder

class SellerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerHomeBinding
    private lateinit var unverifiedProductsRef:DatabaseReference
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView=binding.sellerHomeRecyclerview
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        unverifiedProductsRef=Firebase.database.reference.child("Products")

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_seller_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_logout
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home -> startActivity(Intent(this, SellerHomeActivity::class.java))
                R.id.navigation_add -> {
                    startActivity(Intent(this, SellerProductCategoryActivity::class.java))
                }
                R.id.navigation_logout -> {
                    Firebase.auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK+Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                }
                else -> Toast.makeText(this,"else",Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()

        val options= FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(Firebase.auth.currentUser?.uid),
                Products::class.java)
            .build()

        val adapter=object : FirebaseRecyclerAdapter<Products, ItemViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val binding= SellerItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return ItemViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: ItemViewHolder,
                position: Int,
                model: Products
            ) {
                holder.binding.productSellerName.text=model.pname
                holder.binding.productSellerDescription.text=model.description
                holder.binding.productSellerPrice.text=model.price
                holder.binding.productSellerState.text=model.productState
                Picasso.get().load(model.image).into(holder.binding.productSellerImage)
                holder.itemView.setOnClickListener {
                    val pid=model.pid
                    val options= arrayOf<CharSequence>("Yes","No")
                    val builder= AlertDialog.Builder(it.context)
                    builder.apply {
                        setTitle("Do You Want To Delete This Product. Are You Sure?")
                        setItems(options) { _, which ->
                            if (which==0){ deleteProduct(pid) }
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

    private fun deleteProduct(pid: String?) {
        unverifiedProductsRef.child(pid!!).removeValue()
            .addOnCompleteListener {
                Toast.makeText(this,"Item has been deleted successfully",Toast.LENGTH_SHORT).show()
            }
    }

}

