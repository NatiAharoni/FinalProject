package com.example.myapplication.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
//A utility function combines the fetching and saving of data
//from both local and remote databases, providing a unified LiveData
//stream of the data with appropriate loading and error states.
fun <T,A> performFetchingAndSaving(localDbFetch: () -> LiveData<T>,
                                   remoteDbFetch: suspend () -> Resource<A>,
                                   localDbSave: suspend (A) -> Unit) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        emit(Resource.loading())

        val source = localDbFetch().map { Resource.success(it) }
        emitSource(source)

        val fetchResource = remoteDbFetch()

        if(fetchResource.status is Success)
            localDbSave(fetchResource.status.data!!)

        else if(fetchResource.status is Error){
            emit(Resource.error(fetchResource.status.message))
            emitSource(source)
        }
    }