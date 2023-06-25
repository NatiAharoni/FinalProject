package com.example.myapplication.data.remote_db

import com.example.myapplication.utils.Resource
import retrofit2.Response
//  Class that provides a standardized way of making network requests, handling responses,
//  and encapsulating them within a Resource object that can represent success or failure
//  along with associated data or error messages.
abstract class BaseDataSource {

    protected suspend fun <T>
            getResult(call : suspend () -> Response<T>) : Resource<T> {

        try {
            val result  = call()
            if(result.isSuccessful) {
                val body = result.body()
                if(body != null) return  Resource.success(body)
            }
            return Resource.error("Network call has failed for the following reason: " +
                    "${result.message()} ${result.code()}")
        }catch (e : Exception) {
            return Resource.error("Network call has failed for the following reason: "
             + (e.localizedMessage ?: e.toString()))
        }
    }
}