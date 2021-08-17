package com.test.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityConfirmFileOrderBinding
import com.test.ecommerce.prevalent.currentOnlineUser
import java.lang.StringBuilder
import java.text.SimpleDateFormat

class ConfirmFileOrderActivity : AppCompatActivity() {
    private lateinit var totalPrice:String
    private lateinit var binding: ActivityConfirmFileOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConfirmFileOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        totalPrice= intent.getStringExtra("Total Price").toString()

        binding.confirmFinalOrderBtn.setOnClickListener { check() }
    }

    private fun check() = if (TextUtils.isEmpty(binding.shipmentName.toString())){
        Toast.makeText(this,"Please Enter Your Shipment Name",Toast.LENGTH_SHORT).show()
    }
    else if (TextUtils.isEmpty(binding.shipmentPhoneNumber.toString())){
        Toast.makeText(this,"Please Enter Your Phone Number",Toast.LENGTH_SHORT).show()
    }
    else if (TextUtils.isEmpty(binding.shipmentAddress.toString())){
        Toast.makeText(this,"Please Enter Your Addredd",Toast.LENGTH_SHORT).show()
    }
    else if (TextUtils.isEmpty(binding.shipmentCity.toString())){
        Toast.makeText(this,"Please Enter Your City Name",Toast.LENGTH_SHORT).show()
    }
    else{ confirmOrder() }

    private fun confirmOrder() {
        val calender=java.util.Calendar.getInstance()

        val currentDate= SimpleDateFormat("dd mm yyyy")
        val saveCurrentDate=currentDate.format(calender.time)

        val currentTime= SimpleDateFormat("hh:mm:ss a")
        val saveCurrentTime=currentTime.format(calender.time)

        val ordersRef= Firebase.database.reference.child("Orders").child(currentOnlineUser!!.phone!!)
        val orderMap:HashMap<String, Any> =HashMap<String, Any>()
        orderMap["name"]=binding.shipmentName.text.toString()
        orderMap["phone"]=binding.shipmentPhoneNumber.text.toString()
        orderMap["address"]=binding.shipmentAddress.text.toString()
        orderMap["city"]=binding.shipmentCity.text.toString()
        orderMap["time"]=saveCurrentTime
        orderMap["date"]=saveCurrentDate
        orderMap["totalPrice"]=totalPrice
        orderMap["state"]="not shipped"

        ordersRef.updateChildren(orderMap).addOnCompleteListener {task ->
            if (task.isSuccessful){
                Firebase.database.reference.child("cart list").child("User View")
                    .child(currentOnlineUser!!.phone!!).removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this,"Your Final Order Has Been Placed Successfully",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK+Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            finish()
                        }
                    }
            }
        }
    }
}