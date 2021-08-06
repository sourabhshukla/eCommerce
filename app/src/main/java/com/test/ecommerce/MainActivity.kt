package com.test.ecommerce

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityMainBinding
import com.test.ecommerce.model.Users
import com.test.ecommerce.prevalent.UserPasswordKey
import com.test.ecommerce.prevalent.UserPhoneKey
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var context: Context
    lateinit var loadingProgressDialog: ProgressDialog
    var check: Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        Paper.init(this)

        val userPhone=Paper.book().read<String>(UserPhoneKey)
        val userPassword=Paper.book().read<String>(UserPasswordKey)
        loadingProgressDialog= ProgressDialog(this)

        if(userPhone!=null && userPassword!=null){
            AllowAccess(userPhone,userPassword)
            loadingProgressDialog.apply {
                setTitle("Logging In User")
                setMessage("Fetching User Credentials")
                setCanceledOnTouchOutside(false)
                show()
            }

        }

        binding.mainLoginBtn.setOnClickListener {
            val intent=Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.mainJoinNowBtn.setOnClickListener {
            val intent=Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun AllowAccess(userPhone: String, userPassword: String) {
        val database=Firebase.database.reference

        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("Users").child(userPhone).exists()){
                    val userData=snapshot.child("Users").child(userPhone).getValue(Users::class.java)
                    if (userData?.phone.equals(userPhone) && userData?.password.equals(userPassword)){
                        Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()
                        loadingProgressDialog.dismiss()
                        startActivity(Intent(context,HomeActivity::class.java))
                    }
                    else{Toast.makeText(context,"Incorrect Credentials",Toast.LENGTH_SHORT).show()}
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}