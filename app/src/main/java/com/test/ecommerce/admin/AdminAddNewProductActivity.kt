package com.test.ecommerce.admin
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.test.ecommerce.databinding.ActivityAdminAddNewProductBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AdminAddNewProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminAddNewProductBinding
    private lateinit var categoryName: String
    private lateinit var description: String
    private lateinit var price: String
    private lateinit var pname: String
    private lateinit var saveCurrentDate: String
    private lateinit var saveCurrentTime: String
    private lateinit var productRandomKey: String
    private lateinit var downloadUri: String
    private lateinit var productImageRef: StorageReference
    private lateinit var productRef:DatabaseReference
    private lateinit var loadingProgressDialog: ProgressDialog
    private var imageUri: Uri?=null
    lateinit var getAction:ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productImageRef=Firebase.storage.reference.child("Product Images")
        productRef=Firebase.database.reference.child("Products")
        loadingProgressDialog= ProgressDialog(this)

        categoryName= intent.getStringExtra("category").toString()

        binding.selectProductImage.setOnClickListener { openGallery() }
        binding.addNewProduct.setOnClickListener { validateProductData() }
        getAction=registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.selectProductImage.setImageURI(uri)
            imageUri=uri
        }

        Toast.makeText(this,categoryName,Toast.LENGTH_SHORT).show()
    }

    private fun validateProductData() {
        description=binding.productDescription.text.toString()
        price=binding.productPrice.text.toString()
        pname=binding.productName.text.toString()

        if (imageUri==null){Toast.makeText(this,"Product Image Is Mandatory...",Toast.LENGTH_SHORT).show()}
        else if (TextUtils.isEmpty(description)){Toast.makeText(this,"Description Is Mandatory...",Toast.LENGTH_SHORT).show()}
        else if (TextUtils.isEmpty(price)){Toast.makeText(this,"Price Is Mandatory...",Toast.LENGTH_SHORT).show()}
        else if (TextUtils.isEmpty(pname)){Toast.makeText(this,"Product Name Is Mandatory...",Toast.LENGTH_SHORT).show()}
        else{storeProductInformation()}
    }

    private fun storeProductInformation() {
        val calender=Calendar.getInstance()
        val currentDate= SimpleDateFormat("dd,mm,yyyy")
        val currentTime=SimpleDateFormat("hh:mm:ss a")
        saveCurrentDate=currentDate.format(calender.time)
        saveCurrentTime=currentTime.format(calender.time)
        productRandomKey=saveCurrentDate+saveCurrentTime

        loadingProgressDialog.apply {
            setTitle("Add New Product")
            setMessage("Please Wait, While We Are Adding The Product")
            setCanceledOnTouchOutside(false)
            show()
        }

        val filePath: StorageReference=productImageRef.child(imageUri!!.lastPathSegment!!+productRandomKey+".jpg")

        val uploadTask=filePath.putFile(imageUri!!)
        uploadTask
            .addOnFailureListener(OnFailureListener {
            Toast.makeText(this, "ERROR: $it",Toast.LENGTH_SHORT).show()
                loadingProgressDialog.dismiss()
        })
            .addOnSuccessListener {
                Toast.makeText(this,"Product Image Uploaded Successfully.....",Toast.LENGTH_SHORT).show()
                val urlTask=uploadTask.continueWithTask {
                    if (!it.isSuccessful){
                        it.exception?.let { throw it }
                    }
                    filePath.downloadUrl
                }
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                             downloadUri=it.result.toString()
                            Toast.makeText(this,"Image Url Saved To Database",Toast.LENGTH_SHORT).show()
                            savaProductInfoToDatabase()
                        }
                        else{Toast.makeText(this,"Image Url couldn't be saved to Database",Toast.LENGTH_SHORT).show()}
                    }
            }
    }

    private fun savaProductInfoToDatabase() {
        val productMap:HashMap<String,Any> =HashMap<String,Any>()
        productMap["pid"] = productRandomKey
        productMap["date"] = saveCurrentDate
        productMap["time"] = saveCurrentTime
        productMap["description"] = description
        productMap["image"] = downloadUri
        productMap["category"] = categoryName
        productMap["price"] = price
        productMap["pname"] = pname
        productMap["category"] = categoryName

        productRef.child(productRandomKey).updateChildren(productMap)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    startActivity(Intent(this, AdminCategoryActivity::class.java))
                    loadingProgressDialog.dismiss()
                    Toast.makeText(this,"Product added successfully",Toast.LENGTH_SHORT).show()
                }
                else{
                    loadingProgressDialog.dismiss()
                    val msg=it.exception.toString()
                    Toast.makeText(this,"ERROR: $msg",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun openGallery() {

        binding.selectProductImage.setOnClickListener { getAction.launch("image/*") }

    }
}