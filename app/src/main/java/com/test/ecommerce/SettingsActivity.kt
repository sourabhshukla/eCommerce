package com.test.ecommerce

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.test.ecommerce.databinding.ActivitySettingsBinding
import com.test.ecommerce.prevalent.currentOnlineUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var context: Context
    private var imageUri:Uri?=null
    private lateinit var myUrl:String
    private lateinit var storageProfilePictureRef:StorageReference
    private lateinit var uploadTask: UploadTask
    private var checker: String?=null
    private lateinit var profileImageView: CircleImageView
    private lateinit var fullNameEditText: EditText
    private lateinit var userPhoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var profileChangeTextBtn: TextView
    private lateinit var closeTextBtn: TextView
    private lateinit var saveTextButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageProfilePictureRef=Firebase.storage.reference.child("Profile Pictures")
        context=this

         profileImageView = binding.settingsProfileImage
         fullNameEditText = binding.settingsFullName
         userPhoneEditText = binding.settingsPhoneNumber
         addressEditText = binding.settingsAddress
         profileChangeTextBtn = binding.profileImageChangeBtn
         closeTextBtn = binding.closeSettingsBtn
         saveTextButton =  binding.updateAccountSettingsBtn

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText)

        closeTextBtn.setOnClickListener { finish() }

        saveTextButton.setOnClickListener {
            if (checker == "clicked"){
                userInfoSaved()
            }
            else{
                updateOnlyUserInfo()
            }
        }

        profileChangeTextBtn.setOnClickListener {
            checker="clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)
            imageUri=result.uri
            profileImageView.setImageURI(imageUri)
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
            startActivity(Intent(this,SettingsActivity::class.java))
            finish();
        }
    }

    private fun updateOnlyUserInfo() {
        val usersRef=Firebase.database.reference.child("Users")

        val userMap:HashMap<String, Any> =HashMap<String, Any>()
        userMap["name"]=fullNameEditText.text.toString()
        userMap["phone"]=userPhoneEditText.text.toString()
        userMap["address"]=addressEditText.text.toString()
        usersRef.child(currentOnlineUser!!.phone!!).updateChildren(userMap)

        startActivity(Intent(this,HomeActivity::class.java))
        Toast.makeText(this,"Profile Updated Successfully!",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun userInfoSaved() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private fun uploadImage() {
        val progressDialog=ProgressDialog(this).apply {
            title = "Update Profile"
            setMessage("Please wait, while we are updating your account information")
            setCanceledOnTouchOutside(false)
            show()
        }

        if (imageUri!=null){
            val fileRef=storageProfilePictureRef
                .child(currentOnlineUser!!.phone+".jpg")
            uploadTask=fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask {
                if (!it.isSuccessful){
                    throw it.exception!!
                }
                fileRef.downloadUrl
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    val downloadUri=it.result
                    myUrl=downloadUri.toString()
                    val usersRef=Firebase.database.reference.child("Users")
                    val userMap:HashMap<String, Any> =HashMap<String, Any>()
                    userMap["name"]=fullNameEditText.text.toString()
                    userMap["phone"]=userPhoneEditText.text.toString()
                    userMap["address"]=addressEditText.text.toString()
                    userMap["image"]=myUrl
                    usersRef.child(currentOnlineUser!!.phone!!).updateChildren(userMap)

                    progressDialog.dismiss()
                    startActivity(Intent(this,HomeActivity::class.java))
                    Toast.makeText(this,"Profile Updated Successfully!",Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(this,"ERROR!",Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            progressDialog.dismiss()
            Toast.makeText(this,"Image Not Selected!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun userInfoDisplay(profileImageView: CircleImageView, fullNameEditText: EditText, userPhoneEditText: EditText, addressEditText: EditText) {
        val usersRef=Firebase.database.reference.child("Users").child(currentOnlineUser!!.phone!!)

        usersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        val image=snapshot.child("image").value.toString()
                        val name=snapshot.child("name").value.toString()
                        val phone=snapshot.child("phone").value.toString()
                        val password=snapshot.child("password").value.toString()
                        val address=snapshot.child("address").value.toString()

                        Picasso.get().load(image).into(profileImageView)
                        fullNameEditText.setText(name)
                        userPhoneEditText.setText(phone)
                        addressEditText.setText(address)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"ERROR: ${error}",Toast.LENGTH_SHORT).show()
            }
        })
    }
}