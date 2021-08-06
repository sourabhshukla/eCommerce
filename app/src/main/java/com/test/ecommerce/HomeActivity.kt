package com.test.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.test.ecommerce.databinding.ActivityHomeBinding
import io.paperdb.Paper

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        val mAuth=Firebase.auth
        binding.btnLogout.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            Paper.book().destroy()
        }

    }
}