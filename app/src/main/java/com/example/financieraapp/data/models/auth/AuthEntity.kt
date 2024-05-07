package com.example.financieraapp.data.models.auth

import com.google.gson.annotations.SerializedName

data class AuthEntity (
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)