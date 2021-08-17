package com.test.ecommerce

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityCartBinding
import com.test.ecommerce.databinding.CartItemsLayoutBinding
import com.test.ecommerce.model.Cart
import com.test.ecommerce.prevalent.currentOnlineUser
import com.test.ecommerce.viewHolder.CartViewHolder

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var context: Context
    private var totalPrice:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this
        binding.cartList.setHasFixedSize(true)
        binding.cartList.layoutManager=LinearLayoutManager(this)
        binding.nextProcessBtn.setOnClickListener {
            startActivity(Intent(this,ConfirmFileOrderActivity::class.java).putExtra("Total Price",totalPrice.toString()))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        checkOrderState()
        val cartListRef=Firebase.database.reference.child("cart list")

        val options=FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef.child("User View").child(currentOnlineUser!!.phone!!).child("Products"),Cart::class.java)
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
                val singleProductPrice:Int=model.price!!.toInt()*model.quantity!!.toInt()
                totalPrice+=singleProductPrice
                binding.totalPrice.text="Total Price = ${totalPrice.toString()}"

                holder.itemView.setOnClickListener {
                    val options= arrayOf<CharSequence>("Edit","Remove")
                    val builder=AlertDialog.Builder(context)
                        .setTitle("Cart Options")
                        .setItems(options) { dialog, which ->
                            if (which==0){
                                startActivity(Intent(context,ProductDetailActivity::class.java).putExtra("pid",model.pid))
                            }
                            if (which==1){
                                cartListRef.child("User View").child(currentOnlineUser!!.phone!!).child("Products")
                                    .child(model.pid!!)
                                    .removeValue()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful){
                                            Toast.makeText(context,"Items removed successfully...",Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(context,HomeActivity::class.java))
                                        }
                                    }
                            }
                        }
                        .show()
                }
            }
        }


        binding.cartList.adapter=adapter
        adapter.startListening()
    }

    private fun checkOrderState(){
        val ordersRef=Firebase.database.reference.child("Orders").child(currentOnlineUser!!.phone!!)

        ordersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val shippingState=snapshot.child("state").value.toString()
                    val userName=snapshot.child("name").value.toString()

                    if(shippingState=="shipped"){
                        binding.totalPrice.text="Dear ${userName} order has been shipped successfully."
                        binding.cartList.visibility=View.GONE
                        binding.msg.text="Congratulations your order has been shipped successfully."
                        binding.msg.visibility=View.VISIBLE
                        binding.nextProcessBtn.visibility=View.GONE

                        Toast.makeText(context,"You can purchase more products once your first order arrives",Toast.LENGTH_SHORT).show()
                    }
                    else if (shippingState=="not shipped"){
                        binding.totalPrice.text="Shipping State = Not Shipped"
                        binding.cartList.visibility=View.GONE
                        binding.msg.visibility=View.VISIBLE
                        binding.nextProcessBtn.visibility=View.GONE

                        Toast.makeText(context,"You can purchase more products once your first order arrives",Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}