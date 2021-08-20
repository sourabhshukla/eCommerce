package com.test.ecommerce.sellers

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.R
import com.test.ecommerce.SettingsActivity
import com.test.ecommerce.databinding.ActivitySellerRegistrationBinding

class SellerRegistrationActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySellerRegistrationBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingProgressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth=Firebase.auth
        loadingProgressDialog= ProgressDialog(this)

        binding.sellerAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this,SellerLoginActivity::class.java))
        }

        binding.sellerRegisterBtn.setOnClickListener {
            registerSeller()
        }
    }

    private fun registerSeller() {
        val name=binding.sellerName.text.toString()
        val phone=binding.sellerPhone.text.toString()
        val email=binding.sellerEmail.text.toString()
        val password=binding.sellerPassword.text.toString()
        val address=binding.sellerAddress.text.toString()

        loadingProgressDialog.apply {
            setTitle("Creating Seller Account")
            setMessage("Please wait while we are creating the Account")
            setCanceledOnTouchOutside(false)
            show()
        }

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(address)){
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val rootRef=Firebase.database.reference
                        val uid =mAuth.currentUser?.uid
                        val sellerMap:HashMap<String, Any> =HashMap<String, Any>()
                        sellerMap["uid"] = uid.toString()
                        sellerMap["name"]=name
                        sellerMap["phone"]=phone
                        sellerMap["email"]=email
                        sellerMap["address"]=address
                        sellerMap["password"]=password

                        rootRef.child("Sellers").child(uid!!).updateChildren(sellerMap)
                            .addOnCompleteListener { task->
                                if (task.isSuccessful){
                                    loadingProgressDialog.dismiss()
                                    Toast.makeText(this,"You are Registered Successfully",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, SellerHomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK+Intent.FLAG_ACTIVITY_CLEAR_TASK))
                                    finish()
                                }
                                else{
                                    loadingProgressDialog.dismiss()
                                    Toast.makeText(this,"Seller Info Updation Failed in Firebase Database",Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else{
                        loadingProgressDialog.dismiss()
                        Toast.makeText(this,"Registration Failed ${it.result}",Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                }
        }
        else{
            loadingProgressDialog.dismiss()
            Toast.makeText(this,"Please complete the Registration Form",Toast.LENGTH_SHORT).show()
        }
    }
}