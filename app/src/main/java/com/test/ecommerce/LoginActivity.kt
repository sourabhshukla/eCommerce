package com.test.ecommerce

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityLoginBinding
import com.test.ecommerce.model.Users
import com.test.ecommerce.prevalent.UserPasswordKey
import com.test.ecommerce.prevalent.UserPhoneKey
import io.paperdb.Paper

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var loadingProgressDialog: ProgressDialog
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingProgressDialog= ProgressDialog(this)
        context=this

        Paper.init(this)

        binding.loginBtn.setOnClickListener{ LoginUser() }

    }

    private fun LoginUser() {

        val phoneNumber=binding.loginPhoneNumberInput.text.toString()
        val password=binding.loginPasswordInput.text.toString()

        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,"Please Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT).show()
        }
        else{
            loadingProgressDialog.apply {
                setTitle("Login Account")
                setMessage("Please Wait While We Are Checking Credentials...")
                setCanceledOnTouchOutside(false)
                show()

               allowAccessToAccount(phoneNumber,password)
            }
        }
    }

    private fun allowAccessToAccount(phoneNumber: String, password: String) {
        if(binding.rememberMe.isChecked){
            Paper.book().write(UserPhoneKey,phoneNumber)
            Paper.book().write(UserPasswordKey,password)
        }
        val database= Firebase.database.reference

        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("Users").child(phoneNumber).exists()){
                    val userData=snapshot.child("Users").child(phoneNumber).getValue(Users::class.java)

                    if(userData?.phone.equals(phoneNumber)){
                        if(userData?.password.equals(password)){
                            Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()
                            loadingProgressDialog.dismiss()
                            startActivity(Intent(context,HomeActivity::class.java))
                        }
                        else{Toast.makeText(context,"Incorrect Password",Toast.LENGTH_SHORT).show()}
                    }
                    else{Toast.makeText(context,"Incorrect Phone Number",Toast.LENGTH_SHORT).show()}
                }
                else{
                    Toast.makeText(context,"Account with this Phone Number does not exits",Toast.LENGTH_SHORT).show()
                    loadingProgressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}