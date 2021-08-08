package com.test.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.test.ecommerce.databinding.ActivityAdminAddNewProductBinding

class AdminAddNewProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminAddNewProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this,"Welcome Admin...",Toast.LENGTH_SHORT).show()
    }
}