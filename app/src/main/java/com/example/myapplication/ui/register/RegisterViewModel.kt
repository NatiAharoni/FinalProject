package com.example.myapplication.ui.register

import android.util.Patterns
import androidx.lifecycle.*
import com.example.myapplication.data.models.User
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
// Handling the ViewModel part of the Registration fragment.
@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _userRegistrationStatus = MutableLiveData<Resource<User>>()
    val userRegistrationStatus: LiveData<Resource<User>> = _userRegistrationStatus

    // The Sign-Up function.
    // Checks input validation and sends the result of the registration to the fragment.
    fun createUser(email: String, password: String) {
        // Input validation
        val error = if(email.isEmpty() || password.isEmpty())
            "Empty email or password"
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Not a valid email"
        }else null
        error?.let {
            _userRegistrationStatus.postValue(Resource.error(it))
        }
        // In case there was not any error in the previous steps - continue to the registration itself:
        _userRegistrationStatus.postValue(Resource.loading())
        viewModelScope.launch{
            val registrationResult = repository.createUser(email,password)
            _userRegistrationStatus.postValue(registrationResult)
        }
    }
}
