package com.example.capstone.ui.edit_profilleimport androidx.lifecycle.ViewModelimport com.example.capstone.data.AppRepositoryclass EditProfilleViewModel(private val appRepository: AppRepository) : ViewModel() {    fun updateProfile(        name: String,        email: String,        address: String,        phoen: String    ) = appRepository.updateProfile(name, email, address, phoen)}