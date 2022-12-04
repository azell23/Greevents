package com.example.capstone.ui.edit_profille

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.data.Result
import com.example.capstone.databinding.ActivityEditProfileAvatarBinding
import com.example.capstone.factory.ViewModelFactory
import com.example.capstone.ui.custom_view.MyAlertDialog
import com.example.capstone.ui.detail_event.DetailEventActivity
import com.example.capstone.ui.home.HomeFragment
import com.example.capstone.ui.list_join.ListJoinActivity
import com.example.capstone.ui.main.MainActivity
import com.example.capstone.ui.profile.DetailProfileActivity
import com.example.capstone.ui.profile.DetailProfileViewModel
import com.example.capstone.ui.profile.ProfileFragment
import com.example.capstone.util.reduceFileImage
import com.example.capstone.util.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileAvatarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileAvatarBinding
    private var getFile: File? = null
    private lateinit var viewModelFactory: ViewModelFactory
    private val detailProfileViewModel: DetailProfileViewModel by viewModels { viewModelFactory }
    private val editProfileAvatarViewModel:EditProfileAvatarViewModel by viewModels { viewModelFactory }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak Ada Response",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPermission()
        setActionBar()
        setViewModel()
        getDataUser()
        btnEdit()
        btnGalery()
    }

    private fun setPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setActionBar(){
        supportActionBar?.hide()
    }

    private fun setViewModel(){
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun getDataUser(){
        detailProfileViewModel.getDataUser.observe(this){
            binding.apply {
                when(it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error ->{
                        showLoading(false)
                    }
                    is Result.Success -> {
                        Glide.with(binding.ivEditAvatar)
                            .load(it.data.data.image_profile)
                            .fitCenter()
                            .into(ivEditAvatar)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun btnGalery() {
        binding.btnOpenGalery.setOnClickListener {
            startGalery()
        }
    }


    private fun btnEdit() {
        binding.btnUpdateAvatar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Peringatan !!!")
            builder.setMessage("Upadte Avatar ?")
            builder.setNegativeButton("Tidak") { _, _ ->
            }
            builder.setPositiveButton("Iya") { _, _ ->
                updateProfileAvatar()
                finish()
                onResume()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun startGalery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.ivEditAvatar.setImageURI(selectedImg)
        }
    }

    private fun updateProfileAvatar() {
        var file = reduceFileImage(getFile as File)
        val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image_profile",
            file.name,
            currentImageFile
        )
        editProfileAvatarViewModel.updateProfileAvatar(
            imageMultipart
        ).observe(this) {
            if (it != null){
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Log.d("gagag", "${it.error}")
                        Toast.makeText(this, "${it.error.toString()}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(this, "${it.data.msg}", Toast.LENGTH_SHORT).show()
                        Log.d("hapusC", "${it.data.msg}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBarEditAvatar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}