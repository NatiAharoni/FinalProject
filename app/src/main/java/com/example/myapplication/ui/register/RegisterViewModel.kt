package com.example.myapplication.ui.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.example.myapplication.R
import com.example.myapplication.data.models.User
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.Resource
import com.example.myapplication.utils.Success
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _userRegistrationStatus = MutableLiveData<Resource<User>>()
    val userRegistrationStatus: LiveData<Resource<User>> = _userRegistrationStatus

    fun createUser(email: String, password: String) {
        val error = if(email.isEmpty() || password.isEmpty())
            "Empty email or password"
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Not a valid email"
        }else null
        error?.let {
            _userRegistrationStatus.postValue(Resource.error(it))
        }
        _userRegistrationStatus.postValue(Resource.loading())
        viewModelScope.launch{
            val registrationResult = repository.createUser(email,password)
            _userRegistrationStatus.postValue(registrationResult)
        }
    }
}

//   --- The implementation of the class BEFORE using HILT ---
//class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
//
//    private val _userRegistrationStatus = MutableLiveData<Resource<User>>()
//    val userRegistrationStatus: LiveData<Resource<User>> = _userRegistrationStatus
//
//    fun createUser(email: String, password: String) {
//        val error = if(email.isEmpty() || password.isEmpty())
//            "Empty Strings"
//        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            "Not a valid email"
//        }else null
//        error?.let {
//            _userRegistrationStatus.postValue(Resource.error(it))
//        }
//        _userRegistrationStatus.postValue(Resource.loading())
//        viewModelScope.launch{
//            val registrationResult = repository.createUser(email,password)
//            _userRegistrationStatus.postValue(registrationResult)
//        }
//    }
//
//    class RegisterViewModelFactory(private val repo: AuthRepository) : ViewModelProvider.NewInstanceFactory() {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return RegisterViewModel(repo) as T
//        }
//    }
//}