package com.test.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.ecommerce.databinding.ActivityAdminAddNewProductBinding
import com.test.ecommerce.databinding.ActivityAdminCategoryBinding

class AdminCategoryActivity : AppCompatActivity() {
    lateinit var binding:ActivityAdminCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tShirts.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","tShirts")) }
        binding.sportsTShirts.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","sportdTShirt")) }
        binding.femaleDresses.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","femaleDresses")) }
        binding.sweathers.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","sweathers")) }
        binding.glasses.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","glasses")) }
        binding.hatsCaps.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","hatsCaps")) }
        binding.pursesBagsWallets.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","wallets bags purses")) }
        binding.shoes.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","shoes")) }
        binding.headphonesHandfree.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","Head Phones Hand Free")) }
        binding.laptopPc.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","Laptops")) }
        binding.watches.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","Watches")) }
        binding.mobilephones.setOnClickListener { startActivity(Intent(this,AdminAddNewProductActivity::class.java).putExtra("category","Mobile Phones")) }



    }
}