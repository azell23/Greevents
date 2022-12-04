package com.example.capstone.ui.daftar_event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.capstone.R
import com.example.capstone.data.Result
import com.example.capstone.databinding.ActivityDaftarEventBinding
import com.example.capstone.factory.ViewModelFactory
import com.example.capstone.ui.custom_view.MyAlertDialog
import com.example.capstone.ui.list_join.ListJoinActivity

class DaftarEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDaftarEventBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val daftarEventViewModel: DaftarEventViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val i = intent.getIntExtra(EXTRA_ID_EVENT, 0)
        Log.d("KONTOLLLLLLLL", "$i")
        binding.edtIdEvent.setText(i.toString())
        setActionBar()
        setViewModel()
        buttonJoin()
        back()

    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun setActionBar() {
        supportActionBar?.hide()
    }

    private fun btnJoin() {
        binding.btnJOinEvent.setOnClickListener {
            joinEvent()
        }
    }
    private fun back(){
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun buttonJoin(){
        binding.btnJOinEvent.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Peringatan !!!")
            builder.setMessage("Yakin Daftar Event ?")
            builder.setNegativeButton("Tidak") { _, _ ->
            }
            builder.setPositiveButton("Iya") { _, _ ->
                joinEvent()
                finish()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun joinEvent() {

        val id_event = binding.edtIdEvent.text.toString().toInt()
        val nama = binding.edtNameUser.text.toString()
        val alamat = binding.edtAddressUser.text.toString()
        val phone = binding.edtContactUser.text.toString()
        daftarEventViewModel.joinEvent(id_event, nama, alamat, phone).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        startActivity(Intent(this, ListJoinActivity::class.java))
                        Toast.makeText(this, "${it.data.msg}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "${it.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDaftar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun successAlert() {
        MyAlertDialog(
            this,
            R.string.sukses,
            R.drawable.ic_person,
            fun() {
                val moveActivity = Intent(this, ListJoinActivity::class.java)
                moveActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(moveActivity)
                finish()
            }
        ).show()
    }

    companion object {
        const val EXTRA_ID_EVENT = "extra id"
    }
}