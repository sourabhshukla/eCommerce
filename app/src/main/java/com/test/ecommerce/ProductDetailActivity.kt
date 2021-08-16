package com.test.ecommerce

import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.test.ecommerce.databinding.ActivityProductDetailBinding
import com.test.ecommerce.model.Products
import com.test.ecommerce.prevalent.currentOnlineUser
import java.text.SimpleDateFormat

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProductDetailBinding
    private lateinit var productId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productId= intent.getStringExtra("pid")!!
        
        getProductDetails(productId)

        binding.plus.setOnClickListener {
            var productNumbers=binding.productQuantity.text.toString().toInt()
            if (productNumbers==10){
                Toast.makeText(this,"Cannot Order More Than 10 Pieces",Toast.LENGTH_SHORT).show()
            }
            else{
                ++productNumbers
                binding.productQuantity.text = productNumbers.toString()
            }
        }

        binding.minus.setOnClickListener {
            var productNumbers=binding.productQuantity.text.toString().toInt()
            if (productNumbers==1){
                Toast.makeText(this,"Product Quantity can't be zero",Toast.LENGTH_SHORT).show()
            }
            else{
                --productNumbers
                binding.productQuantity.text = productNumbers.toString()
            }
        }

        binding.productAddToCart.setOnClickListener {
            addingToCartList()
        }
    }

    private fun addingToCartList() {
        val calender=java.util.Calendar.getInstance()

        val currentDate=SimpleDateFormat("dd mm yyyy")
        val saveCurrentDate=currentDate.format(calender.time)

        val currentTime=SimpleDateFormat("hh:mm:ss a")
        val saveCurrentTime=currentTime.format(calender.time)

        val cartListRef=Firebase.database.reference.child("cart list")

        val cartMap:HashMap<String, Any> =HashMap<String, Any>()
        cartMap["pid"]=productId
        cartMap["pname"]=binding.productNameDetails.text.toString()
        cartMap["price"]=binding.productPriceDetails.text.toString()
        cartMap["date"]=saveCurrentDate
        cartMap["time"]=saveCurrentTime
        cartMap["quantity"]=binding.productQuantity.text.toString()
        cartMap["discount"]=""

        cartListRef.child("User View").child(currentOnlineUser!!.phone!!)
            .child("Products").child(productId)
            .updateChildren(cartMap)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    cartListRef.child("Admin View").child(currentOnlineUser!!.phone!!)
                        .child("Produts").child(productId)
                        .updateChildren(cartMap)
                        .addOnCompleteListener {
                            Toast.makeText(this,"Added To Cart Successfully...",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,HomeActivity::class.java))
                        }
                }
            }
    }

    private fun getProductDetails(productId: String) {
        val usersRef=Firebase.database.reference.child("Products")
        usersRef.child(productId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val products=snapshot.getValue(Products::class.java)
                    binding.productNameDetails.text=products?.pname
                    binding.productPriceDetails.text=products?.price
                    binding.productDescriptionDetails.text=products?.description
                    Picasso.get().load(products!!.image).placeholder(R.drawable.profile).into(binding.productImageDetails)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}