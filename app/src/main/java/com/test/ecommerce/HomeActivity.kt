package com.test.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.test.ecommerce.admin.AdminMaintainProductsActivity
import com.test.ecommerce.databinding.ActivityHomeBinding
import com.test.ecommerce.databinding.ProductItemLayoutBinding
import com.test.ecommerce.model.Products
import com.test.ecommerce.prevalent.currentOnlineUser
import com.test.ecommerce.viewHolder.ProductViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var productRef:DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private var type: String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent?.let {
            it.getStringExtra("Admin")
        }
        productRef=Firebase.database.reference.child("Products")
        recyclerView=findViewById(R.id.recycler_menu)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(this)

        Paper.init(this)
        binding.appBarHome.toolbar.title = "Home"
        setSupportActionBar(binding.appBarHome.toolbar)

        binding.appBarHome.fab.setOnClickListener {
            if (!type.equals("Admin"))
            startActivity(Intent(this,CartActivity::class.java))
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView=navView.getHeaderView(0)
        val userNameTextView: TextView=headerView.findViewById(R.id.user_profile_name)
        //Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
        val profileImageView : CircleImageView=headerView.findViewById(R.id.profile_image)

        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        userNameTextView.text= currentOnlineUser!!.name
        Picasso.get().load(currentOnlineUser!!.image).placeholder(R.drawable.profile).into(profileImageView)
    }

    override fun onStart() {
        super.onStart()

        val options=FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(productRef,Products::class.java)
            .build()

        val adapter= object: FirebaseRecyclerAdapter<Products,ProductViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                val binding = ProductItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return ProductViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: ProductViewHolder,
                position: Int,
                model: Products
            ) {
                holder.binding
                holder.binding.productName.text=model.pname
                holder.binding.productDescription.text=model.description
                holder.binding.productPrice.text=model.price
                Picasso.get().load(model.image).into(holder.binding.productImage)
                holder.itemView.setOnClickListener {
                    if(type=="Admin"){
                        startActivity(Intent(applicationContext, AdminMaintainProductsActivity::class.java).putExtra("pid",model.pid))
                    }
                    else{
                        startActivity(Intent(applicationContext,ProductDetailActivity::class.java).putExtra("pid",model.pid))
                    }
                }
            }
        }
        recyclerView.adapter=adapter
        adapter.startListening()
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.nav_logout){
            Firebase.auth.signOut()
            Paper.book().destroy()
            startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()  //User can't go back when he presses back button
        }
        else if (item.itemId==R.id.nav_settings){
            if(!type.equals("Admin"))
            startActivity(Intent(this,SettingsActivity::class.java))
        }
        else if(item.itemId==R.id.nav_cart){
            if(!type.equals("Admin"))
            startActivity(Intent(this,CartActivity::class.java))
        }
        else if (item.itemId==R.id.nav_search){
            if(!type.equals("Admin"))
            startActivity(Intent(this,SearchProductsActivity::class.java))
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    };

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}