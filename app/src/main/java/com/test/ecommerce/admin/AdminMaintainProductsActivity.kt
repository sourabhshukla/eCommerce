package com.test.ecommerce.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.test.ecommerce.R
import com.test.ecommerce.databinding.ActivityAdminMaintainProductsBinding
import com.test.ecommerce.prevalent.currentOnlineUser
import com.test.ecommerce.sellers.SellerProductCategoryActivity

class AdminMaintainProductsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminMaintainProductsBinding
    private lateinit var productId:String
    private lateinit var productsRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminMaintainProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productId=intent.getStringExtra("pid").toString()
        productsRef=Firebase.database.reference.child("Products").child(productId)
        Picasso.get().load(currentOnlineUser!!.image).placeholder(R.drawable.profile).into(binding.productSellerImage)

        displaySpecificProductInfo()

        binding.maintainApplyChangesBtn.setOnClickListener { applyChanges() }
        binding.deleteProductBtn.setOnClickListener { deleteThisProduct() }
    }

    private fun deleteThisProduct() {
        productsRef.removeValue().addOnCompleteListener {
            startActivity(Intent(this, SellerProductCategoryActivity::class.java))
            finish()
            Toast.makeText(this,"Product Removed Successfully",Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyChanges() {
        if (TextUtils.isEmpty(binding.productSellerName.toString())){
            Toast.makeText(this,"Name cant be empty",Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(binding.productSellerDescription.toString())){
            Toast.makeText(this,"Description cant be empty",Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(binding.productSellerPrice.toString())){
            Toast.makeText(this,"Price cant be empty",Toast.LENGTH_SHORT).show()
        }
        else{
            val productMap:HashMap<String,Any> =HashMap<String,Any>()
            productMap["pid"] = productId
            productMap["description"] = binding.productSellerDescription.text.toString()
            productMap["price"] = binding.productSellerPrice.text.toString()
            productMap["pname"] = binding.productSellerName.text.toString()
            productsRef.updateChildren(productMap)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Changes Applied Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SellerProductCategoryActivity::class.java))
                        finish()
                    }
                }
        }
    }

    private fun displaySpecificProductInfo() {
        productsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    binding.productSellerName.setText(snapshot.child("pname").value.toString())
                    binding.productSellerDescription.setText(snapshot.child("description").value.toString())
                    binding.productSellerPrice.setText(snapshot.child("price").value.toString())
                    Picasso.get().load(snapshot.child("image").value.toString()).placeholder(R.drawable.profile).into(binding.productSellerImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}