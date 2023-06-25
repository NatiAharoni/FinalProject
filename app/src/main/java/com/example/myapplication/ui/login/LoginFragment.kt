package com.example.myapplication.ui.login

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
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

// Handling the UI of the Login fragment.
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding : FragmentLoginBinding by autoCleared()
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)

        // In case the user does NOT have an account - go to REGISTRATION
        binding.noAcountTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Passing the entered values by the user to the ViewModel
        binding.buttonLogin.setOnClickListener {
            viewModel.signInUser(binding.editTextLoginEmail.editText?.text.toString(),
                binding.editTextLoginPass.editText?.text.toString())
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observing the user logging status and acting according to the status.
        viewModel.userSignInStatus.observe(viewLifecycleOwner) {

            when(it.status) {
                is Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }
                is Success -> {
                    Toast.makeText(requireContext(),getString(R.string.LoginSuccess), Toast.LENGTH_SHORT).show()
                    // Check if the user is admin. If so - go to ALL MOVIES fragment.
                    if (isAdmin(it.status.data!!.email))
                        findNavController().navigate(R.id.action_loginFragment_to_allMoviesFragment)
                    // Check if the user is NOT admin. If so - go to all USER movies fragment.
                    else
                        findNavController().navigate(R.id.action_loginFragment_to_allUserMoviesFragment, bundleOf("email" to it.status.data!!.email))                }
                is Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// Simple function to check if the user trying to enter is admin or not.
// USed in the observer of the userSignInStatus
private fun isAdmin (email:String?):Boolean{
    return email == "admin@gmail.com"
}