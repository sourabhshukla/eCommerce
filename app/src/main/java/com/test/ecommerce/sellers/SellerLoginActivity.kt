package com.test.ecommerce.sellers

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.R
import com.test.ecommerce.databinding.ActivitySellerLoginBinding

class SellerLoginActivity : AppCompatActivity() {
    private lateinit var loadingProgressDialog: ProgressDialog
    private lateinit var binding: ActivitySellerLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySellerLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingProgressDialog= ProgressDialog(this)
        mAuth=Firebase.auth

        binding.sellerLoginBtn.setOnClickListener { loginSeller() }
    }

    private fun loginSeller() {
        val email=binding.sellerLoginEmail.text.toString()
        val password=binding.sellerLoginPassword.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            loadingProgressDialog.apply {
                setTitle("Login Account")
                setMessage("Please wait while we are Checking User Credentials")
                setCanceledOnTouchOutside(false)
                show()
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        loadingProgressDialog.dismiss()
                        Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SellerHomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK+ Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finish()
                    }
                    else{
                        loadingProgressDialog.dismiss()
                        Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            loadingProgressDialog.dismiss()
            Toast.makeText(this,"Please Complete the Registration form",Toast.LENGTH_SHORT).show()
        }
    }
}