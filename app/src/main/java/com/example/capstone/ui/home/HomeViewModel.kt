package com.example.capstone.ui.homeimport androidx.lifecycle.ViewModelimport com.example.capstone.data.AppRepositoryclass HomeViewModel(val appRepository: AppRepository) : ViewModel() {    fun getAllEvents() = appRepository.getAllEvents()    fun searchEvent(key: String) = appRepository.searchEvent(key)}