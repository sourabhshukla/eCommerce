package com.test.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityAdminNewOrdersBinding
import com.test.ecommerce.databinding.OrdersLayoutBinding
import com.test.ecommerce.model.AdminOrders
import com.test.ecommerce.viewHolder.AdminOrdersViewHolder

class AdminNewOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminNewOrdersBinding
    private lateinit var ordersRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNewOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ordersRef=Firebase.database.reference.child("Orders")
        binding.ordersList.layoutManager=LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        val options=FirebaseRecyclerOptions.Builder<AdminOrders>()
            .setQuery(ordersRef,AdminOrders::class.java)
            .build()

        val adapter=object :FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): AdminOrdersViewHolder {
                val binding=OrdersLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return AdminOrdersViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: AdminOrdersViewHolder,
                position: Int,
                model: AdminOrders
            ) {
                holder.binding.orderUserName.text="Name: ${model.name}"
                holder.binding.orderPhoneNumber.text="Phone: ${model.phone}"
                holder.binding.orderAddressCity.text="Shipping Address: ${model.address}, ${model.city}"
                holder.binding.orderDateTime.text="Order at: ${model.date}, ${model.time}"
                holder.binding.orderTotalPrice.text="Total Amount: $ ${model.totalPrice}"
            }
        }

        binding.ordersList.adapter=adapter
        adapter.startListening()
    }
}