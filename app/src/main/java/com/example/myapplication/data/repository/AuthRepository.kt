package com.example.myapplication.data.repository

import com.example.myapplication.utils.Resource


interface AuthRepository {

    suspend fun currentUser() : Resource<Any>
    suspend fun login(email:String, password:String) : Resource<Any>
    suspend fun createUser(userEmail:String,
                           userLoginPass:String) : Resource<Any>
    fun logout()
}