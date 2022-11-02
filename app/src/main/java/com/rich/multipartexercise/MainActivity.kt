package com.rich.multipartexercise

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.rich.multipartexercise.databinding.ActivityMainBinding
import com.rich.multipartexercise.viewmodel.UserViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var userVM : UserViewModel
    private lateinit var image : MultipartBody.Part
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            val contentResolver = this.contentResolver
            val type = contentResolver.getType(result!!)
            binding.imgProfile.setImageURI(result)

            val tempFile = File.createTempFile("image", "jpg",null)
            val inputStream = contentResolver.openInputStream(result!!)
            tempFile.outputStream().use {
                inputStream?.copyTo(it)
            }
            val reqBody : RequestBody = tempFile.asRequestBody(type!!.toMediaType())
            image = MultipartBody.Part.createFormData("image", tempFile.name, reqBody)
        }
    private val REQUEST_CODE_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userVM = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnEditPhoto.setOnClickListener {
            addPhoto()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val fullName = binding.fullnameInput.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val email = binding.emailInput.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val password = binding.passwordInput.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val phoneNumber = binding.phoneNumberInput.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val address = binding.addressInput.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val city = binding.cityInput.text.toString().toRequestBody("multipart/form-data".toMediaType())

        userVM.registerUser(fullName,email,password,phoneNumber,address,city,image)
        userVM.registerUser.observe(this){
            if(it != null){
                Toast.makeText(this,"Registration Success",Toast.LENGTH_SHORT)
            }else{
                Toast.makeText(this,"Registration Failed",Toast.LENGTH_SHORT)
            }
        }
    }

    private fun addPhoto() {
        checkingPermissions()
    }

    private fun checkingPermissions() {
        if (isGranted(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION,)
        ){
            openGallery()
        }
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int,
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun openGallery() {
        this.intent.type = "image/*"
        galleryResult.launch("image/*")
    }
}