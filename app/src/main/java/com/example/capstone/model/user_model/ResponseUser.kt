package com.example.capstone.model.user_modelimport android.os.Parcelableimport kotlinx.parcelize.Parcelize@Parcelizedata class ResponseUser(    var msg: String,    var status: Int,    var data: DetailUser) : Parcelable@Parcelizedata class DetailUser(    val id: Int,    val name: String,    val email: String,    val password: String,    val image_profile: String,    val phone: String,    val address: String) : Parcelabledata class ResponseUpdate(    var msg: String,    var status: Int)