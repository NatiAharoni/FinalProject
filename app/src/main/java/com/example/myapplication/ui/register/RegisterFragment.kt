package com.example.myapplication.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.repository.firebaseImpl.AuthRepositoryFirebase
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
// Handling the UI part of the Registration fragment.
@AndroidEntryPoint
class RegisterFragment : Fragment(){

    private var binding : FragmentRegisterBinding by autoCleared()
    private val viewModel : RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        // Passing the entered values by the user to the ViewModel
        binding.userRegisterButton.setOnClickListener {

            viewModel.createUser(binding.edxtEmailAddress.editText?.text.toString(),
                binding.edxtPassword.editText?.text.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observing on the registration status and acting according to the status
        viewModel.userRegistrationStatus.observe(viewLifecycleOwner) {

            when (it.status) {
                is Loading -> {
                    binding.registerProgress.isVisible = true
                    binding.userRegisterButton.isEnabled = false
                }
                is Success -> {
                    Toast.makeText(requireContext(),getString(R.string.RegisterSuccess), Toast.LENGTH_SHORT).show()
                    // Check if the user is admin. If so - go to ALL MOVIES fragment.
                    if (isAdmin(it.status.data!!.email))
                        findNavController().navigate(R.id.action_registerFragment_to_allMoviesFragment)
                    // Check if the user is NOT admin. If so - go to all USER movies fragment.
                    else
                        findNavController().navigate(R.id.action_registerFragment_to_allUserMoviesFragment, bundleOf("email" to it.status.data!!.email))
                }
                is Error -> {
                    binding.registerProgress.isVisible = false
                    binding.userRegisterButton.isEnabled = true
                    Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
// Simple function to check if the user trying to enter is admin or not.
// Used in the observer of the userSignInStatus
private fun isAdmin (email:String?):Boolean{
    return email == "admin@gmail.com"
}

