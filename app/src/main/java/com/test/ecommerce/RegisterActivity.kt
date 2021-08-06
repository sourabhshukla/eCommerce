package com.test.ecommerce

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityRegisterBinding
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var loadingProgressBar: ProgressDialog
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingProgressBar= ProgressDialog(this)
        context=this

        binding.registerBtn.setOnClickListener { createAccount() }
    }

    private fun createAccount() {
        val name=binding.registerUsernameInput.text.toString()
        val phoneNumber=binding.registerPhoneNumberInput.text.toString()
        val password=binding.registerPasswordInput.text.toString()

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please Enter Username",Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,"Please Enter Phone Number",Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show()
        }
        else{
            loadingProgressBar.apply {
                setTitle("Create Account")
                setMessage("Please Wait While We Are Checking Credentials...")
                setCanceledOnTouchOutside(false)
                show()

                validatePhoneNumber(name, phoneNumber, password)
            }
        }
    }

    private fun validatePhoneNumber(name: String, phoneNumber: String, password: String) {
        val database=Firebase.database.reference

        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.child("Users").child(phoneNumber).exists()){
                    val userDataMap: HashMap<String, String> = HashMap<String, String>()
                    userDataMap["phone"] = phoneNumber
                    userDataMap["password"] = password
                    userDataMap["name"] = name

                    database.child("Users").child(phoneNumber).updateChildren(userDataMap as Map<String, Any>)
                        .addOnCompleteListener(object: OnCompleteListener<Void>{
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful){
                                    Toast.makeText(context,"Congratulations Your account has been Created",Toast.LENGTH_SHORT).show()
                                    loadingProgressBar.dismiss()
                                    startActivity(Intent(context,RegisterActivity::class.java))
                                }
                                else{
                                    loadingProgressBar.dismiss()
                                    Toast.makeText(context,"Network Error: Please Try Again",Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                }
                else{
                    Toast.makeText(context,"This "+phoneNumber+"is already Registered",Toast.LENGTH_SHORT).show()
                    loadingProgressBar.dismiss()
                    Toast.makeText(context,"Please Try Using Another Phone",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(context,MainActivity::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"DataBase error",Toast.LENGTH_SHORT).show()
            }
        })
    }
}