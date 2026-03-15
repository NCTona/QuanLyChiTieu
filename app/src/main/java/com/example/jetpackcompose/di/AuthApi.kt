package com.example.jetpackcompose.di

import javax.inject.Qualifier

/**
 * Qualifier cho ApiService KHONG co AuthInterceptor.
 * Dung cho Login, SignUp, ForgotPassword — chua co token.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthApi
