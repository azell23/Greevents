package com.example.capstone.ui.edit_profille

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.data.Result
import com.example.capstone.databinding.ActivityEditProfilleBinding
import com.example.capstone.factory.ViewModelFactory
import com.example.capstone.ui.profile.DetailProfileActivity
import com.example.capstone.ui.profile.DetailProfileViewModel

class EditProfilleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilleBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val editProfilleViewModel: EditProfilleViewModel by viewModels { viewModelFactory }
    private val detailProfileViewModel: DetailProfileViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()
        setViewModel()
        getDataUser()
        btnEdit()
        back()
    }


    private fun setActionBar() {
        supportActionBar?.hide()
    }

    private fun back() {
        binding.imageView7.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun getDataUser() {
        detailProfileViewModel.getDataUser.observe(this) {
            binding.apply {
                when (it) {
                    is Result.Error -> {
                        Log.d("error", "${it.error}")
                    }
                    is Result.Success -> {
                        edtNameUser.setText(it.data.data.name)
                        edtEmailUser.setText(it.data.data.email)
                        edtAddressUser.setText(it.data.data.address)
                        edtContactUser.setText(it.data.data.phone)
                    }
                }
            }
        }
    }

    private fun btnEdit() {
        binding.btnEditProfile.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Peringatan !!!")
            builder.setMessage("Simpan Perubahan ?")
            builder.setNegativeButton("Tidak") { _, _ ->
            }
            builder.setPositiveButton("Iya") { _, _ ->
                updateProfile()
                startActivity(Intent(this, DetailProfileActivity::class.java))
                onResume()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun updateProfile() {
        val name = binding.edtNameUser.text.toString()
        val address = binding.edtAddressUser.text.toString()
        val email = binding.edtEmailUser.text.toString()
        val phone = binding.edtContactUser.text.toString()
        editProfilleViewModel.updateProfile(
            name,
            email,
            address,
            phone
        ).observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "${it.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    Toast.makeText(this, "${it.data.msg}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarEditProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}