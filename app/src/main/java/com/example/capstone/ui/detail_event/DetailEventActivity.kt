package com.example.capstone.ui.detail_event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.adapter.ListCommentAdapter
import com.example.capstone.data.Result
import com.example.capstone.databinding.ActivityDetailBinding
import com.example.capstone.factory.ViewModelFactory
import com.example.capstone.model.event_model.Comment
import com.example.capstone.ui.daftar_event.DaftarEventActivity
import com.example.capstone.ui.daftar_event.DaftarEventActivity.Companion.EXTRA_ID_EVENT
import com.example.capstone.ui.detail_comment.DetailCommentActivity
import com.example.capstone.ui.detail_comment.DetailCommentActivity.Companion.EXTRA_COMMENT
import com.example.capstone.ui.detail_comment.DetailCommentActivity.Companion.EXTRA_EVENT
import com.example.capstone.ui.detail_comment.DetailCommentActivity.Companion.EXTRA_USER
import com.example.capstone.util.PhotoDialog
import com.example.capstone.util.PhotoDialogSurat

class DetailEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var listCommentAdapter: ListCommentAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var seePhoto: PhotoDialog
    private lateinit var seeSurat: PhotoDialogSurat
    private val commentViewModel: CommentViewModel by viewModels { viewModelFactory }
    private val detailEventViewModel: DetailEventViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seePhoto = PhotoDialog(this)
        seeSurat = PhotoDialogSurat(this)

        setActionBar()
        setViewModel()
        getDetailEvent()
        btnComment()
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

    private fun btnJoinEvent(id: Int) {
        binding.btnJoinEvent.setOnClickListener {
            startActivity(Intent(this, DaftarEventActivity::class.java).also {
                it.putExtra(EXTRA_ID_EVENT, id)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        getDetailEvent()
    }

    private fun getDetailEvent() {
        val id = intent.getIntExtra(EXTRA_ID, 1)
        detailEventViewModel.getEventById(id).observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Error -> {
                    showLoading(false)
                }
                is Result.Success -> {
                    Toast.makeText(this, "${it.data.msg}", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        btnJoinEvent(it.data.data.id)
                        tvNameEventDetail.text = it.data.data.name
                        tvDateEvent.text = it.data.data.date
                        tvDescEvent.text = it.data.data.deskripsi
                        tvDetailLoc.text = it.data.data.location
                        tvDetailContactHp.text = it.data.data.contact_person
                        tvDetailEmail.text = it.data.data.email
                        tvDetailAuthor.text = it.data.data.author
                        Glide.with(this@DetailEventActivity)
                            .load(it.data.data.image_poster)
                            .into(ivEvent)
                        ivEvent.setOnClickListener {
                            seePhoto.showPhoto(intent.getStringExtra(EXTRA_POSTER)!!)
                            Toast.makeText(
                                this@DetailEventActivity,
                                "${intent.getStringExtra(EXTRA_POSTER)}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        btnLihatSk.setOnClickListener {
                            seeSurat.showPhoto(intent.getStringExtra(EXTRA_SURAT)!!)
                            Toast.makeText(
                                this@DetailEventActivity,
                                "${intent.getStringExtra(EXTRA_SURAT)}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        setRecyler(it.data.data.comments)
                    }
                    showLoading(false)
                }
            }
        }
    }

    private fun setRecyler(listComment: ArrayList<Comment>) {
        listCommentAdapter = ListCommentAdapter(listComment)
        binding.rvComment.apply {
            adapter = listCommentAdapter
            layoutManager = LinearLayoutManager(context)
        }

        listCommentAdapter.setOnItemClickCallback(object : ListCommentAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: Comment) {
                startActivity(
                    Intent(
                        this@DetailEventActivity,
                        DetailCommentActivity::class.java
                    ).also {
                        it.putExtra(EXTRA_COMMENT, data.id)
                        it.putExtra(EXTRA_USER, data.user_id)
                        it.putExtra(EXTRA_EVENT, data.event_id)
                        finish()
                    })
            }
        })
    }

    private fun btnComment() {
        val id = intent.getIntExtra(EXTRA_ID, 1)
        binding.btnComent.setOnClickListener {
            val comment = binding.edtCommentEvent.text.toString()
            if (comment.isEmpty()) {
                binding.edtCommentEvent.error = "Masukan Komentar"
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Peringatan !!!")
                builder.setMessage("Posting Komentar ?")
                builder.setNegativeButton("Tidak") { _, _ ->
                }
                builder.setPositiveButton("Iya") { _, _ ->
                    postComment()
                    startActivity(Intent(this, DetailEventActivity::class.java).also {
                        it.putExtra(EXTRA_ID, id)
                    })
                    finish()
                }
                val alert = builder.create()
                alert.show()

            }

        }
    }

    private fun postComment() {
        val id = intent.getIntExtra(EXTRA_ID, 1)
        val comment = binding.edtCommentEvent.text.toString()
        commentViewModel.postComment(id, comment).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Log.d("cek ", "${it.error}")
                    }
                    is Result.Success -> {
                        Toast.makeText(this, "${it.data.msg}", Toast.LENGTH_SHORT).show()
                        showLoading(false)

                    }
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "extra id"
        const val EXTRA_POSTER = "extra_poster"
        const val EXTRA_SURAT = "extra_surat"
    }
}