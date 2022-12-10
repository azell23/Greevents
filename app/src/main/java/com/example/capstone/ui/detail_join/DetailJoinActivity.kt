package com.example.capstone.ui.detail_join

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.capstone.data.Result
import com.example.capstone.databinding.ActivityDetailJoinBinding
import com.example.capstone.factory.ViewModelFactory
import com.example.capstone.model.DetailJoin
import com.example.capstone.ui.detail_event.DetailEventActivity
import com.example.capstone.ui.detail_event.DetailEventActivity.Companion.EXTRA_ID
import com.example.capstone.ui.list_join.ListJoinActivity
import com.example.capstone.util.Constanta
import java.util.*

class DetailJoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailJoinBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val detailJoinViewModel: DetailJoinViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDetailJoin()
        setActionBar()
        setViewModel()
        back()
    }

    private fun setActionBar() {
        supportActionBar?.hide()
    }

    private fun back() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun getDetailJoin() {
        val getData = intent.getParcelableExtra<DetailJoin>(EXTRA_DATA) as DetailJoin
        binding.apply {
            tvUsername.text = getData.nama
            tvDetaulAddress.text = getData.alamat
            tvDetailPhoneUser.text = getData.phone
            tvDateRegistDetail.text =
                Constanta.formatDate(getData.createdAt, TimeZone.getDefault().id)
            tvNameEvent.text = getData.event.name
            tvDate.text = getData.event.date
            tvDesc.text = getData.event.deskripsi
            Glide.with(binding.ivEventList)
                .load(getData.event.image_poster)
                .fitCenter()
                .into(ivEventList)
            showEvent(getData.event_id)
            btnCancelJoin(getData.id)
        }
    }

    private fun showEvent(id: Int) {
        binding.cardEvent.setOnClickListener {
            startActivity(Intent(this, DetailEventActivity::class.java).also {
                it.putExtra(EXTRA_ID, id)
            })
        }
    }

    private fun btnCancelJoin(id: Int) {
        binding.btnCancelJoin.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Peringatan !!!")
            builder.setMessage("Apakah Anda Yakin Ingin Cancel Event ?")
            builder.setNegativeButton("Tidak") { _, _ ->
            }
            builder.setPositiveButton("Iya") { _, _ ->
                cancelJoin(id)
                startActivity(Intent(this, ListJoinActivity::class.java))
                finish()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun cancelJoin(id: Int) {
        detailJoinViewModel.deleteJoin(id).observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Error -> {
                    showLoading(false)
                    Log.d("cekH", "${it.error}")
                    Toast.makeText(this, "${it.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("tod", "${it.data.msg}")
                    Toast.makeText(this, "${it.data.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetaiJoin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}