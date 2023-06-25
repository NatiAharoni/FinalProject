package com.example.myapplication.ui.login

import android.util.Patterns
import androidx.lifecycle.*
import com.example.myapplication.R
import com.example.myapplication.data.models.User
import kotlinx.coroutines.launch
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.Resource
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Handling the ViewModel of the Login fragment.
@HiltViewModel
class LoginViewModel @Inject constructor(private val authRep: AuthRepository) : ViewModel() {

    // The status of the user that's observed in the Login Fragmnet.
    private val _userSignInStatus = MutableLiveData<Resource<User>>()
    val userSignInStatus: LiveData<Resource<User>> = _userSignInStatus

    // The Sign-In function.
    // Checks input validation and sends the result of the logging to the fragment.
    fun signInUser(userEmail:String, userPass:String) {
        // Input validation
        val error = if (userEmail.isEmpty() || userPass.isEmpty())
            "Empty email or password"
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            "Not a valid email"
        }else null
        error?.let {
            _userSignInStatus.postValue(Resource.error(it))
        }
        // In case there was not any error in the previous steps - continue to the login itself:
        _userSignInStatus.postValue(Resource.loading())
        viewModelScope.launch {
            val loginResult = authRep.login(userEmail,userPass)
            _userSignInStatus.postValue(loginResult)
        }
    }
}
