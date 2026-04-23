package com.example.jetpackcompose.data.remote.dto

data class RegistrationData(
    val phone_number: String,
    val email: String,
    val password: String,
    val retype_password: String
)

data class LoginData(
    val phone_number: String,
    val password: String,
)

data class LoginResponse(
    val status: String,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

data class RegistrationResponse(
    val status: String,
    val message: String
)

data class SendOtp(
    val email: String
)

data class RefreshToken(
    val refreshToken: String
)

data class VerifyOtp(
    val email: String,
    val otp: String
)

data class ResetPassword(
    val email: String,
    val resetToken: String,
    val newPassword: String,
    val confirmPassword: String
)

data class ApiResponse(
    val status: String,
    val message: String
)

data class OTPResponse(
    val status: String,
    val message: String,
    val resetToken: String
)
