package com.example.jetpackcompose.app.features.apiService.LogAPI

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory để cung cấp SignInViewModel với Context
class SignInViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
