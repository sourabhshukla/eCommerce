package com.test.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.ecommerce.admin.AdminCheckNewProductsActivity
import com.test.ecommerce.admin.AdminNewOrdersActivity
import com.test.ecommerce.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

         binding.adminLogoutBtn.setOnClickListener { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK+Intent.FLAG_ACTIVITY_CLEAR_TOP));finish() }
         binding.checkOrdersBtn.setOnClickListener { startActivity(Intent(this, AdminNewOrdersActivity::class.java)) }
         binding.maintainBtn.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java).putExtra("Admin","Admin")) }
         binding.checkApproveProductsBtn.setOnClickListener { startActivity(Intent(this, AdminCheckNewProductsActivity::class.java)) }

    }
}