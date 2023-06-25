package com.example.myapplication.ui.login

import android.util.Patterns
import androidx.lifecycle.*
import com.example.myapplication.data.models.User
import kotlinx.coroutines.launch
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.Resource
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRep: AuthRepository) : ViewModel() {

    private val _userSignInStatus = MutableLiveData<Resource<User>>()
    val userSignInStatus: LiveData<Resource<User>> = _userSignInStatus

    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser:LiveData<Resource<User>> = _currentUser

    init {
        viewModelScope.launch {
            _currentUser.postValue(Resource.loading())
            _currentUser.postValue(authRep.currentUser())
        }
    }
    fun signInUser(userEmail:String, userPass:String) {
        val error = if(userEmail.isEmpty() || userPass.isEmpty())
            "Empty email or password"
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            "Not a valid email"
        }else null
        error?.let {
            _userSignInStatus.postValue(Resource.error(it))
        }
        _userSignInStatus.postValue(Resource.loading())
        viewModelScope.launch {
            val loginResult = authRep.login(userEmail,userPass)
            _userSignInStatus.postValue(loginResult)
        }
    }

    private fun checkIfAdmin(email:String) : Boolean{
        return email == "admin@gmail.com"
    }
}


//    fun signInUser(userEmail:String, userPass:String) {
//        if(userEmail.isEmpty() || userPass.isEmpty())
//            _userSignInStatus.postValue(Resource.error("Empty email or password"))
//        else{
//            _userSignInStatus.postValue(Resource.loading())
//            viewModelScope.launch {
//                val loginResult = authRep.login(userEmail,userPass)
//                _userSignInStatus.postValue(loginResult)
//            }
//        }
//    }




//   --- The implementation of the class BEFORE using HILT ---
//class LoginViewModel(private val authRep: AuthRepository) : ViewModel() {
//
//    private val _userSignInStatus = MutableLiveData<Resource<User>>()
//    val userSignInStatus: LiveData<Resource<User>> = _userSignInStatus
//
//    private val _currentUser = MutableLiveData<Resource<User>>()
//    val currentUser:LiveData<Resource<User>> = _currentUser
//
//    init {
//        viewModelScope.launch {
//            _currentUser.postValue(Resource.loading())
//            _currentUser.postValue(authRep.currentUser())
//        }
//    }
//
//    fun signInUser(userEmail:String, userPass:String) {
//        if(userEmail.isEmpty() || userPass.isEmpty())
//            _userSignInStatus.postValue(Resource.error("Empty email or password"))
//        else{
//            _userSignInStatus.postValue(Resource.loading())
//            viewModelScope.launch {
//                val loginResult = authRep.login(userEmail,userPass)
//                _userSignInStatus.postValue(loginResult)
//            }
//        }
//    }
//
//
//    class LoginViewModelFactory(private val repo: AuthRepository) : ViewModelProvider.NewInstanceFactory() {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return LoginViewModel(repo) as T
//        }
//    }
//}