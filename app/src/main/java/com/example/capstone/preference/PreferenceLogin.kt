package com.example.capstone.preferenceimport android.content.Contextimport com.example.capstone.model.login_model.LoginResultModelclass PreferenceLogin(context: Context) {    private val preferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE)    fun setAuthLogin(value: LoginResultModel){        val mEdit = preferences.edit()        mEdit.putString(NAME, value.name)        mEdit.putString(USER_ID, value.id)        mEdit.putString(TOKEN, value.token)        mEdit.apply()    }    fun getUser(): LoginResultModel {        val name = preferences.getString(NAME, null)        val userId = preferences.getString(USER_ID, null)        val token = preferences.getString(TOKEN, null)        return LoginResultModel(userId, name, token)    }    fun deleteUser(){        val mEdit = preferences.edit().clear()        mEdit.apply()    }    companion object{        private const val NAME_PREF = "login_pref"        private const val NAME = "extra_name"        private const val USER_ID = "extra_userId"        private const val TOKEN = "extra_token"        private const val ONBOARDING = "extra_onboarding"    }}