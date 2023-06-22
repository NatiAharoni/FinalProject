package com.example.myapplication.data.repository

import com.example.myapplication.data.models.User
import com.example.myapplication.utils.Resource


interface AuthRepository {

    suspend fun currentUser() : Resource<User>
    suspend fun login(email:String, password:String) : Resource<User>
    suspend fun createUser(userEmail:String,
                           userLoginPass:String) : Resource<User>
    fun logout()
}