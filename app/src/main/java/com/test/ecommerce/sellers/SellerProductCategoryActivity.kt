package com.test.ecommerce.sellers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.ecommerce.databinding.ActivitySellerProductCategoryBinding

class SellerProductCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivitySellerProductCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySellerProductCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tShirts.setOnClickListener { startActivity(Intent(this, SellerAddNewProductActivity::class.java).putExtra("category","tShirts")) }
        binding.sportsTShirts.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","sportsTShirt")) }
        binding.femaleDresses.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","femaleDresses")) }
        binding.sweathers.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","sweaters")) }
        binding.glasses.setOnClickListener { startActivity(Intent(this, SellerAddNewProductActivity::class.java).putExtra("category","glasses")) }
        binding.hatsCaps.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","hatsCaps")) }
        binding.pursesBagsWallets.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","wallets bags purses")) }
        binding.shoes.setOnClickListener { startActivity(Intent(this, SellerAddNewProductActivity::class.java).putExtra("category","shoes")) }
        binding.headphonesHandfree.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","Head Phones Hand Free")) }
        binding.laptopPc.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","Laptops")) }
        binding.watches.setOnClickListener { startActivity(Intent(this, SellerAddNewProductActivity::class.java).putExtra("category","Watches")) }
        binding.mobilephones.setOnClickListener { startActivity(Intent(this,
            SellerAddNewProductActivity::class.java).putExtra("category","Mobile Phones")) }
       // binding.adminLogoutBtn.setOnClickListener { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK+Intent.FLAG_ACTIVITY_CLEAR_TOP));finish() }
       // binding.checkOrdersBtn.setOnClickListener { startActivity(Intent(this,
        //    AdminNewOrdersActivity::class.java)) }
        //binding.maintainBtn.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java).putExtra("Admin","Admin")) }



    }
}